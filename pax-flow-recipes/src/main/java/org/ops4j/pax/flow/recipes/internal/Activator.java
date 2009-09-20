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
import static org.ops4j.pax.flow.api.TriggerType.*;
import static org.ops4j.pax.flow.api.helpers.ImmutableConfiguration.*;
import static org.ops4j.pax.flow.api.helpers.ImmutableJobDescription.*;
import org.ops4j.pax.flow.recipes.flow.ScanDirectoryForJobDescriptionsFlow;
import org.ops4j.pax.flow.recipes.flow.ScheduleJobFlow;
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
    @Named( Module.SCHEDULE_JOB_FLOW )
    private Export<FlowFactory> m_sjffExport;

    @Inject
    @Named( Module.SCAN_DIRECTORY_FOR_JOB_DESCRIPTIONS )
    private Export<FlowFactory> m_sdfjdExport;

    public void start( final BundleContext bundleContext )
    {
        LOG.debug( "Binding default flows to Pax Flow" );

        createInjector( osgiModule( bundleContext ), new Module() ).injectMembers( this );

        setupScheduleJobFlow();
        setupJobDescriptionScanningFlow();

        LOG.info( "Default flows bounded to Pax Flow" );
    }

    public void stop( final BundleContext bundleContext )
        throws Exception
    {
        if( m_sjffExport != null )
        {
            m_sjffExport.unput();
            m_sjffExport = null;
        }
        if( m_sdfjdExport != null )
        {
            m_sdfjdExport.unput();
            m_sdfjdExport = null;
        }
        LOG.info( "Default flows unbounded from Pax Flow" );
    }

    private static class Module extends AbstractModule
    {

        private static final String SCHEDULE_JOB_FLOW = "ScheduleJobFlow";
        private static final String SCAN_DIRECTORY_FOR_JOB_DESCRIPTIONS = "ScanDirectoryForJobDescriptionsFlow";

        @Override
        protected void configure()
        {
            bind( Transformer.class ).toProvider( service( Transformer.class ).single() );

            bind( export( FlowFactory.class ) )
                .annotatedWith( named( SCHEDULE_JOB_FLOW ) )
                .toProvider(
                    service( ScheduleJobFlow.Factory.class )
                        .attributes( ScheduleJobFlow.Factory.attributes() )
                        .export()
                );

            bind( export( FlowFactory.class ) )
                .annotatedWith( named( SCAN_DIRECTORY_FOR_JOB_DESCRIPTIONS ) )
                .toProvider(
                    service( ScanDirectoryForJobDescriptionsFlow.Factory.class )
                        .attributes( ScanDirectoryForJobDescriptionsFlow.Factory.attributes() )
                        .export()
                );
        }

    }

    private void setupScheduleJobFlow()
    {
        try
        {
            m_transformer.schedule(
                immutableJobDescription(
                    ScheduleJobFlow.Factory.TYPE,
                    withoutConfiguration(),
                    triggerType( "serviceAvailableTrigger" ),
                    immutableConfiguration(
                        property( Properties.WATCHED_SERVICE_TYPE, JobDescription.class.getName() )
                    )
                )
            );
        }
        catch( Exception ignore )
        {
            LOG.warn( "Setup of 'Schedule Job Flow' failed.", ignore );
        }
    }

    private void setupJobDescriptionScanningFlow()
    {
        try
        {
            m_transformer.schedule(
                immutableJobDescription(
                    ScanDirectoryForJobDescriptionsFlow.Factory.TYPE,
                    immutableConfiguration(
                        property( Properties.DIRECTORY, "${default.directory.jobs:./conf/jobs}" )
                    ),
                    triggerType( "fixedRateTimerTrigger" ),
                    immutableConfiguration(
                        property( Properties.INITIAL_DELAY, "${default.initialDelay:5s}" ),
                        property( Properties.REPEAT_PERIOD, "${default.repeatPeriod:10s}" )
                    )
                )
            );
        }
        catch( Exception ignore )
        {
            LOG.warn( "Setup of 'Job Description Scanning Flow' failed.", ignore );
        }
    }

}