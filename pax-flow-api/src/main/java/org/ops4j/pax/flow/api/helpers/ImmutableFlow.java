package org.ops4j.pax.flow.api.helpers;

import static java.lang.String.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.Action;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ImmutableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( ImmutableFlow.class );

    private final FlowName m_flowName;

    private final Action[] m_actions;

    private Thread m_thread;

    public ImmutableFlow( final FlowName flowName,
                          final Action... actions )
    {
        // VALIDATE
        m_flowName = flowName;
        if( actions == null )
        {
            m_actions = new Action[0];
        }
        else
        {
            m_actions = actions;
        }
    }

    public FlowName name()
    {
        return m_flowName;
    }

    public void execute( final ExecutionContext context )
    {
        // TODO handle concurrency
        m_thread = new Thread(
            new Runnable()
            {
                public void run()
                {
                    final long startTime = System.currentTimeMillis();
                    LOG.info( format( "Executing flow [%s]", name() ) );

                    try
                    {
                        for( Action action : m_actions )
                        {
                            final long duration = System.currentTimeMillis() - startTime;
                            LOG.trace(
                                format(
                                    "Executing action [%s:%s] after %d millis from start", name(), action, duration
                                )
                            );
                            action.execute( context );
                        }
                    }
                    catch( Exception e )
                    {
                        final long duration = System.currentTimeMillis() - startTime;
                        LOG.warn( format( "Execution of flow [%s] failed after %d millis ", name(), duration ), e );
                    }
                    final long duration = System.currentTimeMillis() - startTime;
                    LOG.info( format( "Flow [%s] executed successfully in %d millis", name(), duration ) );
                }
            }
        );
        m_thread.start();
    }

    public void cancel()
    {
        if( m_thread != null && m_thread.isAlive() )
        {
            m_thread.interrupt();
        }
    }
}
