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
package org.ops4j.pax.flow.trigger.internal;

import com.google.inject.AbstractModule;
import static com.google.inject.Guice.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import static com.google.inject.name.Names.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.flow.api.Transformer;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.trigger.ManualTrigger;
import org.ops4j.pax.flow.trigger.ServiceAvailableTrigger;
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
    @Named( "manual" )
    private Export<TriggerFactory> m_mtExport;

    @Inject
    @Named( "serviceAvailable" )
    private Export<TriggerFactory> m_satExport;

    public void start( final BundleContext bundleContext )
        throws Exception
    {
        LOG.debug( "Installing default triggers" );

        createInjector( osgiModule( bundleContext ), new Module() ).injectMembers( this );

        LOG.info( "Default triggers installed" );
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
        LOG.info( "Default triggers un-installed" );
    }

    private static class Module extends AbstractModule
    {

        @Override
        protected void configure()
        {
            bind( export( TriggerFactory.class ) )
                .annotatedWith( named( "manual" ) )
                .toProvider( service( ManualTrigger.Factory.class ).export() );

            bind( export( TriggerFactory.class ) )
                .annotatedWith( named( "serviceAvailable" ) )
                .toProvider( service( ServiceAvailableTrigger.Factory.class ).export() );
        }

    }

}