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

package org.ops4j.pax.flow.it.suite001;

import com.google.inject.AbstractModule;
import static com.google.inject.Guice.*;
import com.google.inject.Injector;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.ExecutionTarget;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.it.Cfg;
import org.ops4j.pax.flow.recipes.trigger.Manual;
import static org.ops4j.peaberry.Peaberry.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
@RunWith( JUnit4TestRunner.class )
@org.ops4j.pax.exam.junit.Configuration( extend = { Cfg.Triggers.class } )
public class Test001ManualTrigger
{

    @org.ops4j.pax.exam.Inject
    private BundleContext bundleContext;

    @Test
    public void executeTrigger()
        throws Exception
    {
        final Injector injector = createInjector( osgiModule( bundleContext ), new GuiceSetup() );

        injector.injectMembers( this );

        final TriggerFactory<Manual> factory = injector.getInstance(
            Manual.Factory.class
        );

        final Configuration config = mock( Configuration.class );
        final ExecutionTarget target = mock( ExecutionTarget.class );

        factory.create( config, target ).start().fire();

        verify( target ).execute( any( ExecutionContext.class ) );
    }

    public static class GuiceSetup
        extends AbstractModule
    {

        @Override
        protected void configure()
        {
            // nothing to bind
        }

    }

}
