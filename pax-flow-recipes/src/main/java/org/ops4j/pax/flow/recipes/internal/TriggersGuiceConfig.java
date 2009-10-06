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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import com.google.inject.AbstractModule;
import static com.google.inject.name.Names.*;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.recipes.trigger.BundleContentWatcher;
import org.ops4j.pax.flow.recipes.trigger.BundleManifestWatcher;
import org.ops4j.pax.flow.recipes.trigger.Manual;
import org.ops4j.pax.flow.recipes.trigger.ServiceAvailable;
import org.ops4j.pax.flow.recipes.trigger.ServiceUnavailable;
import org.ops4j.pax.flow.recipes.trigger.ServiceWatcher;
import org.ops4j.pax.flow.recipes.trigger.Timer;
import static org.ops4j.peaberry.Peaberry.*;
import static org.ops4j.peaberry.util.TypeLiterals.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class TriggersGuiceConfig
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        // TODO make number of threads configurable
        bind( ScheduledExecutorService.class ).toInstance( Executors.newScheduledThreadPool( 5 ) );

        bind( export( TriggerFactory.class ) )
            .annotatedWith( named( Manual.Factory.class.getName() ) )
            .toProvider(
                service( Manual.Factory.class )
                    .attributes( Manual.Factory.attributes() )
                    .export()
            );

        bind( export( TriggerFactory.class ) )
            .annotatedWith( named( ServiceAvailable.Factory.class.getName() ) )
            .toProvider(
                service( ServiceAvailable.Factory.class )
                    .attributes( ServiceAvailable.Factory.attributes() )
                    .export()
            );

        bind( export( TriggerFactory.class ) )
            .annotatedWith( named( ServiceUnavailable.Factory.class.getName() ) )
            .toProvider(
                service( ServiceUnavailable.Factory.class )
                    .attributes( ServiceUnavailable.Factory.attributes() )
                    .export()
            );

        bind( export( TriggerFactory.class ) )
            .annotatedWith( named( ServiceWatcher.Factory.class.getName() ) )
            .toProvider(
                service( ServiceWatcher.Factory.class )
                    .attributes( ServiceWatcher.Factory.attributes() )
                    .export()
            );

        bind( export( TriggerFactory.class ) )
            .annotatedWith( named( Timer.Factory.class.getName() ) )
            .toProvider(
                service( Timer.Factory.class )
                    .attributes( Timer.Factory.attributes() )
                    .export()
            );

        bind( export( TriggerFactory.class ) )
            .annotatedWith( named( BundleManifestWatcher.Factory.class.getName() ) )
            .toProvider(
                service( BundleManifestWatcher.Factory.class )
                    .attributes( BundleManifestWatcher.Factory.attributes() )
                    .export()
            );

        bind( export( TriggerFactory.class ) )
            .annotatedWith( named( BundleContentWatcher.Factory.class.getName() ) )
            .toProvider(
                service( BundleContentWatcher.Factory.class )
                    .attributes( BundleContentWatcher.Factory.attributes() )
                    .export()
            );
    }

    @Override
    public String toString()
    {
        return "Triggers";
    }

}