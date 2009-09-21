package org.ops4j.pax.flow.recipes.flow;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.google.inject.Inject;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowName;
import static org.ops4j.pax.flow.api.FlowName.*;
import org.ops4j.pax.flow.api.FlowType;
import static org.ops4j.pax.flow.api.FlowType.*;
import org.ops4j.pax.flow.api.Transformer;
import org.ops4j.pax.flow.api.helpers.ForEachFlow;
import org.ops4j.pax.flow.api.helpers.SequentialFlow;
import org.ops4j.pax.flow.api.helpers.TypedConfiguration;
import static org.ops4j.pax.flow.api.helpers.TypedConfiguration.*;
import static org.ops4j.pax.flow.recipes.internal.Properties.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ScanDirectoryForJobDescriptionsFlow
    extends SequentialFlow
    implements Flow
{

    public ScanDirectoryForJobDescriptionsFlow( final FlowName flowName,
                                                final Transformer transformer,
                                                final File directory,
                                                final String[] includes,
                                                final String[] excludes )
    {
        super(
            flowName,
            new ListDirectory( directory, includes, excludes ),
            new ForEachFlow( new ParseJsonJobDescription(), ListDirectory.FILES, ParseJsonJobDescription.FILE ),
            new ScheduleJob( transformer )
        );
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( ScanDirectoryForJobDescriptionsFlow.class );

        private final Transformer m_transformer;

        private long m_counter;

        @Inject
        public Factory( final Transformer transformer )
        {
            // VALIDATE
            m_transformer = transformer;
        }

        public FlowType type()
        {
            return TYPE;
        }

        public ScanDirectoryForJobDescriptionsFlow create( final Configuration configuration )
        {
            final TypedConfiguration cfg = typedConfiguration( configuration );

            return new ScanDirectoryForJobDescriptionsFlow(
                flowName( String.format( "%s::%d", type(), m_counter++ ) ),
                m_transformer,
                new File( cfg.mandatory( DIRECTORY, String.class ) ),
                cfg.optional( INCLUDES, String[].class ),
                cfg.optional( EXCLUDES, String[].class )
            );
        }

        @Override
        public String toString()
        {
            return String.format( "Flow factory for type [%s] (%d instances)", type(), m_counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "flowType", TYPE.toString() );

            return attributes;
        }

    }

}