package org.ops4j.pax.flow.api.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
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
        Object value = m_context.get( propertyName );
        if( value == null )
        {
            throw new IllegalStateException(
                String.format( "Property [%s] must be specified (cannot be null)", propertyName )
            );
        }
        if( !propertyType.isAssignableFrom( value.getClass() ) )
        {
            value = convert( value, propertyType );
            if( value != null && !propertyType.isAssignableFrom( value.getClass() ) )
            {
                throw new IllegalStateException(
                    String.format( "Property [%s] must be of type [%s].", propertyName, propertyType.getName() )
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
            value = convert( value, propertyType );
            if( value != null && !propertyType.isAssignableFrom( value.getClass() ) )
            {
                throw new IllegalStateException(
                    String.format( "Property [%s] must be of type [%s].", propertyName, propertyType.getName() )
                );
            }
        }

        return (T) value;
    }

    public Object convert( final Object value,
                           final Class<?> propertyType )
    {
        // TODO Use a converter API? Peter K. was talking about an unified one for OSGi. Or Java standard one?
        try
        {
            if( value instanceof File )
            {
                if( propertyType.isAssignableFrom( URI.class ) )
                {
                    return ( (File) value ).toURI();
                }
                else if( propertyType.isAssignableFrom( URL.class ) )
                {
                    return convert( ( (File) value ).toURI(), URL.class );
                }
                else if( propertyType.isAssignableFrom( InputStream.class ) )
                {
                    return new FileInputStream( (File) value );
                }
                else if( propertyType.isAssignableFrom( OutputStream.class ) )
                {
                    return new FileOutputStream( (File) value );
                }
                else if( propertyType.isAssignableFrom( String.class ) )
                {
                    return ( (File) value ).getName();
                }
            }
            else if( value instanceof URL )
            {
                if( propertyType.isAssignableFrom( URI.class ) )
                {
                    return ( (URL) value ).toURI();
                }
                else if( propertyType.isAssignableFrom( InputStream.class ) )
                {
                    return ( (URL) value ).openStream();
                }
                else if( propertyType.isAssignableFrom( String.class ) )
                {
                    return ( (URL) value ).toExternalForm();
                }
            }
            else if( value instanceof URI )
            {
                if( propertyType.isAssignableFrom( URL.class ) )
                {
                    return ( (URI) value ).toURL();
                }
                else if( propertyType.isAssignableFrom( InputStream.class ) )
                {
                    return convert( ( (URI) value ).toURL(), propertyType );
                }
                else if( propertyType.isAssignableFrom( String.class ) )
                {
                    return ( (URI) value ).toASCIIString();
                }
            }

            if( propertyType.isAssignableFrom( String.class ) )
            {
                return value.toString();
            }
        }
        catch( Exception ignore )
        {
            // ignore
        }
        return null;
    }

    public static TypedExecutionContext typedExecutionContext( final ExecutionContext context )
    {
        return new TypedExecutionContext( context );
    }

}