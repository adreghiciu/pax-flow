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

package org.ops4j.pax.flow.api.base;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class Value
{

    private final String m_value;

    public Value( final String value )
    {
        // VALIDATE
        m_value = value;
    }

    public String stringValue()
    {
        return m_value;
    }

    @Override
    public boolean equals( final Object o )
    {
        if( o == null )
        {
            return false;
        }
        if( !( this.getClass().isAssignableFrom( o.getClass() ) ) )
        {
            return false;
        }
        if( this == o )
        {
            return true;
        }

        final Value value = (Value) o;

        if( !m_value.equals( value.m_value ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return m_value.hashCode();
    }

    @Override
    public String toString()
    {
        return stringValue();
    }

}