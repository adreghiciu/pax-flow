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

    private Thread thread;

    private FlowName name;

    public CancelableFlow()
    {
        name = flowName( this.getClass().getSimpleName() );
    }

    public CancelableFlow( final FlowName name )
    {
        this.name = name;
    }

    public FlowName name()
    {
        return name;
    }

    public void execute( final ExecutionContext context )
        throws Exception
    {
        thread = new Thread(
            new Runnable()
            {

                public void run()
                {
                    try
                    {
                        CancelableFlow.this.run( context );
                    }
                    catch( Exception e )
                    {
                        throw new CarryException( e );
                    }
                }
            }
        );
        final ExceptionCaptor captor = new ExceptionCaptor();
        try
        {
            thread.setName( name().value() );
            thread.setUncaughtExceptionHandler( captor );
            thread.start();
            thread.join();
        }
        catch( InterruptedException ignore )
        {
            // ignore
        }
        finally
        {
            if( captor.throwable != null )
            {
                if( captor.throwable instanceof CarryException )
                {
                    throw (Exception) captor.throwable.getCause();
                }
                else
                {
                    throw new RuntimeException( captor.throwable );
                }
            }

        }
    }

    public void cancel()
        throws Exception
    {
        if( thread != null && thread.isAlive() )
        {
            thread.interrupt();
            thread = null;
        }
    }

    @Override
    public String toString()
    {
        return name().value();
    }

    protected abstract void run( ExecutionContext context )
        throws Exception;

    private static class ExceptionCaptor
        implements Thread.UncaughtExceptionHandler
    {

        Throwable throwable;

        public void uncaughtException( final Thread thread, final Throwable throwable )
        {
            this.throwable = throwable;
        }

    }

    private static class CarryException
        extends RuntimeException
    {

        private CarryException( final Throwable throwable )
        {
            super( throwable );
        }
    }

}