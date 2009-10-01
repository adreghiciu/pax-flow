package org.ops4j.pax.flow.recipes.flow.cm;

import java.io.File;
import static java.lang.String.*;
import java.util.HashMap;
import java.util.Map;
import com.google.inject.Inject;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowName;
import static org.ops4j.pax.flow.api.FlowName.*;
import org.ops4j.pax.flow.api.FlowType;
import static org.ops4j.pax.flow.api.FlowType.*;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
import org.ops4j.pax.flow.api.helpers.ForEachFlow;
import org.ops4j.pax.flow.api.helpers.SequentialFlow;
import org.ops4j.pax.flow.api.helpers.TypedConfiguration;
import static org.ops4j.pax.flow.api.helpers.TypedConfiguration.*;
import org.ops4j.pax.flow.recipes.flow.basic.ListDirectory;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ScanDirectoryForConfigurations
    extends SequentialFlow
    implements Flow
{

    public ScanDirectoryForConfigurations( final FlowName flowName,
                                           final ConfigurationAdmin configurationAdmin,
                                           final File directory,
                                           final String[] includes,
                                           final String[] excludes )
    {
        super(
            flowName,
            new ListDirectory( directory, includes, excludes ),
            new ForEachFlow(
                ListDirectory.ADDED_FILES, ParsePropertiesFileAsConfiguration.FILE,
                new SequentialFlow(
                    flowName( format( "%s::%s", flowName, "Added" ) ), // TODO do we need a name?
                    new DeterminePidFromFileName(
                        ParsePropertiesFileAsConfiguration.FILE,
                        AddConfiguration.PID,
                        AddConfiguration.FACTORY_PID
                    ),
                    new ParsePropertiesFileAsConfiguration(),
                    new AddConfiguration( configurationAdmin )
                )
            ),
            new ForEachFlow(
                ListDirectory.MODIFIED_FILES, ParsePropertiesFileAsConfiguration.FILE,
                new SequentialFlow(
                    flowName( format( "%s::%s", flowName, "Modified" ) ), // TODO do we need a name?
                    new DeterminePidFromFileName(
                        ParsePropertiesFileAsConfiguration.FILE,
                        UpdateConfiguration.PID,
                        UpdateConfiguration.FACTORY_PID
                    ),
                    new ParsePropertiesFileAsConfiguration(),
                    new UpdateConfiguration( configurationAdmin )
                )
            ),
            new ForEachFlow(
                ListDirectory.DELETED_FILES, ParsePropertiesFileAsConfiguration.FILE,
                new SequentialFlow(
                    flowName( format( "%s::%s", flowName, "Deleted" ) ), // TODO do we need a name?
                    new DeterminePidFromFileName(
                        ParsePropertiesFileAsConfiguration.FILE,
                        DeleteConfiguration.PID,
                        DeleteConfiguration.FACTORY_PID
                    ),
                    new DeleteConfiguration( configurationAdmin )
                )
            )
        );
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( ScanDirectoryForConfigurations.class );

        public static final PropertyName DIRECTORY = propertyName( "directory" );
        public static final PropertyName INCLUDES = propertyName( "includes" );
        public static final PropertyName EXCLUDES = propertyName( "excludes" );

        private final ConfigurationAdmin m_configurationAdmin;

        private long m_counter;

        @Inject
        public Factory( final ConfigurationAdmin configurationAdmin )
        {
            // VALIDATE
            m_configurationAdmin = configurationAdmin;
        }

        public FlowType type()
        {
            return TYPE;
        }

        public ScanDirectoryForConfigurations create( final Configuration configuration )
        {
            final TypedConfiguration cfg = typedConfiguration( configuration );

            return new ScanDirectoryForConfigurations(
                flowName( format( "%s::%d", type(), m_counter++ ) ),
                m_configurationAdmin,
                new File( cfg.mandatory( DIRECTORY, String.class ) ),
                cfg.optional( INCLUDES, String[].class ),
                cfg.optional( EXCLUDES, String[].class )
            );
        }

        @Override
        public String toString()
        {
            return format( "Flow factory for type [%s] (%d instances)", type(), m_counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "flowType", TYPE.toString() );

            return attributes;
        }

    }

}