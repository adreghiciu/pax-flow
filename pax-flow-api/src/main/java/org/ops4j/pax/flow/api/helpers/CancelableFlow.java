package org.ops4j.pax.flow.api.helpers;

import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowName;
import static org.ops4j.pax.flow.api.FlowName.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public abstract class CancelableFlow
    implements Flow
{

    private Thread m_thread;

    private final FlowName FLOW_NAME = flowName( this.getClass().getSimpleName() );

    public FlowName name()
    {
        return FLOW_NAME;
    }

    public void execute( final ExecutionContext context )
        throws Exception
    {
        m_thread = new Thread(
            new Runnable()
            {

                public void run()
                {
                    try
                    {
                        CancelableFlow.this.run( context );
                    }
                    catch( RuntimeException e )
                    {
                        throw e;
                    }
                    catch( Exception e )
                    {
                        throw new CarryException( e );
                    }
                }
            }
        );
        try
        {
            m_thread.start();
            m_thread.join();
        }
        catch( InterruptedException ignore )
        {
            // ignore
        }
        catch( CarryException e )
        {
            throw (Exception) e.getCause();
        }
    }

    public void cancel()
        throws Exception
    {
        if( m_thread != null )
        {
            m_thread.interrupt();
        }
    }

    protected abstract void run( ExecutionContext context )
        throws Exception;

    private static class CarryException
        extends RuntimeException
    {

        private CarryException( final Exception exception )
        {
            super( exception );
        }
    }


}