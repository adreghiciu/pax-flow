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
public class TriggerType
{

    private final String type;

    public TriggerType( final String type )
    {
        // VALIDATE
        this.type = type;
    }

    public String value()
    {
        return type;
    }

    @Override
    public boolean equals( final Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( !( o instanceof TriggerType ) )
        {
            return false;
        }

        final TriggerType that = (TriggerType) o;

        return type.equals( that.type );
    }

    @Override
    public int hashCode()
    {
        return type.hashCode();
    }

    @Override
    public String toString()
    {
        return value();
    }

    public static TriggerType triggerType( final String type )
    {
        return new TriggerType( type );
    }

    public static TriggerType triggerType( final Class<?> type )
    {
        // VALIDATE
        String name = type.getSimpleName();
        name = name.substring( 0, 1 ).toLowerCase() + name.substring( 1 );
        return new TriggerType( name );
    }

    public static TriggerType triggerType( final Class<?> type,
                                           final String suffix)
    {
        // VALIDATE
        String name = type.getSimpleName();
        name = name.substring( 0, 1 ).toLowerCase() + name.substring( 1 );
        return new TriggerType( name + suffix );
    }

}