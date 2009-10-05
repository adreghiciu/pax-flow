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
public class JobName
{

    private final String name;

    public JobName( final String name )
    {
        // VALIDATE
        this.name = name;
    }

    public String value()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( !( o instanceof JobName ) )
        {
            return false;
        }

        final JobName that = (JobName) o;

        return name.equals( that.name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return value();
    }

    public static JobName jobName( final String name )
    {
        return new JobName( name );
    }

}