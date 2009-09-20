package org.ops4j.pax.flow.recipes.action;

import org.ops4j.pax.flow.api.Action;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * Copy a property (source), from {@link ExecutionContext} to another property (target).
 *
 * @author Alin Dreghiciu
 */
public class CopyProperty<T>
    implements Action
{

    private final PropertyName m_source;
    private final PropertyName m_target;
    private final Class<T> m_propertyType;

    public CopyProperty( final PropertyName source,
                         final PropertyName target,
                         final Class<T> propertyType )
    {
        // VALIDATE
        m_source = source;
        m_target = target;
        m_propertyType = propertyType;
    }

    public void execute( final ExecutionContext context )
        throws Exception
    {
        final T source = typedExecutionContext( context ).mandatory( m_source, m_propertyType );
        context.set( m_target, source );
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
        return String.format( "Copy property [%s] -> [%s]", m_source, m_target );
    }
    
}