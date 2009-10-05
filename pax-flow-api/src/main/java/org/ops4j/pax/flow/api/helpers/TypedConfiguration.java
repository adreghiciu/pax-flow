package org.ops4j.pax.flow.api.helpers;

import static java.lang.String.*;
import org.osgi.service.blueprint.container.Converter;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.swissbox.converter.GenericType.*;
import static org.ops4j.pax.swissbox.converter.JavaConverter.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class TypedConfiguration
{

    private final Configuration configuration;
    private final Converter converter;

    private TypedConfiguration( final Configuration configuration )
    {
        // VALIDATE
        this.configuration = configuration;
        // TODO shall we get the converter as input?
        converter = javaConverter();
    }

    public <T> T mandatory( final PropertyName propertyName,
                            final Class<T> propertyType )
    {
        // VALIDATE property name / type
        Object value = configuration.get( propertyName );

        if( value == null )
        {
            throw new IllegalStateException(
                String.format( "Property [%s] must be specified (cannot be null)", propertyName )
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
        Object value = configuration.get( propertyName, defaultValue );

        if( value != null && !propertyType.isAssignableFrom( value.getClass() ) )
        {
            try
            {
                value = converter.convert( value, genericType( propertyType ) );
            }
            catch( Exception e )
            {
                return null;
            }
            if( value != null && !propertyType.isAssignableFrom( value.getClass() ) )
            {
                return null;
            }
        }

        return (T) value;
    }

    public static TypedConfiguration typedConfiguration( final Configuration configuration )
    {
        return new TypedConfiguration( configuration );
    }

}
