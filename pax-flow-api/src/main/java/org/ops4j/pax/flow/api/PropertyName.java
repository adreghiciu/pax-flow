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
public class PropertyName
{

    private final String m_name;

    public PropertyName( final String name )
    {
        // VALIDATE
        m_name = name;
    }

    public String value()
    {
        return m_name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( !( o instanceof PropertyName ) )
        {
            return false;
        }

        final PropertyName that = (PropertyName) o;

        return m_name.equals( that.m_name );
    }

    @Override
    public int hashCode()
    {
        return m_name.hashCode();
    }

    @Override
    public String toString()
    {
        return value();
    }

    public static PropertyName propertyName( final String name )
    {
        return new PropertyName( name );
    }

}