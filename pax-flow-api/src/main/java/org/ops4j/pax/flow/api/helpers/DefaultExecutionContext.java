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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.ExecutionProperty;
import org.ops4j.pax.flow.api.PropertyName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class DefaultExecutionContext
    implements ExecutionContext
{

    private final Map<PropertyName, ExecutionProperty<?>> properties;

    protected DefaultExecutionContext( final ExecutionProperty<?>... properties )
    {
        this.properties = new HashMap<PropertyName, ExecutionProperty<?>>();
        if( properties != null && properties.length > 0 )
        {
            for( ExecutionProperty<?> property : properties )
            {
                this.properties.put( property.name(), property );
            }
        }
    }

    public <T> T get( final PropertyName name )
    {
        final ExecutionProperty<?> property = properties.get( name );
        if( property == null )
        {
            return null;
        }
        return (T) property.value();
    }

    public <T> T get( final PropertyName name, final T defaultValue )
    {
        // VALIDATE
        final ExecutionProperty<?> property = properties.get( name );
        if( property == null )
        {
            return defaultValue;
        }
        return (T) property.value();
    }

    public Iterable<PropertyName> names()
    {
        return Collections.unmodifiableSet( properties.keySet() );
    }

    public ExecutionContext add( final ExecutionProperty property )
    {
        // VALIDATE
        properties.put( property.name(), property );

        return this;
    }

    public static DefaultExecutionContext defaultExecutionContext( final ExecutionProperty... properties )
    {
        return new DefaultExecutionContext( properties );
    }

}
