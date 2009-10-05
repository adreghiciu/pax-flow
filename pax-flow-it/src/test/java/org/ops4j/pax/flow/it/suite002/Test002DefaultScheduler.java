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
import com.google.inject.Injector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.flow.api.Scheduler;
import org.ops4j.pax.flow.it.Cfg;
import static org.ops4j.peaberry.Peaberry.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
@RunWith( JUnit4TestRunner.class )
@org.ops4j.pax.exam.junit.Configuration( extend = { Cfg.Runtime.class, Cfg.Triggers.class } )
public class Test002DefaultScheduler
{

    @org.ops4j.pax.exam.Inject
    private BundleContext bundleContext;

    /**
     * Tests that runtime bundle is activated without problems (exceptions) and a {@link org.ops4j.pax.flow.api.Scheduler} service gets
     * registered.
     */
    @Test
    public void runtimeStarts()
    {
        final Injector injector = createInjector( osgiModule( bundleContext ), new GuiceSetup() );

        injector.injectMembers( this );

        injector.getInstance( Scheduler.class );
    }

    public static class GuiceSetup
        extends AbstractModule
    {

        @Override
        protected void configure()
        {
            bind( Scheduler.class ).toProvider( service( Scheduler.class ).single() );
        }

    }


}