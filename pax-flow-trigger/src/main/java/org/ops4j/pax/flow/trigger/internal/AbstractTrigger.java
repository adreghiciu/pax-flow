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

package org.ops4j.pax.flow.trigger.internal;

import java.util.Collection;
import java.util.HashSet;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.ExecutionTarget;
import org.ops4j.pax.flow.api.Trigger;
import org.ops4j.pax.flow.api.TriggerName;
import org.ops4j.pax.flow.api.helpers.DefaultExecutionContext;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public abstract class AbstractTrigger<T extends Trigger>
    implements Trigger
{

    private final TriggerName m_name;
    private final Collection<ExecutionTarget> m_targets;

    private boolean m_started;

    public AbstractTrigger( final TriggerName name )
    {
        // VALIDATE name
        m_name = name;
        m_targets = new HashSet<ExecutionTarget>();
    }

    public TriggerName name()
    {
        return m_name;
    }

    public T start()
        throws Exception
    {
        m_started = true;

        return itself();
    }

    public T stop()
    {
        m_started = false;

        return itself();
    }

    public T attach( final ExecutionTarget target )
    {
        // VALIDATE target
        m_targets.add( target );

        return itself();
    }

    public T detach( final ExecutionTarget target )
    {
        // VALIDATE target
        m_targets.remove( target );

        return itself();
    }

    protected boolean isStarted()
    {
        return m_started;
    }

    protected abstract T itself();

    protected T fire()
    {
        return fire( new DefaultExecutionContext() );
    }

    protected T fire( final ExecutionContext executionContext )
    {
        // VALIDATE executionContext
        for( ExecutionTarget target : m_targets )
        {
            try
            {
                target.execute( executionContext );
            }
            catch( Throwable ignore )
            {
                // ignore
            }
        }

        return itself();
    }
}