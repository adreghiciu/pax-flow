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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.Transformer;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.recipes.flow.ScanDirectoryForJobDescriptions;
import org.ops4j.pax.flow.recipes.flow.WatchRegistryForJobDescriptions;
import org.ops4j.pax.flow.recipes.flow.cm.ScanDirectoryForConfigurations;
import org.ops4j.pax.flow.recipes.flow.cm.WatchBundlesForConfigurations;
import org.ops4j.pax.flow.recipes.flow.scanner.ScanBundles;
import org.ops4j.pax.flow.recipes.trigger.BundleContentWatcher;
import org.ops4j.pax.flow.recipes.trigger.BundleManifestWatcher;
import org.ops4j.pax.flow.recipes.trigger.FixedRateTimer;
import org.ops4j.pax.flow.recipes.trigger.Manual;
import org.ops4j.pax.flow.recipes.trigger.ServiceAvailable;
import org.ops4j.pax.flow.recipes.trigger.ServiceUnavailable;
import org.ops4j.pax.flow.recipes.trigger.ServiceWatcher;
import org.ops4j.pax.scanner.ProvisionService;
import static org.ops4j.peaberry.Peaberry.*;
import static org.ops4j.peaberry.util.TypeLiterals.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class GuiceConfig
    extends AbstractModule
{

    private static final Log LOG = LogFactory.getLog( GuiceConfig.class );

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
            .annotatedWith( named( FixedRateTimer.Factory.class.getName() ) )
            .toProvider(
                service( FixedRateTimer.Factory.class )
                    .attributes( FixedRateTimer.Factory.attributes() )
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

        bind( Transformer.class ).toProvider( service( Transformer.class ).single() );

        bind( export( FlowFactory.class ) )
            .annotatedWith( named( WatchRegistryForJobDescriptions.Factory.class.getName() ) )
            .toProvider(
                service( WatchRegistryForJobDescriptions.Factory.class )
                    .attributes( WatchRegistryForJobDescriptions.Factory.attributes() )
                    .export()
            );

        bind( export( FlowFactory.class ) )
            .annotatedWith( named( ScanDirectoryForJobDescriptions.Factory.class.getName() ) )
            .toProvider(
                service( ScanDirectoryForJobDescriptions.Factory.class )
                    .attributes( ScanDirectoryForJobDescriptions.Factory.attributes() )
                    .export()
            );

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

        bind( ProvisionService.class ).toProvider( service( ProvisionService.class ).single() );

        bind( export( FlowFactory.class ) )
            .annotatedWith( named( ScanBundles.Factory.class.getName() ) )
            .toProvider(
                service( ScanBundles.Factory.class )
                    .attributes( ScanBundles.Factory.attributes() )
                    .export()
            );

        LOG.info( "Registered built-in flows and triggers" );
    }

}