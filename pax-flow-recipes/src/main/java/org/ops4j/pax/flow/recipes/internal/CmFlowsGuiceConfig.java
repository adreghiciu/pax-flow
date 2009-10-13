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

package org.ops4j.pax.flow.recipes.internal;

import com.google.inject.AbstractModule;
import static com.google.inject.name.Names.*;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.recipes.flow.cm.ScanDirectoryForConfigurations;
import org.ops4j.pax.flow.recipes.flow.cm.WatchBundlesForConfigurations;
import org.ops4j.pax.flow.recipes.flow.cm.WatchRegistryForConfigurations;
import static org.ops4j.peaberry.Peaberry.*;
import static org.ops4j.peaberry.util.TypeLiterals.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class CmFlowsGuiceConfig
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( ConfigurationAdmin.class ).toProvider( service( ConfigurationAdmin.class ).single() );

        bind( export( FlowFactory.class ) )
            .annotatedWith( named( ScanDirectoryForConfigurations.Factory.class.getName() ) )
            .toProvider(
                service( ScanDirectoryForConfigurations.Factory.class )
                    .attributes( ScanDirectoryForConfigurations.Factory.attributes() )
                    .export()
            );

        bind( export( FlowFactory.class ) )
            .annotatedWith( named( WatchBundlesForConfigurations.Factory.class.getName() ) )
            .toProvider(
                service( WatchBundlesForConfigurations.Factory.class )
                    .attributes( WatchBundlesForConfigurations.Factory.attributes() )
                    .export()
            );

        bind( export( FlowFactory.class ) )
            .annotatedWith( named( WatchRegistryForConfigurations.Factory.class.getName() ) )
            .toProvider(
                service( WatchRegistryForConfigurations.Factory.class )
                    .attributes( WatchRegistryForConfigurations.Factory.attributes() )
                    .export()
            );

        bind( export( TriggerFactory.class ) )
            .annotatedWith( named( WatchRegistryForConfigurations.DefaultTriggerFactory.class.getName() ) )
            .toProvider(
                service( WatchRegistryForConfigurations.DefaultTriggerFactory.class )
                    .attributes( WatchRegistryForConfigurations.DefaultTriggerFactory.attributes() )
                    .export()
            );
    }

    @Override
    public String toString()
    {
        return "Configuration Admin Flows";
    }

}