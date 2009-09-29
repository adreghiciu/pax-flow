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
import org.ops4j.pax.swissbox.extender.BundleManifestScanner;
import org.ops4j.pax.swissbox.extender.BundleObserver;
import org.ops4j.pax.swissbox.extender.BundleWatcher;
import org.ops4j.pax.swissbox.extender.ManifestEntry;
import org.ops4j.pax.swissbox.extender.RegexKeyManifestFilter;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ManifestWatcher
    extends AbstractTrigger<ManifestWatcher>
    implements Trigger
{

    public static final PropertyName BUNDLE = propertyName( "bundle" );
    public static final PropertyName MANIFEST_ENTRIES = propertyName( "manifestEntries" );
    public static final PropertyName EVENT = propertyName( "event" );

    public static final String ADDED = "ADDED";
    public static final String REMOVED = "REMOVED";

    private final BundleWatcher<ManifestEntry> m_bundleWatcher;
    private final String m_description;

    public ManifestWatcher( final TriggerName name,
                            final ExecutionTarget target,
                            final BundleContext bundleContext,
                            final String regexp )
    {
        super( name, target );

        // VALIDATE
        m_bundleWatcher = new BundleWatcher<ManifestEntry>(
            bundleContext,
            new BundleManifestScanner( new RegexKeyManifestFilter( regexp ) ),
            new BundleObserver<ManifestEntry>()
            {
                public void addingEntries( final Bundle bundle, final List<ManifestEntry> manifestEntries )
                {
                    final DefaultExecutionContext executionContext = defaultExecutionContext();
                    executionContext.add( executionProperty( BUNDLE, bundle ) );
                    executionContext.add( executionProperty( MANIFEST_ENTRIES, manifestEntries ) );
                    executionContext.add( executionProperty( EVENT, ADDED ) );

                    fire( executionContext );
                }

                public void removingEntries( final Bundle bundle, final List<ManifestEntry> manifestEntries )
                {
                    final DefaultExecutionContext executionContext = defaultExecutionContext();
                    executionContext.add( executionProperty( BUNDLE, bundle ) );
                    executionContext.add( executionProperty( MANIFEST_ENTRIES, manifestEntries ) );
                    executionContext.add( executionProperty( EVENT, REMOVED ) );

                    fire( executionContext );
                }
            }
        );

        m_description = format( "Watch bundles with a manifest attribute matching [%s]", regexp );
    }

    @Override
    public ManifestWatcher start()
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
    public ManifestWatcher stop()
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
    protected ManifestWatcher itself()
    {
        return this;
    }

    /**
     * JAVADOC
     *
     * @author Alin Dreghiciu
     */
    public static class Factory
        implements TriggerFactory<ManifestWatcher>
    {

        public static final TriggerType TYPE = triggerType( ManifestWatcher.class );

        public static final PropertyName REGEXP = propertyName( "regexp" );

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

        public ManifestWatcher create( final Configuration configuration,
                                       final ExecutionTarget target )
            throws ClassNotFoundException
        {

            final TypedConfiguration config = typedConfiguration( configuration );

            final String regexp = config.mandatory( REGEXP, String.class );

            return new ManifestWatcher(
                triggerName( format( "%s::%d", type(), m_counter++ ) ),
                target,
                m_bundleContext,
                regexp
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