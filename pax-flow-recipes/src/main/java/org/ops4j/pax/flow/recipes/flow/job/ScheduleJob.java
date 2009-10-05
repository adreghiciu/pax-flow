package org.ops4j.pax.flow.recipes.flow.job;

import com.google.inject.Inject;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.JobDescription;
import org.ops4j.pax.flow.api.PropertyName;
import org.ops4j.pax.flow.api.Scheduler;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * Takes a job description from {@link ExecutionContext} and schedules it.
 *
 * @author Alin Dreghiciu
 */
public class ScheduleJob
    extends CancelableFlow
    implements Flow
{

    public static final PropertyName JOB_DESCRIPTION = PropertyName.propertyName( "jobDescription" );

    private final Scheduler m_scheduler;

    @Inject
    public ScheduleJob( final Scheduler scheduler )
    {
        // VALIDATE
        m_scheduler = scheduler;
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final JobDescription description = typedExecutionContext( context ).mandatory(
            JOB_DESCRIPTION, JobDescription.class
        );
        m_scheduler.schedule( description );
    }

    @Override
    public String toString()
    {
        return "Schedule job available in execution context";
    }
}
