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

package org.ops4j.pax.flow.recipes.trigger;

import static java.lang.String.*;
import java.util.HashMap;
import java.util.Map;
import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.flow.api.Configuration;
import static org.ops4j.pax.flow.api.ExecutionProperty.*;
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
import org.ops4j.pax.flow.recipes.internal.trigger.AbstractTrigger;
import org.ops4j.peaberry.Import;
import org.ops4j.peaberry.ServiceRegistry;
import org.ops4j.peaberry.util.AbstractWatcher;
import static org.ops4j.peaberry.util.Filters.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ServiceAvailable
    extends AbstractTrigger<ServiceAvailable>
    implements Trigger
{

    private static final Log LOG = LogFactory.getLog( ServiceAvailable.class );

    private final String description;

    public ServiceAvailable( final TriggerName name,
                             final ExecutionTarget target,
                             final ServiceRegistry serviceRegistry,
                             final Class<?> serviceClass,
                             final String serviceFilter )
    {
        super( name, target );
        // VALIDATE
        serviceRegistry.watch(
            serviceClass,
            serviceFilter == null ? null : ldap( serviceFilter ),
            new AbstractWatcher<Object>()
            {
                @Override
                protected Object adding( final Import<Object> anImport )
                {
                    final Object service = super.adding( anImport );

                    LOG.info( format( "Service [%s] available", service ) );

                    final DefaultExecutionContext executionContext = defaultExecutionContext();
                    executionContext.add( executionProperty( ServiceWatcher.SERVICE, service ) );
                    executionContext.add( executionProperty( ServiceWatcher.EVENT, ServiceWatcher.ADDED ) );

                    fire( executionContext );

                    return service;
                }
            }
        );
        description = format( "Service of type [%s] is available%s",
                              serviceClass.getName(),
                              serviceFilter == null ? "" : format( " (filter: %s", serviceFilter )
        );
    }

    @Override
    public String toString()
    {
        return description;
    }

    @Override
    protected ServiceAvailable itself()
    {
        return this;
    }

    /**
     * JAVADOC
     *
     * @author Alin Dreghiciu
     */
    public static class Factory
        implements TriggerFactory<ServiceAvailable>
    {

        public static final TriggerType TYPE = triggerType( ServiceAvailable.class );

        public static final PropertyName WATCHED_SERVICE_TYPE = propertyName( "watchedServiceType" );
        public static final PropertyName WATCHED_SERVICE_FILTER = propertyName( "watchedServiceFilter" );

        private final BundleContext bundleContext;
        private final ServiceRegistry serviceRegistry;

        private long counter;

        @Inject
        public Factory( final BundleContext bundleContext,
                        final ServiceRegistry serviceRegistry )
        {
            // VALIDATE
            this.bundleContext = bundleContext;
            this.serviceRegistry = serviceRegistry;
        }

        public TriggerType type()
        {
            return TYPE;
        }

        public ServiceAvailable create( final Configuration configuration,
                                        final ExecutionTarget target )
            throws ClassNotFoundException
        {

            final TypedConfiguration config = typedConfiguration( configuration );

            final String serviceClassName = config.mandatory( WATCHED_SERVICE_TYPE, String.class );
            final String serviceFilter = config.optional( WATCHED_SERVICE_FILTER, String.class );

            final Class serviceClass = bundleContext.getBundle().loadClass( serviceClassName );

            return new ServiceAvailable(
                triggerName( format( "%s::%d", type(), counter++ ) ),
                target,
                serviceRegistry,
                serviceClass,
                serviceFilter
            );
        }

        @Override
        public String toString()
        {
            return format( "Trigger factory for type [%s] (%d instances)", type(), counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "triggerType", TYPE.toString() );

            return attributes;
        }

    }

}