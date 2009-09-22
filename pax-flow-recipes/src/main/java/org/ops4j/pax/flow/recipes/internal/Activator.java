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
import static com.google.inject.Guice.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import static com.google.inject.name.Names.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.JobDescription;
import static org.ops4j.pax.flow.api.Property.*;
import org.ops4j.pax.flow.api.Transformer;
import org.ops4j.pax.flow.api.TriggerFactory;
import static org.ops4j.pax.flow.api.TriggerType.*;
import static org.ops4j.pax.flow.api.helpers.ImmutableConfiguration.*;
import static org.ops4j.pax.flow.api.helpers.ImmutableJobDescription.*;
import org.ops4j.pax.flow.recipes.flow.ScanDirectoryForJobDescriptionsFlow;
import org.ops4j.pax.flow.recipes.flow.ScheduleJobFlow;
import org.ops4j.pax.flow.recipes.trigger.FixedRateTimer;
import org.ops4j.pax.flow.recipes.trigger.Manual;
import org.ops4j.pax.flow.recipes.trigger.ServiceAvailable;
import org.ops4j.peaberry.Export;
import static org.ops4j.peaberry.Peaberry.*;
import static org.ops4j.peaberry.util.TypeLiterals.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class Activator
    implements BundleActivator
{

    private static final Log LOG = LogFactory.getLog( Activator.class );

    @Inject
    private Transformer m_transformer;

    @Inject
    @Named( Module.MANUAL )
    private Export<TriggerFactory> m_mtExport;

    @Inject
    @Named( Module.SERVICE_AVAILABLE )
    private Export<TriggerFactory> m_satExport;

    @Inject
    @Named( Module.TIMER )
    private Export<TriggerFactory> m_ttExport;

    public void start( final BundleContext bundleContext )
    {
        createInjector( osgiModule( bundleContext ), new Module() ).injectMembers( this );
        LOG.info( "Registered built in flows and triggers" );
    }

    public void stop( final BundleContext bundleContext )
        throws Exception
    {
        if( m_mtExport != null )
        {
            m_mtExport.unput();
            m_mtExport = null;
        }
        if( m_satExport != null )
        {
            m_satExport.unput();
            m_satExport = null;
        }
        if( m_ttExport != null )
        {
            m_ttExport.unput();
            m_ttExport = null;
        }

        LOG.info( "Unregistered built in flows and triggers" );
    }

    private static class Module extends AbstractModule
    {
        private static final String SERVICE_AVAILABLE = "serviceAvailableTrigger";
        private static final String MANUAL = "manualTriger";
        private static final String TIMER = "fixedRateTimerTrigger";

        @Override
        protected void configure()
        {

            // TODO make number of threads configurable
            bind( ScheduledExecutorService.class ).toInstance( Executors.newScheduledThreadPool( 5 ) );

            bind( export( TriggerFactory.class ) )
                .annotatedWith( named( MANUAL ) )
                .toProvider(
                    service( Manual.Factory.class )
                        .attributes( Manual.Factory.attributes() )
                        .export()
                );

            bind( export( TriggerFactory.class ) )
                .annotatedWith( named( SERVICE_AVAILABLE ) )
                .toProvider(
                    service( ServiceAvailable.Factory.class )
                        .attributes( ServiceAvailable.Factory.attributes() )
                        .export()
                );

            bind( export( TriggerFactory.class ) )
                .annotatedWith( named( TIMER ) )
                .toProvider(
                    service( FixedRateTimer.Factory.class )
                        .attributes( FixedRateTimer.Factory.attributes() )
                        .export()
                );
        }

    }

}