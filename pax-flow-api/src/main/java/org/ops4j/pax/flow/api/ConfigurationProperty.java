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

package org.ops4j.pax.flow.api;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ConfigurationProperty<T>
{

    private final PropertyName name;
    private final T value;

    public ConfigurationProperty( final PropertyName name,
                                  final T value )
    {
        this.name = name;
        this.value = value;
    }

    public PropertyName name()
    {
        return name;
    }

    public T value()
    {
        return value;
    }

    @Override
    public boolean equals( final Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( !( o instanceof ConfigurationProperty ) )
        {
            return false;
        }

        final ConfigurationProperty property = (ConfigurationProperty) o;

        if( !name.equals( property.name ) )
        {
            return false;
        }
        if( !value.equals( property.value ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return String.format( "%s=%s", name, value );
    }

    public static <T> ConfigurationProperty<T> configurationProperty( final PropertyName name,
                                                                      final T value )
    {
        return new ConfigurationProperty<T>( name, value );
    }

}