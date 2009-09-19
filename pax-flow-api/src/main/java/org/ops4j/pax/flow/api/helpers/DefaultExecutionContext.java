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
import org.ops4j.pax.flow.api.AttributeName;
import org.ops4j.pax.flow.api.ExecutionContext;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class DefaultExecutionContext
    implements ExecutionContext
{

    private final Map<AttributeName, Object> m_attributes;

    public DefaultExecutionContext()
    {
        m_attributes = new HashMap<AttributeName, Object>();
    }

    public <T> T get( final AttributeName name )
    {
        return (T) m_attributes.get( name );
    }

    public <T> T get( final AttributeName name, final T defaultValue )
    {
        final Object attribute = m_attributes.get( name );
        if( attribute == null )
        {
            return defaultValue;
        }
        return (T) attribute;
    }

    public Iterable<AttributeName> getNames()
    {
        return Collections.unmodifiableSet( m_attributes.keySet() );
    }

    public ExecutionContext set( final AttributeName name, final Object value )
    {
        m_attributes.put( name, value );
        
        return this;
    }

}