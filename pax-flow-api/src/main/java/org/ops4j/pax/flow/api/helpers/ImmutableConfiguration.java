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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.ConfigurationProperty;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.helpers.ConfigurationUtils.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ImmutableConfiguration
    implements Configuration
{

    private final Map<PropertyName, ConfigurationProperty<?>> properties;
    private final Configuration fallback;

    private ImmutableConfiguration( final Configuration configuration,
                                    final ConfigurationProperty<?>... properties )
    {
        fallback = configuration;
        this.properties = new HashMap<PropertyName, ConfigurationProperty<?>>();
        if( properties != null && properties.length > 0 )
        {
            for( ConfigurationProperty<?> property : properties )
            {
                this.properties.put( property.name(), property );
            }
        }
    }

    public <T> T get( final PropertyName name )
    {
        ConfigurationProperty<?> property = properties.get( name );
        if( property == null )
        {
            if( fallback != null )
            {
                return fallback.<T>get( name );
            }
            return null;
        }
        final Object value = property.value();

        if( value instanceof String
            && !name.value().equals( value ) )
        {
            return (T) replacePlaceholders( this, (String) value );
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

    public static ImmutableConfiguration immutableConfiguration( final ConfigurationProperty<?>... properties )
    {
        return immutableConfiguration( null, properties );
    }

    public static ImmutableConfiguration immutableConfiguration( final Configuration configuration,
                                                                 final ConfigurationProperty<?>... properties )
    {
        return new ImmutableConfiguration( configuration, properties );
    }

    public static ImmutableConfiguration immutableConfiguration( final Collection<ConfigurationProperty<?>> properties )
    {
        return immutableConfiguration( null, properties );
    }

    public static ImmutableConfiguration immutableConfiguration( final Configuration configuration,
                                                                 final Collection<ConfigurationProperty<?>> properties )
    {
        if( properties == null )
        {
            return new ImmutableConfiguration( configuration, (ConfigurationProperty<?>[]) null );
        }
        return new ImmutableConfiguration(
            configuration,
            properties.toArray( new ConfigurationProperty<?>[properties.size()] )
        );
    }

    public static ImmutableConfiguration withoutConfiguration()
    {
        return new ImmutableConfiguration( null );
    }

}