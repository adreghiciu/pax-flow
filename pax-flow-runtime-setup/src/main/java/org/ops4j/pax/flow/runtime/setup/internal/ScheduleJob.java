package org.ops4j.pax.flow.runtime.setup.internal;

import com.google.inject.Inject;
import org.ops4j.pax.flow.api.Action;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.JobDescription;
import org.ops4j.pax.flow.api.Transformer;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;
import static org.ops4j.pax.flow.runtime.setup.internal.Properties.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ScheduleJob
    implements Action
{

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
        final JobDescription description = typedExecutionContext( context ).mandatory( SERVICE, JobDescription.class );
        m_transformer.schedule( description );
    }

    public void cancel()
        throws Exception
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }


}
