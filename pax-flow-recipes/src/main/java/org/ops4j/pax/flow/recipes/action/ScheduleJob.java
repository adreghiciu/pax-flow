package org.ops4j.pax.flow.recipes.action;

import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.Action;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.JobDescription;
import org.ops4j.pax.flow.api.PropertyName;
import org.ops4j.pax.flow.api.Transformer;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * Takes a job description from {@link ExecutionContext} and schedules it.
 *
 * @author Alin Dreghiciu
 */
public class ScheduleJob
    implements Action
{

    public static final PropertyName JOB_DESCRIPTION = PropertyName.propertyName( "jobDescription" );

    private final Transformer m_transformer;

    @Inject
    public ScheduleJob( final Transformer transformer )
    {
        // VALIDATE
        m_transformer = transformer;
    }

    public void execute( final ExecutionContext context )
        throws Exception
    {
        final JobDescription description = typedExecutionContext( context ).mandatory(
            JOB_DESCRIPTION, JobDescription.class
        );
        m_transformer.schedule( description );
    }

    public void cancel()
        throws Exception
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString()
    {
        return "Schedule job available in execution context";
    }
}