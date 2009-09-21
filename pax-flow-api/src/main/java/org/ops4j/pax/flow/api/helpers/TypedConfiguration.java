package org.ops4j.pax.flow.api.helpers;

import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.PropertyName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class TypedConfiguration
{

    private final Configuration m_configuration;

    private TypedConfiguration( final Configuration configuration )
    {
        // VALIDATE
        m_configuration = configuration;
    }

    public <T> T mandatory( final PropertyName propertyName,
                            final Class<T> propertyType )
    {
        // VALIDATE property name / type
        final Object value = m_configuration.get( propertyName );
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
        final Object value = m_configuration.get( propertyName );
        if( value != null && !propertyType.isAssignableFrom( value.getClass() ) )
        {
            throw new IllegalStateException(
                String.format( "Property [%s] must be of type [%s].", propertyName, propertyType.getName() )
            );
        }

        return (T) value;
    }

    public <T> T optional( final PropertyName propertyName,
                           final Class<T> propertyType,
                           final T defaultValue )
    {
        // VALIDATE property name / type
        Object value = optional( propertyName, propertyType );
        if( value == null )
        {
            value = defaultValue;
        }

        return (T) value;
    }

    public static TypedConfiguration typedConfiguration( final Configuration configuration )
    {
        return new TypedConfiguration( configuration );
    }

}
