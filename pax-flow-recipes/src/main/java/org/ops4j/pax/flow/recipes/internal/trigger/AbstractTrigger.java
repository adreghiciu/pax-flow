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

package org.ops4j.pax.flow.recipes.internal.trigger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.ExecutionTarget;
import org.ops4j.pax.flow.api.Trigger;
import org.ops4j.pax.flow.api.TriggerName;
import static org.ops4j.pax.flow.api.helpers.DefaultExecutionContext.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public abstract class AbstractTrigger<T extends Trigger>
    implements Trigger
{

    private final Log LOG = LogFactory.getLog( this.getClass() );

    private final TriggerName name;
    private final ExecutionTarget target;

    private boolean started;

    public AbstractTrigger( final TriggerName name,
                            final ExecutionTarget target )
    {
        // VALIDATE
        this.name = name;
        this.target = target;
    }

    public TriggerName name()
    {
        return name;
    }

    public T start()
        throws Exception
    {
        started = true;

        return itself();
    }

    public T stop()
    {
        started = false;

        return itself();
    }

    protected boolean isStarted()
    {
        return started;
    }

    protected abstract T itself();

    protected T fire()
    {
        return fire( defaultExecutionContext() );
    }

    protected T fire( final ExecutionContext executionContext )
    {
        // VALIDATE executionContext
        try
        {
            LOG.debug( String.format( "[%s] fires now %2$tH:%2$tM:%2$tS:%2$tL", this, System.currentTimeMillis() ) );
            target.execute( executionContext );
        }
        catch( Throwable ignore )
        {
            // TODO ignore?
        }

        return itself();
    }
}