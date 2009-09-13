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
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.osgi.framework.BundleContext;
import org.quartz.Scheduler;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.TriggerFactory;
import static org.ops4j.pax.flow.api.base.TriggerName.*;
import org.ops4j.pax.flow.it.BasicConfiguration;
import org.ops4j.pax.flow.it.Wait;
import static org.ops4j.pax.flow.it.Wait.*;
import org.ops4j.pax.flow.trigger.ManualTrigger;
import static org.ops4j.peaberry.Peaberry.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
@RunWith( JUnit4TestRunner.class )
@Configuration( extend = { BasicConfiguration.class } )
public class Test001ManualTrigger
{

    @org.ops4j.pax.exam.Inject
    private BundleContext m_bundleContext;

    @Inject
    private Scheduler m_scheduler;

    @Test
    public void testMethod()
        throws Exception
    {
        final Injector injector = createInjector( osgiModule( m_bundleContext ), new Module() );

        injector.injectMembers( this );

        final TriggerFactory<ManualTrigger> manualTriggerFactory = injector.getInstance( ManualTrigger.Factory.class );

        final Flow flow = mock( Flow.class );

        manualTriggerFactory.create(
            triggerName( "test" ),
            null,
            flow
        ).start().fire();

        waitFor( 2 );
        
        verify( flow ).execute();
    }

    public static class Module
        extends AbstractModule
    {

        @Override
        protected void configure()
        {
            bind( Scheduler.class ).toProvider( service( Scheduler.class ).single() );
        }

    }

}
