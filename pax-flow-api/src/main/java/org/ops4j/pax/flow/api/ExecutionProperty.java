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
public class ExecutionProperty<T>
{

    private final PropertyName name;
    private final T value;
    private final boolean persistent;

    public ExecutionProperty( final PropertyName name,
                              final T value,
                              final boolean persistent )
    {
        this.name = name;
        this.value = value;
        this.persistent = persistent;
    }

    public PropertyName name()
    {
        return name;
    }

    public T value()
    {
        return value;
    }

    public boolean isPersistent()
    {
        return persistent;
    }

    @Override
    public boolean equals( final Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( !( o instanceof ExecutionProperty ) )
        {
            return false;
        }

        final ExecutionProperty property = (ExecutionProperty) o;

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
        return String.format( "%s=%s%s", name, value, persistent ? "(persistent)" : "" );
    }

    public static <T> ExecutionProperty<T> executionProperty( final PropertyName name,
                                                              final T value )
    {
        return new ExecutionProperty<T>( name, value, false );
    }

    public static <T> ExecutionProperty<T> persistentExecutionProperty( final PropertyName name,
                                                                        final T value )
    {
        return new ExecutionProperty<T>( name, value, true );
    }

}