package org.ops4j.pax.flow.api.helpers;

import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.PropertyName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class TypedExecutionContext
{

    private final ExecutionContext m_context;

    private TypedExecutionContext( final ExecutionContext context )
    {
        // VALIDATE
        m_context = context;
    }

    public <T> T mandatory( final PropertyName propertyName,
                            final Class<T> propertyType )
    {
        // VALIDATE property name / type
        final Object value = m_context.get( propertyName );
        if( value == null )
        {
            throw new IllegalStateException(
                String.format( "Property [%s] must be specified (cannot be null)", propertyName )
            );
        }
        if( !propertyType.isAssignableFrom( value.getClass() ) )
        {
            throw new IllegalStateException(
                String.format( "Property [%s] must be of type [%s].", propertyName, propertyType.getName() )
            );
        }

        return (T) value;
    }

    public <T> T optional( final PropertyName propertyName,
                           final Class<T> propertyType )
    {
        // VALIDATE property name / type
        final Object value = m_context.get( propertyName );
        if( value != null && !propertyType.isAssignableFrom( value.getClass() ) )
        {
            throw new IllegalStateException(
                String.format( "Property [%s] must be of type [%s].", propertyName, propertyType.getName() )
            );
        }

        return (T) value;
    }

    public static TypedExecutionContext typedExecutionContext( final ExecutionContext context )
    {
        return new TypedExecutionContext( context );
    }

}