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

package org.ops4j.pax.flow.api.helpers;

import org.osgi.framework.BundleContext;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.helpers.ConfigurationUtils.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class FrameworkPropertiesConfiguration
    implements Configuration
{

    private final BundleContext bundleContext;

    private FrameworkPropertiesConfiguration( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    public <T> T get( final PropertyName name )
    {
        final String value = bundleContext.getProperty( name.value() );
        if( !name.value().equals( value ) )
        {
            return (T) replacePlaceholders( this, value );
        }
        return (T) value;
    }

    public <T> T get( final PropertyName name,
                      final T defaultValue )
    {
        T value = this.<T>get( name );

        if( value == null )
        {
            value = defaultValue;
            if( value instanceof String
                && !name.value().equals( value ) )
            {
                return (T) replacePlaceholders( this, (String) value );
            }
        }

        return value;
    }

    public static FrameworkPropertiesConfiguration frameworkPropertiesConfiguration( final BundleContext bundleContext )
    {
        return new FrameworkPropertiesConfiguration( bundleContext );
    }

}