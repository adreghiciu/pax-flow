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
import static com.google.inject.Guice.*;
import com.google.inject.Inject;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
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

    @Inject
    private Export<Scheduler> m_schedulerHandler;

    public void start( final BundleContext bundleContext )
        throws Exception
    {
        createInjector( osgiModule( bundleContext ), new Module() ).injectMembers( this );
    }

    public void stop( final BundleContext bundleContext )
        throws Exception
    {
        if( m_schedulerHandler != null )
        {
            m_schedulerHandler.unput();
            m_schedulerHandler = null;
        }

    }

    private static class Module extends AbstractModule
    {

        private final Scheduler m_scheduler;

        Module()
            throws SchedulerException
        {
            m_scheduler = new StdSchedulerFactory().getScheduler();
            // TODO this should be in start so it can be stopped
            m_scheduler.start();
        }

        @Override
        protected void configure()
        {
            bind( export( Scheduler.class ) ).toProvider( service( m_scheduler ).export() );
        }

    }

}
