package org.ops4j.pax.flow.recipes.flow;

import com.google.inject.Inject;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.JobDescription;
import org.ops4j.pax.flow.api.PropertyName;
import org.ops4j.pax.flow.api.Transformer;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;

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

    private final Transformer m_transformer;

    @Inject
    public ScheduleJob( final Transformer transformer )
    {
        // VALIDATE
        m_transformer = transformer;
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final JobDescription description = typedExecutionContext( context ).mandatory(
            JOB_DESCRIPTION, JobDescription.class
        );
        m_transformer.schedule( description );
    }

    @Override
    public String toString()
    {
        return "Schedule job available in execution context";
    }
}
