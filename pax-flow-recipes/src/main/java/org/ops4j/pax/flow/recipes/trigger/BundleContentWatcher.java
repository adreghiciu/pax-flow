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
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.inject.Inject;
import org.osgi.framework.Bundle;
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
import org.ops4j.pax.flow.recipes.internal.AbstractTrigger;
import org.ops4j.pax.swissbox.extender.BundleObserver;
import org.ops4j.pax.swissbox.extender.BundleURLScanner;
import org.ops4j.pax.swissbox.extender.BundleWatcher;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class BundleContentWatcher
    extends AbstractTrigger<BundleContentWatcher>
    implements Trigger
{

    public static final PropertyName BUNDLE = propertyName( "bundle" );
    public static final PropertyName URLS = propertyName( "manifestEntries" );
    public static final PropertyName EVENT = propertyName( "event" );

    public static final String ADDED = "ADDED";
    public static final String REMOVED = "REMOVED";

    private final BundleWatcher<URL> m_bundleWatcher;
    private final String m_description;

    public BundleContentWatcher( final TriggerName name,
                                 final ExecutionTarget target,
                                 final BundleContext bundleContext,
                                 final String path,
                                 final String pattern,
                                 final boolean recursive )
    {
        super( name, target );

        // VALIDATE
        m_bundleWatcher = new BundleWatcher<URL>(
            bundleContext,
            new BundleURLScanner( path, pattern, recursive ),
            new BundleObserver<URL>()
            {
                public void addingEntries( final Bundle bundle, final List<URL> urls )
                {
                    final DefaultExecutionContext executionContext = defaultExecutionContext();
                    executionContext.add( executionProperty( BUNDLE, bundle ) );
                    executionContext.add( executionProperty( URLS, urls ) );
                    executionContext.add( executionProperty( EVENT, ADDED ) );

                    fire( executionContext );
                }

                public void removingEntries( final Bundle bundle, final List<URL> urls )
                {
                    final DefaultExecutionContext executionContext = defaultExecutionContext();
                    executionContext.add( executionProperty( BUNDLE, bundle ) );
                    executionContext.add( executionProperty( URLS, urls ) );
                    executionContext.add( executionProperty( EVENT, REMOVED ) );

                    fire( executionContext );
                }
            }
        );

        m_description = format(
            "Watch bundles for files like [%s] in path [%s]%s", pattern, path, recursive ? ", recursive" : ""
        );
    }

    @Override
    public BundleContentWatcher start()
        throws Exception
    {
        if( !isStarted() )
        {
            super.start();
            m_bundleWatcher.start();
        }
        return itself();
    }

    @Override
    public BundleContentWatcher stop()
    {
        if( isStarted() )
        {
            m_bundleWatcher.stop();
            super.stop();
        }
        return itself();
    }

    @Override
    public String toString()
    {
        return m_description;
    }

    @Override
    protected BundleContentWatcher itself()
    {
        return this;
    }

    /**
     * JAVADOC
     *
     * @author Alin Dreghiciu
     */
    public static class Factory
        implements TriggerFactory<BundleContentWatcher>
    {

        public static final TriggerType TYPE = triggerType( BundleContentWatcher.class );

        public static final PropertyName PATH = propertyName( "path" );
        public static final PropertyName PATTERN = propertyName( "pattern" );
        public static final PropertyName RECURSIVE = propertyName( "recursive" );

        private final BundleContext m_bundleContext;

        private long m_counter;

        @Inject
        public Factory( final BundleContext bundleContext )
        {
            // VALIDATE
            m_bundleContext = bundleContext;
        }

        public TriggerType type()
        {
            return TYPE;
        }

        public BundleContentWatcher create( final Configuration configuration,
                                            final ExecutionTarget target )
            throws ClassNotFoundException
        {

            final TypedConfiguration config = typedConfiguration( configuration );

            final String path = config.mandatory( PATH, String.class );
            final String pattern = config.optional( PATTERN, String.class );
            final String recursive = config.optional( RECURSIVE, String.class );

            return new BundleContentWatcher(
                triggerName( format( "%s::%d", type(), m_counter++ ) ),
                target,
                m_bundleContext,
                path,
                pattern,
                Boolean.valueOf( recursive )
            );
        }

        @Override
        public String toString()
        {
            return format( "Trigger factory for type [%s] (%d instances)", type(), m_counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "triggerType", TYPE.toString() );

            return attributes;
        }

    }

}