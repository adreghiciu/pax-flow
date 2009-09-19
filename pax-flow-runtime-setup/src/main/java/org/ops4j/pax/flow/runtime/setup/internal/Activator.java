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
package org.ops4j.pax.flow.runtime.setup.internal;

import com.google.inject.AbstractModule;
import static com.google.inject.Guice.*;
import com.google.inject.Inject;
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
import static org.ops4j.pax.flow.runtime.setup.internal.Properties.*;
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
    private Export<FlowFactory> m_sjffExport;

    public void start( final BundleContext bundleContext )
        throws Exception
    {
        LOG.debug( "Binding default flows to Pax Flow" );

        createInjector( osgiModule( bundleContext ), new Module() ).injectMembers( this );

        m_transformer.schedule(
            immutableJobDescription(
                ScheduleJobFlow.Factory.TYPE,
                withoutConfiguration(),
                triggerType( "serviceAvailableTrigger" ),
                immutableConfiguration(
                    property( WATCHED_SERVICE_TYPE, JobDescription.class.getName() )
                )
            )
        );

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
        LOG.info( "Default flows unbounded from Pax Flow" );
    }

    private static class Module extends AbstractModule
    {

        @Override
        protected void configure()
        {
            bind( Transformer.class ).toProvider( service( Transformer.class ).single() );
            bind( export( FlowFactory.class ) )
                .toProvider(
                    service( ScheduleJobFlow.Factory.class )
                        .attributes( ScheduleJobFlow.Factory.attributes() )
                        .export()
                );
        }

    }

}