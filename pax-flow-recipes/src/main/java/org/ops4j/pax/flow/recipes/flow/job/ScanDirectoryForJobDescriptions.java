package org.ops4j.pax.flow.recipes.flow.job;

import java.io.File;
import static java.lang.String.*;
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
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
import org.ops4j.pax.flow.api.Scheduler;
import org.ops4j.pax.flow.api.helpers.ForEachFlow;
import org.ops4j.pax.flow.api.helpers.SequentialFlow;
import org.ops4j.pax.flow.api.helpers.TypedConfiguration;
import static org.ops4j.pax.flow.api.helpers.TypedConfiguration.*;
import org.ops4j.pax.flow.recipes.flow.basic.ExtractFileNameFromFile;
import org.ops4j.pax.flow.recipes.flow.basic.ListDirectory;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ScanDirectoryForJobDescriptions
    extends SequentialFlow
    implements Flow
{

    public ScanDirectoryForJobDescriptions( final FlowName flowName,
                                            final Scheduler scheduler,
                                            final File directory,
                                            final String[] includes,
                                            final String[] excludes )
    {
        super(
            flowName,
            new ListDirectory( directory, includes, excludes ),
            new ForEachFlow(
                ListDirectory.NEW, ParseJsonJobDescription.FILE,
                new SequentialFlow(
                    flowName( format( "%s::%s", flowName, "New" ) ), // TODO do we need a name?
                    new ExtractFileNameFromFile( ParseJsonJobDescription.FILE, ParseJsonJobDescription.JOB_NAME ),
                    new ParseJsonJobDescription(),
                    new ScheduleJob( scheduler )
                )
            ),
            new ForEachFlow(
                ListDirectory.MODIFIED, ParseJsonJobDescription.FILE,
                new SequentialFlow(
                    flowName( format( "%s::%s", flowName, "Modified" ) ), // TODO do we need a name?
                    new ExtractFileNameFromFile( ParseJsonJobDescription.FILE, ParseJsonJobDescription.JOB_NAME ),
                    new ParseJsonJobDescription(),
                    new RescheduleJob( scheduler )
                )
            ),
            new ForEachFlow(
                ListDirectory.REMOVED, ParseJsonJobDescription.FILE,
                new SequentialFlow(
                    flowName( format( "%s::%s", flowName, "Removed" ) ), // TODO do we need a name?
                    new ExtractFileNameFromFile( ParseJsonJobDescription.FILE, UnscheduleJob.JOB_NAME ),
                    new UnscheduleJob( scheduler )
                )
            )
        );
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( ScanDirectoryForJobDescriptions.class );

        public static final PropertyName DIRECTORY = propertyName( "directory" );
        public static final PropertyName INCLUDES = propertyName( "includes" );
        public static final PropertyName EXCLUDES = propertyName( "excludes" );

        private final Scheduler scheduler;

        private long counter;

        @Inject
        public Factory( final Scheduler scheduler )
        {
            // VALIDATE
            this.scheduler = scheduler;
        }

        public FlowType type()
        {
            return TYPE;
        }

        public ScanDirectoryForJobDescriptions create( final Configuration configuration )
        {
            final TypedConfiguration cfg = typedConfiguration( configuration );

            return new ScanDirectoryForJobDescriptions(
                flowName( format( "%s::%d", type(), counter++ ) ),
                scheduler,
                new File( cfg.mandatory( DIRECTORY, String.class ) ),
                cfg.optional( INCLUDES, String[].class ),
                cfg.optional( EXCLUDES, String[].class )
            );
        }

        @Override
        public String toString()
        {
            return format( "Flow factory for type [%s] (%d instances)", type(), counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "flowType", TYPE.toString() );

            return attributes;
        }

    }

}