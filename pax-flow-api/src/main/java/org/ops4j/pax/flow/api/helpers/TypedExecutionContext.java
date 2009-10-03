package org.ops4j.pax.flow.api.helpers;

import static java.lang.String.*;
import org.osgi.service.blueprint.container.Converter;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.swissbox.converter.GenericType.*;
import static org.ops4j.pax.swissbox.converter.JavaConverter.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class TypedExecutionContext
{

    private final ExecutionContext m_context;
    private final Converter converter;

    private TypedExecutionContext( final ExecutionContext context )
    {
        // VALIDATE
        m_context = context;
        // TODO shall we get the converter as input?
        converter = javaConverter();
    }

    public <T> T mandatory( final PropertyName propertyName,
                            final Class<T> propertyType )
    {
        // VALIDATE property name / type
        Object value = m_context.get( propertyName );

        if( value == null )
        {
            throw new IllegalStateException(
                format( "Property [%s] must be specified (cannot be null)", propertyName )
            );
        }

        if( !propertyType.isAssignableFrom( value.getClass() ) )
        {
            try
            {
                value = converter.convert( value, genericType( propertyType ) );
            }
            catch( Exception e )
            {
                throw new IllegalStateException(
                    format(
                        "Property [%s] with value [%s] cannot be converted to type [%s].",
                        propertyName, value, propertyType.getName()
                    )
                );
            }
            if( value != null && !propertyType.isAssignableFrom( value.getClass() ) )
            {
                throw new IllegalStateException(
                    format(
                        "Property [%s] with value [%s] must be of type [%s].",
                        propertyName, value, propertyType.getName()
                    )
                );
            }
        }

        return (T) value;
    }

    public <T> T optional( final PropertyName propertyName,
                           final Class<T> propertyType )
    {
        return optional( propertyName, propertyType, null );
    }

    public <T> T optional( final PropertyName propertyName,
                           final Class<T> propertyType,
                           final T defaultValue )
    {
        // VALIDATE property name / type
        Object value = m_context.get( propertyName, defaultValue );

        if( value != null && !propertyType.isAssignableFrom( value.getClass() ) )
        {
            try
            {
                value = converter.convert( value, genericType( propertyType ) );
            }
            catch( Exception e )
            {
                throw new IllegalStateException(
                    format(
                        "Property [%s] with value [%s] cannot be converted to type [%s].",
                        propertyName, value, propertyType.getName()
                    )
                );
            }
            if( value != null && !propertyType.isAssignableFrom( value.getClass() ) )
            {
                throw new IllegalStateException(
                    format( "Property [%s] must be of type [%s].", propertyName, propertyType.getName() )
                );
            }
        }

        return (T) value;
    }

    public static TypedExecutionContext typedExecutionContext( final ExecutionContext context )
    {
        return new TypedExecutionContext( context );
    }

}