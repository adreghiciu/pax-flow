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
package org.ops4j.pax.flow.runtime.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.inject.AbstractModule;
import static com.google.inject.Guice.*;
import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowFactoryRegistry;
import org.ops4j.pax.flow.api.Transformer;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.api.TriggerFactoryRegistry;
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
    private Export<TriggerFactoryRegistry> m_tfrExport;
    @Inject
    private Export<FlowFactoryRegistry> m_ffrExport;
    @Inject
    private Export<Transformer> m_transformerExport;

    public void start( final BundleContext bundleContext )
        throws Exception
    {
        LOG.debug( "Starting Pax Transformers Runtime ..." );
        createInjector( osgiModule( bundleContext ), new Module() ).injectMembers( this );

        LOG.info( "Started Pax Transformers Runtime" );
    }

    public void stop( final BundleContext bundleContext )
        throws Exception
    {
        if( m_transformerExport != null )
        {
            m_transformerExport.unput();
            m_transformerExport = null;
        }
        if( m_tfrExport != null )
        {
            m_tfrExport.unput();
            m_tfrExport = null;
        }
        if( m_ffrExport != null )
        {
            m_ffrExport.unput();
            m_ffrExport = null;
        }
        LOG.info( "Stopped Pax Transformers Runtime" );
    }

    private static class Module extends AbstractModule
    {

        @Override
        protected void configure()
        {
            // TODO make thread pool configurable
            bind( ExecutorService.class ).toInstance( Executors.newFixedThreadPool( 10 ) );
            bind( export( Transformer.class ) ).toProvider( service( DefaultTransformer.class ).export() );

            bind( TriggerFactoryRegistry.class )
                .toProvider( service( CompositeTriggerFactoryRegistry.class ).single() );

            bind( iterable( FlowFactory.class ) )
                .toProvider( service( FlowFactory.class ).multiple() );
            bind( iterable( TriggerFactory.class ) )
                .toProvider( service( TriggerFactory.class ).multiple() );

            bind( export( TriggerFactoryRegistry.class ) )
                .toProvider( service( SRTriggerFactoryRegistry.class ).export() );
            bind( export( FlowFactoryRegistry.class ) )
                .toProvider( service( SRFlowFactoryRegistry.class ).export() );
        }

    }

}
