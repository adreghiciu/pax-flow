package org.ops4j.pax.flow.recipes.flow.job;

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
import org.ops4j.pax.flow.api.JobDescription;
import org.ops4j.pax.flow.api.Scheduler;
import org.ops4j.pax.flow.api.helpers.SequentialFlow;
import org.ops4j.pax.flow.api.helpers.SwitchFlow;
import static org.ops4j.pax.flow.api.helpers.SwitchFlow.SwitchCase.*;
import org.ops4j.pax.flow.recipes.flow.basic.CopyProperty;
import org.ops4j.pax.flow.recipes.trigger.ServiceWatcher;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class WatchRegistryForJobDescriptions
    extends SequentialFlow
    implements Flow
{

    public WatchRegistryForJobDescriptions( final FlowName flowName,
                                            final Scheduler scheduler )
    {
        super(
            flowName,
            new CopyProperty<JobDescription>(
                ServiceWatcher.SERVICE, ScheduleJob.JOB_DESCRIPTION, JobDescription.class
            ),
            new SwitchFlow(
                ServiceWatcher.EVENT,
                switchCase( ServiceWatcher.NEW, new ScheduleJob( scheduler ) ),
                switchCase( ServiceWatcher.REMOVED, new UnscheduleJob( scheduler ) )
            )
        );
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( WatchRegistryForJobDescriptions.class );

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

        public WatchRegistryForJobDescriptions create( final Configuration configuration )
        {
            return new WatchRegistryForJobDescriptions(
                flowName( String.format( "%s::%d", type(), counter++ ) ),
                scheduler
            );
        }

        @Override
        public String toString()
        {
            return String.format( "Flow factory for type [%s] (%d instances)", type(), counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "flowType", TYPE.toString() );

            return attributes;
        }

    }

}
