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

package org.ops4j.pax.flow.recipes.flow.cm;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import com.google.inject.Inject;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowName;
import static org.ops4j.pax.flow.api.FlowName.*;
import org.ops4j.pax.flow.api.FlowType;
import static org.ops4j.pax.flow.api.FlowType.*;
import org.ops4j.pax.flow.api.helpers.SequentialFlow;
import org.ops4j.pax.flow.api.helpers.SwitchFlow;
import static org.ops4j.pax.flow.api.helpers.SwitchFlow.SwitchCase.*;
import org.ops4j.pax.flow.recipes.flow.basic.CopyProperty;
import org.ops4j.pax.flow.recipes.trigger.ServiceWatcher;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class WatchRegistryForConfigurations
    extends SequentialFlow
    implements Flow
{

    public WatchRegistryForConfigurations( final FlowName flowName,
                                           final ConfigurationAdmin configurationAdmin )
    {
        super(
            flowName,
            new SwitchFlow(
                ServiceWatcher.EVENT,
                switchCase(
                    ServiceWatcher.ADDED,
                    new SequentialFlow(
                        new CopyProperty<Dictionary>(
                            ServiceWatcher.SERVICE, AddConfiguration.CONFIGURATION, Dictionary.class
                        ),
                        new DeterminePidFromMap(
                            ServiceWatcher.ATTRIBUTES,
                            "service.pid",
                            "service.factoryPid",
                            AddConfiguration.PID,
                            AddConfiguration.FACTORY_PID
                        ),
                        new AddConfiguration( configurationAdmin )
                    )
                ),
                switchCase(
                    ServiceWatcher.REMOVED,
                    new SequentialFlow(
                        new CopyProperty<Dictionary>(
                            ServiceWatcher.SERVICE, UpdateConfiguration.CONFIGURATION, Dictionary.class
                        ),
                        new DeterminePidFromMap(
                            ServiceWatcher.ATTRIBUTES,
                            "service.pid",
                            "service.factoryPid",
                            DeleteConfiguration.PID,
                            DeleteConfiguration.FACTORY_PID
                        ),
                        new DeleteConfiguration( configurationAdmin )
                    )
                )
            )
        );
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( WatchRegistryForConfigurations.class );

        private final ConfigurationAdmin configurationAdmin;

        private long counter;

        @Inject
        public Factory( final ConfigurationAdmin configurationAdmin )
        {
            // VALIDATE
            this.configurationAdmin = configurationAdmin;
        }

        public FlowType type()
        {
            return TYPE;
        }

        public WatchRegistryForConfigurations create( final Configuration configuration )
        {
            return new WatchRegistryForConfigurations(
                flowName( String.format( "%s::%d", type(), counter++ ) ),
                configurationAdmin
            );
        }

        @Override
        public String toString()
        {
            return String.format( "Flow factory for type [%s] (%d instances)", type(), counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "flowType", TYPE.toString() );

            return attributes;
        }

    }

}