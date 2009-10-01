package org.ops4j.pax.flow.recipes.flow.job;

import com.google.inject.Inject;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import static org.ops4j.pax.flow.api.JobName.*;
import org.ops4j.pax.flow.api.PropertyName;
import org.ops4j.pax.flow.api.Transformer;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class UnscheduleJob
    extends CancelableFlow
    implements Flow
{

    public static final PropertyName JOB_NAME = PropertyName.propertyName( "jobDescriptionName" );

    private final Transformer m_transformer;

    @Inject
    public UnscheduleJob( final Transformer transformer )
    {
        // VALIDATE
        m_transformer = transformer;
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final String jobName = typedExecutionContext( context ).mandatory( JOB_NAME, String.class );

        m_transformer.unschedule( jobName( jobName ) );
    }

    @Override
    public String toString()
    {
        return "Unschedule job available in execution context";
    }
}