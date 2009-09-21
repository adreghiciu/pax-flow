package org.ops4j.pax.flow.api.helpers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowName;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ForEachFlow
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( ForEachFlow.class );

    private final Flow m_iterableFlow;
    private final PropertyName m_iterablePropertyName;
    private final PropertyName m_iteratorPropertyName;

    private Thread m_thread;

    public ForEachFlow( final Flow iterableFlow,
                        final PropertyName iterablePropertyName,
                        final PropertyName iteratorPropertyName )
    {
        super();
        // VALIDATE
        m_iterableFlow = iterableFlow;
        m_iterablePropertyName = iterablePropertyName;
        m_iteratorPropertyName = iteratorPropertyName;
    }

    public ForEachFlow( final FlowName flowName,
                        final Flow iterableFlow,
                        final PropertyName iterablePropertyName,
                        final PropertyName iteratorPropertyName )
    {
        super( flowName );
        // VALIDATE
        m_iterableFlow = iterableFlow;
        m_iterablePropertyName = iterablePropertyName;
        m_iteratorPropertyName = iteratorPropertyName;
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final Iterable<?> iterable =
            typedExecutionContext( context ).optional( m_iterablePropertyName, Iterable.class );

        if( iterable != null )
        {
            for( Object object : iterable )
            {
                context.set( m_iteratorPropertyName, object );
                m_iterableFlow.execute( context );
            }
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