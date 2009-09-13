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
package org.ops4j.pax.flow.osworkflow.internal;

import com.google.inject.AbstractModule;
import static com.google.inject.Guice.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.loader.AbstractWorkflowFactory;
import com.opensymphony.workflow.spi.WorkflowStore;
import com.opensymphony.workflow.spi.memory.MemoryWorkflowStore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.flow.osworkflow.OSWorkflowDescriptor;
import org.ops4j.pax.flow.osworkflow.OSWorkflowDescriptorRegistry;
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

    private Workflow m_workflow;

    @Inject
    private Configuration m_configuration;
    @Inject
    private Export<Workflow> m_workflowExport;
    @Inject
    @Named( "serviceRegistry" )
    private Export<OSWorkflowDescriptorRegistry> m_srExport;
    @Inject
    @Named( "basic" )
    private Export<OSWorkflowDescriptorRegistry> m_basicExport;

    public void start( final BundleContext bundleContext )
        throws Exception
    {
        m_workflow = new BasicWorkflow( "Pax Flow" );
        createInjector( osgiModule( bundleContext ), new Module() ).injectMembers( this );
        m_workflow.setConfiguration( m_configuration );
    }

    public void stop( final BundleContext bundleContext )
        throws Exception
    {
        if( m_workflowExport != null )
        {
            m_workflowExport.unput();
            m_workflowExport = null;
        }
        if( m_srExport != null )
        {
            m_srExport.unput();
            m_srExport = null;
        }
        if( m_basicExport != null )
        {
            m_basicExport.unput();
            m_basicExport = null;
        }
    }

    private class Module
        extends AbstractModule
    {

        @Override
        protected void configure()
        {
            bind( WorkflowStore.class ).to( MemoryWorkflowStore.class );
            bind( AbstractWorkflowFactory.class ).to( DefaultOSWorkflowFactory.class );
            bind( Configuration.class ).to( DefaultOSWorkflowConfiguration.class );

            bind( iterable( OSWorkflowDescriptor.class ) ).toProvider(
                service( OSWorkflowDescriptor.class ).multiple()
            );
            bind( export( OSWorkflowDescriptorRegistry.class ) )
                .annotatedWith( Names.named( "serviceRegistry" ) )
                .toProvider( service( ServiceRegistryOSWorkflowDescriptorRegistry.class ).export()
                );
            bind( export( OSWorkflowDescriptorRegistry.class ) )
                .annotatedWith( Names.named( "basic" ) )
                .toProvider( service( BasicOSWorkflowDescriptorRegistry.class ).export()
                );

            bind( OSWorkflowDescriptorRegistry.class ).to( CompositeOSWorkflowDescriptorRegistry.class );

            bind( iterable( OSWorkflowDescriptorRegistry.class ) ).toProvider(
                service( OSWorkflowDescriptorRegistry.class ).multiple()
            );
            bind( export( Workflow.class ) ).toProvider( service( m_workflow ).export() );
        }

    }

}