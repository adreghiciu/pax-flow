/*
 * Copyright 2009 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.swissbox.converter.java.io;

import java.io.File;
import java.net.URI;
import java.net.URL;
import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
import static org.ops4j.pax.swissbox.converter.java.lang.AssignableConverter.*;
import static org.ops4j.pax.swissbox.converter.java.lang.FromNullConverter.*;
import static org.ops4j.pax.swissbox.converter.internal.Primitives.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 */
public class ToURLConverter
    implements Converter
{

    public static final ToURLConverter INSTANCE = new ToURLConverter();

    private final Converter escape;

    public ToURLConverter()
    {
        this.escape = null;
    }

    public ToURLConverter( final Converter escape )
    {
        this.escape = escape;
    }

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        return fromNullConverter().canConvert( sourceObject, targetType )
               || assignableConverter().canConvert( sourceObject, targetType )
               || ( URL.class.isAssignableFrom( unwrap( targetType.getRawClass() ) )
                    && ( sourceObject instanceof URI
                         || sourceObject instanceof File
                         || sourceObject instanceof CharSequence
                         || ( escape != null
                              && ( escape.canConvert( sourceObject, new ReifiedType( URI.class ) )
                                   || escape.canConvert( sourceObject, new ReifiedType( File.class ) ) ) ) ) );
    }

    public Object convert( final Object sourceObject,
                           final ReifiedType targetType )
        throws Exception
    {
        if( !canConvert( sourceObject, targetType ) )
        {
            throw new Exception(
                String.format(
                    "%s cannot convert an %s", ToURLConverter.class.getSimpleName(), sourceObject.getClass()
                )
            );
        }

        if( fromNullConverter().canConvert( sourceObject, targetType ) )
        {
            return fromNullConverter().convert( sourceObject, targetType );
        }

        if( assignableConverter().canConvert( sourceObject, targetType ) )
        {
            return assignableConverter().convert( sourceObject, targetType );
        }

        return convertToURL( sourceObject, targetType );
    }

    public URL convertToURL( final Object sourceObject,
                             final ReifiedType targetType )
        throws Exception
    {
        if( sourceObject instanceof URI )
        {
            return ( (URI) sourceObject ).toURL();
        }

        if( sourceObject instanceof File )
        {
            return ( (File) sourceObject ).toURI().toURL();
        }

        if( sourceObject instanceof CharSequence )
        {
            return new URL( sourceObject.toString() );
        }

        if( escape != null )
        {
            if( escape.canConvert( sourceObject, new ReifiedType( URI.class ) ) )
            {
                try
                {
                    return ( (URI) escape.convert( sourceObject, new ReifiedType( URI.class ) ) ).toURL();
                }
                catch( Exception ignore )
                {
                    // ignore
                }
            }

            if( escape.canConvert( sourceObject, new ReifiedType( File.class ) ) )
            {
                try
                {
                    return ( (File) escape.convert( sourceObject, new ReifiedType( File.class ) ) ).toURI().toURL();
                }
                catch( Exception ignore )
                {
                    // ignore
                }
            }
        }

        throw new Exception( String.format( "Unable to convert number %s to %s", sourceObject, targetType ) );
    }

    public static ToURLConverter toURLConverter()
    {
        return INSTANCE;
    }

    public static ToURLConverter toURLConverter( final Converter escape )
    {
        return new ToURLConverter( escape );
    }

}