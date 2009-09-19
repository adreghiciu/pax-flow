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
import org.ops4j.pax.flow.api.Property;
import org.ops4j.pax.flow.api.PropertyName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class DefaultExecutionContext
    implements ExecutionContext
{

    private final Map<PropertyName, Object> m_properties;

    private DefaultExecutionContext( final Property<?>... properties )
    {
        m_properties = new HashMap<PropertyName, Object>();
        if( properties != null && properties.length > 0 )
        {
            for( Property<?> property : properties )
            {
                m_properties.put( property.name(), property.value() );
            }
        }
    }

    public <T> T get( final PropertyName name )
    {
        return (T) m_properties.get( name );
    }

    public <T> T get( final PropertyName name, final T defaultValue )
    {
        final Object property = m_properties.get( name );
        if( property == null )
        {
            return defaultValue;
        }
        return (T) property;
    }

    public Iterable<PropertyName> getNames()
    {
        return Collections.unmodifiableSet( m_properties.keySet() );
    }

    public ExecutionContext set( final PropertyName name, final Object value )
    {
        m_properties.put( name, value );

        return this;
    }

    public static DefaultExecutionContext defaultExecutionContext( final Property... properties )
    {
        return new DefaultExecutionContext( properties );
    }

}
