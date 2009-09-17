package org.ops4j.pax.flow.api.helpers;

import org.ops4j.pax.flow.api.AttributeName;
import org.ops4j.pax.flow.api.Configuration;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class TypedConfiguration
{

    private final Configuration m_configuration;

    public TypedConfiguration( final Configuration configuration )
    {
        // VALIDATE
        m_configuration = configuration;
    }

    public <T> T mandatory( final AttributeName attributeName,
                            final Class<T> attributeType )
    {
        // VALIDATE attribute name / type
        final Object value = m_configuration.get( attributeName );
        if( value == null )
        {
            throw new IllegalStateException(
                String.format( "Attribute [%s] must be specified (cannot be null)", attributeName )
            );
        }
        if( !attributeType.isAssignableFrom( value.getClass() ) )
        {
            throw new IllegalStateException(
                String.format( "Attribute [%s] must be of type [%s].", attributeName, attributeType.getName() )
            );
        }

        return (T) value;
    }

    public <T> T optional( final AttributeName attributeName,
                           final Class<T> attributeType )
    {
        // VALIDATE attribute name / type
        final Object value = m_configuration.get( attributeName );
        if( value != null && !attributeType.isAssignableFrom( value.getClass() ) )
        {
            throw new IllegalStateException(
                String.format( "Attribute [%s] must be of type [%s].", attributeName, attributeType.getName() )
            );
        }

        return (T) value;
    }

}
