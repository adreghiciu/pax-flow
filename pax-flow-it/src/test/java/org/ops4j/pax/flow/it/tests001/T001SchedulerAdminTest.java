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

package org.ops4j.pax.flow.it.tests001;

import static com.google.inject.Guice.*;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import static org.ops4j.pax.exam.CoreOptions.*;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.*;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import static org.ops4j.pax.flow.api.base.JobName.*;
import org.ops4j.pax.flow.api.base.WorkflowName;
import static org.ops4j.pax.flow.api.helper.ImmutableJob.*;
import static org.ops4j.pax.flow.api.helper.ImmutableWorkflow.*;
import org.ops4j.pax.flow.trigger.ManualTrigger;
import static org.ops4j.peaberry.Peaberry.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
@RunWith( JUnit4TestRunner.class )
public class T001SchedulerAdminTest
{

    @Inject
    private BundleContext m_bundleContext;

    @Configuration
    public static Option[] configuration()
    {
        return options(
            profile( "peaberry" ).version( "1.1.1" ),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-osw-minimal" ).versionAsInProject(),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-osq-complete" ).versionAsInProject(),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-api" ).versionAsInProject(),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-trigger" ).versionAsInProject(),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-runtime" ).versionAsInProject()
        );
    }

    @Test
    public void testMethod()
    {
        final T001Holder holder = new T001Holder();
        createInjector( osgiModule( m_bundleContext ), new T001Module() ).injectMembers( holder );

        holder.schedulerAdmin.schedule(
            job( jobName( "test.job" ),
                 new ManualTrigger.Factory(),
                 workflow( WorkflowName.workflowName( "test.workflow" ), new WorkflowDescriptor() )
            )
        );
    }

}
