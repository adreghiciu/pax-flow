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

package org.ops4j.pax.flow.trigger;

import com.google.inject.Inject;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.ExecutionTarget;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
import org.ops4j.pax.flow.api.Trigger;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.api.TriggerName;
import static org.ops4j.pax.flow.api.TriggerName.*;
import org.ops4j.pax.flow.api.TriggerType;
import static org.ops4j.pax.flow.api.TriggerType.*;
import org.ops4j.pax.flow.api.helpers.DefaultExecutionContext;
import static org.ops4j.pax.flow.api.helpers.DefaultExecutionContext.*;
import org.ops4j.pax.flow.api.helpers.TypedConfiguration;
import static org.ops4j.pax.flow.api.helpers.TypedConfiguration.*;
import org.ops4j.pax.flow.trigger.internal.AbstractTrigger;
import org.ops4j.peaberry.Import;
import org.ops4j.peaberry.ServiceRegistry;
import org.ops4j.peaberry.util.AbstractWatcher;
import static org.ops4j.peaberry.util.Filters.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ServiceAvailableTrigger
    extends AbstractTrigger<ServiceAvailableTrigger>
    implements Trigger
{

    public static final PropertyName SERVICE = propertyName( "service" );

    private final ServiceRegistry m_serviceRegistry;

    public ServiceAvailableTrigger( final TriggerName name,
                                    final ExecutionTarget target,
                                    final ServiceRegistry serviceRegistry,
                                    final Class<?> serviceClass,
                                    final String serviceFilter )
    {
        super( name, target );
        // VALIDATE
        m_serviceRegistry = serviceRegistry;
        serviceRegistry.watch(
            serviceClass,
            serviceFilter == null ? null : ldap( serviceFilter ),
            new AbstractWatcher()
            {
                @Override
                protected Object adding( final Import anImport )
                {
                    final Object service = super.adding( anImport );

                    final DefaultExecutionContext executionContext = defaultExecutionContext();
                    executionContext.set( SERVICE, service );

                    fire( executionContext );

                    return service;
                }
            }
        );
    }

    @Override
    protected ServiceAvailableTrigger itself()
    {
        return this;
    }

    /**
     * JAVADOC
     *
     * @author Alin Dreghiciu
     */
    public static class Factory
        implements TriggerFactory<ServiceAvailableTrigger>
    {

        public static final PropertyName WATCHED_SERVICE_TYPE = propertyName( "watchedServiceType" );
        public static final PropertyName WATCHED_SERVICE_FILTER = propertyName( "watchedServiceFilter" );

        private final BundleContext m_bundleContext;
        private final ServiceRegistry m_serviceRegistry;

        private long m_counter;

        @Inject
        public Factory( final BundleContext bundleContext,
                        final ServiceRegistry serviceRegistry )
        {
            // VALIDATE
            m_bundleContext = bundleContext;
            m_serviceRegistry = serviceRegistry;
        }

        public TriggerType type()
        {
            return triggerType( ServiceAvailableTrigger.class );
        }

        public ServiceAvailableTrigger create( final Configuration configuration,
                                               final ExecutionTarget target )
            throws ClassNotFoundException
        {

            final TypedConfiguration config = typedConfiguration( configuration );

            final String serviceClassName = config.mandatory( WATCHED_SERVICE_TYPE, String.class );
            final String serviceFilter = config.optional( WATCHED_SERVICE_FILTER, String.class );

            final Class serviceClass = m_bundleContext.getBundle().loadClass( serviceClassName );

            return new ServiceAvailableTrigger(
                triggerName( String.format( "%s::%d", type(), m_counter++ ) ),
                target,
                m_serviceRegistry,
                serviceClass,
                serviceFilter
            );
        }
    }

}