package org.ops4j.pax.flow.recipes.flow;

import org.ops4j.pax.flow.api.ExecutionContext;
import static org.ops4j.pax.flow.api.ExecutionProperty.*;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.PropertyName;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * Copy a property (source), from {@link ExecutionContext} to another property (target).
 *
 * @author Alin Dreghiciu
 */
public class CopyProperty<T>
    extends CancelableFlow
    implements Flow
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

    public void run( final ExecutionContext context )
    {
        final T source = typedExecutionContext( context ).mandatory( m_source, m_propertyType );
        context.add( executionProperty( m_target, source ) );
    }

    @Override
    public String toString()
    {
        return String.format( "Copy property [%s] -> [%s]", m_source, m_target );
    }

}