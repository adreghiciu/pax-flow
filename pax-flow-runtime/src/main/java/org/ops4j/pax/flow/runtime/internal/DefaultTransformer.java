package org.ops4j.pax.flow.runtime.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import com.google.inject.Inject;
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
        implements Runnable
    {

        private final Flow m_flow;

        public FlowScheduler( final Flow flow )
        {
            m_flow = flow;
        }

        public void run()
        {
            m_executorService.submit(
                new Callable<Object>()
                {
                    public Object call()
                        throws Exception
                    {
                        m_flow.execute( new DefaultExecutionContext() );
                        return null;
                    }
                }
            );
        }
    }

}