package org.ops4j.pax.flow.runtime.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.ExecutionTarget;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.Transformer;
import org.ops4j.pax.flow.api.Trigger;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class DefaultTransformer
    implements Transformer
{

    private static final Log LOG = LogFactory.getLog( DefaultTransformer.class );
    
    private final ExecutorService m_executorService;

    @Inject
    public DefaultTransformer( final ExecutorService executorService )
    {
        m_executorService = executorService;
    }

    public void schedule( final Flow flow,
                          final Trigger trigger )
        throws Exception
    {
        // VALIDATE
        trigger.attach( new FlowScheduler( flow ) );
        trigger.start();
    }

    private class FlowScheduler
        implements ExecutionTarget
    {

        private final Flow m_flow;

        public FlowScheduler( final Flow flow )
        {
            m_flow = flow;
        }

        public void execute( final ExecutionContext executionContext )
        {
            m_executorService.submit(
                new Callable<Object>()
                {
                    public Object call()
                        throws Exception
                    {
                        m_flow.execute( executionContext );
                        return null;
                    }
                }
            );
        }
    }

}
