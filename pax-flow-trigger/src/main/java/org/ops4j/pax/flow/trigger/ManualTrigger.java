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

import java.util.HashMap;
import java.util.Map;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.ExecutionTarget;
import org.ops4j.pax.flow.api.Trigger;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.api.TriggerName;
import static org.ops4j.pax.flow.api.TriggerName.*;
import org.ops4j.pax.flow.api.TriggerType;
import static org.ops4j.pax.flow.api.TriggerType.*;
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
                          final ExecutionTarget target )
    {
        super( name, target );
    }

    @Override
    public ManualTrigger fire()
    {
        return super.fire();
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

        public static final TriggerType TYPE = triggerType( ManualTrigger.class );

        private long m_counter;

        public TriggerType type()
        {
            return TYPE;
        }

        public ManualTrigger create( final Configuration configuration,
                                     final ExecutionTarget target )
        {
            return new ManualTrigger(
                triggerName( String.format( "%s::%d", type(), m_counter++ ) ),
                target
            );
        }

        @Override
        public String toString()
        {
            return String.format( "Trigger factory for type [%s] (%d instances)", type(), m_counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "triggerType", TYPE.toString() );

            return attributes;
        }
        
    }

}