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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import static com.google.inject.Guice.*;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.flow.api.FlowFactoryRegistry;
import org.ops4j.pax.flow.api.TriggerFactoryRegistry;
import static org.ops4j.peaberry.Peaberry.*;
import static org.ops4j.peaberry.util.TypeLiterals.*;
import org.ops4j.peaberry.Export;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class Activator
    implements BundleActivator
{

    @Inject
    private Export<TriggerFactoryRegistry> m_tfrExport;
    @Inject
    private Export<FlowFactoryRegistry> m_ffrExport;

    public void start( final BundleContext bundleContext )
        throws Exception
    {
        createInjector( osgiModule( bundleContext ), new Module() ).injectMembers( this );
    }

    public void stop( final BundleContext bundleContext )
        throws Exception
    {
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
    }

    private static class Module extends AbstractModule
    {

        @Override
        protected void configure()
        {
            bind( TriggerFactoryRegistry.class )
                .toProvider( service( CompositeTriggerFactoryRegistry.class ).single() );
            bind( FlowFactoryRegistry.class )
                .toProvider( service( CompositeFlowFactoryRegistry.class ).single() );

            bind( export( TriggerFactoryRegistry.class ) )
                .toProvider( service( SRTriggerFactoryRegistry.class ).export() );
            bind( export( FlowFactoryRegistry.class ) )
                .toProvider( service( SRFlowFactoryRegistry.class ).export() );
        }

    }

}
