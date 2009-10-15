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
import org.osgi.service.obr.RepositoryAdmin;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.recipes.flow.obr.GenerateObrRepositoryFromDirectory;
import org.ops4j.pax.flow.recipes.flow.obr.RegenerateObrRepositoryFromDirectory;
import org.ops4j.pax.flow.recipes.flow.obr.ScanDirectoryForObrRepositories;
import org.ops4j.pax.flow.recipes.flow.obr.WatchBundlesForObrRepositories;
import static org.ops4j.peaberry.Peaberry.*;
import static org.ops4j.peaberry.util.TypeLiterals.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ObrFlowsGuiceConfig
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( RepositoryAdmin.class ).toProvider( service( RepositoryAdmin.class ).single() );

        bind( export( FlowFactory.class ) )
            .annotatedWith( named( ScanDirectoryForObrRepositories.Factory.class.getName() ) )
            .toProvider(
                service( ScanDirectoryForObrRepositories.Factory.class )
                    .attributes( ScanDirectoryForObrRepositories.Factory.attributes() )
                    .export()
            );

        bind( export( FlowFactory.class ) )
            .annotatedWith( named( WatchBundlesForObrRepositories.Factory.class.getName() ) )
            .toProvider(
                service( WatchBundlesForObrRepositories.Factory.class )
                    .attributes( WatchBundlesForObrRepositories.Factory.attributes() )
                    .export()
            );

        bind( export( TriggerFactory.class ) )
            .annotatedWith( named( WatchBundlesForObrRepositories.DefaultTriggerFactory.class.getName() ) )
            .toProvider(
                service( WatchBundlesForObrRepositories.DefaultTriggerFactory.class )
                    .attributes( WatchBundlesForObrRepositories.DefaultTriggerFactory.attributes() )
                    .export()
            );

        bind( export( FlowFactory.class ) )
            .annotatedWith( named( GenerateObrRepositoryFromDirectory.Factory.class.getName() ) )
            .toProvider(
                service( GenerateObrRepositoryFromDirectory.Factory.class )
                    .attributes( GenerateObrRepositoryFromDirectory.Factory.attributes() )
                    .export()
            );

        bind( export( FlowFactory.class ) )
            .annotatedWith( named( RegenerateObrRepositoryFromDirectory.Factory.class.getName() ) )
            .toProvider(
                service( RegenerateObrRepositoryFromDirectory.Factory.class )
                    .attributes( RegenerateObrRepositoryFromDirectory.Factory.attributes() )
                    .export()
            );

    }

    @Override
    public String toString()
    {
        return "Configuration Admin Flows";
    }

}