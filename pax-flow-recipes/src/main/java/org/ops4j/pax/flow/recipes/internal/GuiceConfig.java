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
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.Transformer;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.recipes.flow.ScanDirectoryForJobDescriptionsFlow;
import org.ops4j.pax.flow.recipes.flow.ScheduleJobFlow;
import org.ops4j.pax.flow.recipes.trigger.FixedRateTimer;
import org.ops4j.pax.flow.recipes.trigger.Manual;
import org.ops4j.pax.flow.recipes.trigger.ServiceAvailable;
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

    private static final String SERVICE_AVAILABLE = "serviceAvailableTrigger";
    private static final String MANUAL = "manualTriger";
    private static final String TIMER = "fixedRateTimerTrigger";

    private static final String SCHEDULE_JOB_FLOW = "ScheduleJobFlow";
    private static final String SCAN_DIRECTORY_FOR_JOB_DESCRIPTIONS = "ScanDirectoryForJobDescriptionsFlow";

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

        LOG.info( "Registered built-in flows and triggers" );
    }

}