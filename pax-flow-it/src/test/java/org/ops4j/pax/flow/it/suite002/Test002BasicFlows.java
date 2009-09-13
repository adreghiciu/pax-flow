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

package org.ops4j.pax.flow.it.suite002;

import com.google.inject.AbstractModule;
import static com.google.inject.Guice.*;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.flow.api.base.FlowName;
import org.ops4j.pax.flow.api.helper.DefaultExecutionContext;
import org.ops4j.pax.flow.it.BasicConfiguration;
import org.ops4j.pax.flow.it.DebugConfiguration;
import org.ops4j.pax.flow.osworkflow.OSWorkflowDescriptor;
import org.ops4j.pax.flow.osworkflow.OSWorkflowFlowFactory;
import static org.ops4j.pax.flow.osworkflow.OSWorkflowName.*;
import org.ops4j.peaberry.Export;
import static org.ops4j.peaberry.Peaberry.*;
import static org.ops4j.peaberry.util.TypeLiterals.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
@RunWith( JUnit4TestRunner.class )
@Configuration( extend = { BasicConfiguration.class, DebugConfiguration.class } )
public class Test002BasicFlows
{

    @org.ops4j.pax.exam.Inject
    private BundleContext m_bundleContext;

    @Test
    public void testMethod()
        throws Exception
    {
        final Injector injector = createInjector( osgiModule( m_bundleContext ), new Module() );

        injector.injectMembers( this );

        final OSWorkflowFlowFactory osWorkflowFlowFactory = injector.getInstance( OSWorkflowFlowFactory.class );

        osWorkflowFlowFactory.create(
            FlowName.flowName( "installWorkflowDescriptors" ),
            new DefaultExecutionContext()
        ).execute();
    }

    public class Module
        extends AbstractModule
    {

        @Override
        protected void configure()
        {
            bind( Workflow.class ).toProvider( service( Workflow.class ).single() );
        }

    }

}