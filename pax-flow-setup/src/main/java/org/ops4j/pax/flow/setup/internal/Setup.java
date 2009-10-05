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
package org.ops4j.pax.flow.setup.internal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import static org.ops4j.pax.flow.api.ConfigurationProperty.*;
import org.ops4j.pax.flow.api.JobDescription;
import static org.ops4j.pax.flow.api.JobName.*;
import org.ops4j.pax.flow.api.Scheduler;
import static org.ops4j.pax.flow.api.helpers.FrameworkPropertiesConfiguration.*;
import static org.ops4j.pax.flow.api.helpers.ImmutableConfiguration.*;
import static org.ops4j.pax.flow.api.helpers.ImmutableJobDescription.*;
import org.ops4j.pax.flow.recipes.flow.job.ScanDirectoryForJobDescriptions;
import org.ops4j.pax.flow.recipes.flow.job.WatchRegistryForJobDescriptions;
import org.ops4j.pax.flow.recipes.trigger.FixedRateTimer;
import org.ops4j.pax.flow.recipes.trigger.ServiceAvailable;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
@Singleton
public class Setup
{

    private static final Log LOG = LogFactory.getLog( Setup.class );

    private Scheduler m_scheduler;
    private final BundleContext m_bundleContext;

    @Inject
    public Setup( final Scheduler scheduler,
                  final BundleContext bundleContext )
    {

        m_scheduler = scheduler;
        m_bundleContext = bundleContext;

        setupWatchRegistryForJobDescriptionsFlow();
        setupJobDescriptionScanningFlow();
    }

    // TODO on stop unregister jobs?

    private void setupWatchRegistryForJobDescriptionsFlow()
    {
        try
        {
            m_scheduler.schedule(
                immutableJobDescription(
                    jobName( "watchRegistryForJobDescriptions (default setup)" ),
                    WatchRegistryForJobDescriptions.Factory.TYPE,
                    withoutConfiguration(),
                    ServiceAvailable.Factory.TYPE,
                    immutableConfiguration(
                        configurationProperty(
                            ServiceAvailable.Factory.WATCHED_SERVICE_TYPE, JobDescription.class.getName()
                        )
                    )
                )
            );
        }
        catch( Exception ignore )
        {
            LOG.warn( "Setup of 'Schedule Job Flow' failed.", ignore );
        }
    }

    private void setupJobDescriptionScanningFlow()
    {
        try
        {
            m_scheduler.schedule(
                immutableJobDescription(
                    jobName( "scanFileSystemForJobDescriptions (default setup)" ),
                    ScanDirectoryForJobDescriptions.Factory.TYPE,
                    immutableConfiguration(
                        frameworkPropertiesConfiguration( m_bundleContext ),
                        configurationProperty(
                            ScanDirectoryForJobDescriptions.Factory.DIRECTORY,
                            "${default.directory.jobs:${default.directory:./conf}/jobs/active}"
                        )
                    ),
                    FixedRateTimer.Factory.TYPE,
                    immutableConfiguration(
                        frameworkPropertiesConfiguration( m_bundleContext ),
                        configurationProperty( FixedRateTimer.Factory.INITIAL_DELAY, "${default.initialDelay:0s}" ),
                        configurationProperty( FixedRateTimer.Factory.REPEAT_PERIOD, "${default.repeatPeriod:10s}" )
                    )
                )
            );
        }
        catch( Exception ignore )
        {
            LOG.warn( "Setup of 'Job Description Scanning Flow' failed.", ignore );
        }
    }

}