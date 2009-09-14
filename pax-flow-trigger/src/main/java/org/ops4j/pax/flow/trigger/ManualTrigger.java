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

package org.ops4j.pax.flow.trigger;

import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.Trigger;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.api.TriggerName;
import org.ops4j.pax.flow.trigger.internal.AbstractTrigger;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ManualTrigger
    extends AbstractTrigger<ManualTrigger>
    implements Trigger
{

    public ManualTrigger( final TriggerName name,
                          final ExecutionContext context,
                          final Flow target )
    {
        super( name, context, target );
    }

    public Trigger fire()
        throws Exception
    {
        if( isStarted() )
        {
        }
        return this;
    }

    @Override
    protected ManualTrigger itself()
    {
        return this;
    }

    /**
     * JAVADOC
     *
     * @author Alin Dreghiciu
     */
    public static class Factory
        implements TriggerFactory<ManualTrigger>
    {

        public ManualTrigger create( final TriggerName name,
                                     final ExecutionContext context,
                                     final Flow target )
        {
            return new ManualTrigger( name, context, target );
        }

    }
}