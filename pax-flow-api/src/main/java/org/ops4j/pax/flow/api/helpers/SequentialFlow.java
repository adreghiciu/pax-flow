package org.ops4j.pax.flow.api.helpers;

import static java.lang.String.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class SequentialFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( SequentialFlow.class );

    private final FlowName m_flowName;

    private final Flow[] m_flows;

    private Thread m_thread;

    public SequentialFlow( final FlowName flowName,
                          final Flow... flows )
    {
        // VALIDATE
        m_flowName = flowName;
        if( flows == null )
        {
            m_flows = new Flow[0];
        }
        else
        {
            m_flows = flows;
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
                        for( Flow flow : m_flows )
                        {
                            final long duration = System.currentTimeMillis() - startTime;
                            LOG.debug(
                                format(
                                    "Executing flow [%s:%s] after %d millis from start", name(), flow, duration
                                )
                            );
                            flow.execute( context );
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
            },
            this.toString()
        );
        try
        {
            m_thread.join();
            m_thread.start();
        }
        catch( InterruptedException ignore )
        {
            // ignore
        }
    }

    public void cancel()
    {
        if( m_thread != null && m_thread.isAlive() )
        {
            m_thread.interrupt();
        }
    }

    @Override
    public String toString()
    {
        return name().value();
    }

}
