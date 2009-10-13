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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.ops4j.pax.flow.recipes.internal.trigger.AbstractTrigger;
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
public class BundleManifestWatcher
    extends AbstractTrigger<BundleManifestWatcher>
    implements Trigger
{

    private static final Log LOG = LogFactory.getLog( BundleManifestWatcher.class );

    public static final PropertyName BUNDLE = propertyName( "bundle" );
    public static final PropertyName MANIFEST_ENTRIES = propertyName( "manifestEntries" );
    public static final PropertyName EVENT = propertyName( "event" );

    public static final String NEW = "NEW";
    public static final String REMOVED = "REMOVED";

    private final BundleWatcher<ManifestEntry> bundleWatcher;
    private final String description;

    public BundleManifestWatcher( final TriggerName name,
                                  final ExecutionTarget target,
                                  final BundleContext bundleContext,
                                  final String regexp )
    {
        super( name, target );

        // VALIDATE
        bundleWatcher = new BundleWatcher<ManifestEntry>(
            bundleContext,
            new BundleManifestScanner( new RegexKeyManifestFilter( regexp ) ),
            new BundleObserver<ManifestEntry>()
            {
                public void addingEntries( final Bundle bundle, final List<ManifestEntry> manifestEntries )
                {
                    if( LOG.isInfoEnabled() )
                    {
                        for( ManifestEntry entry : manifestEntries )
                        {
                            LOG.info( format( "Watching manifest entry [%s] in bundle [%s]", entry, bundle ) );
                        }
                    }

                    final DefaultExecutionContext executionContext = defaultExecutionContext();
                    executionContext.add( executionProperty( BUNDLE, bundle ) );
                    executionContext.add( executionProperty( MANIFEST_ENTRIES, manifestEntries ) );
                    executionContext.add( executionProperty( EVENT, NEW ) );

                    fire( executionContext );
                }

                public void removingEntries( final Bundle bundle, final List<ManifestEntry> manifestEntries )
                {
                    if( LOG.isInfoEnabled() )
                    {
                        for( ManifestEntry entry : manifestEntries )
                        {
                            LOG.info( format( "Un-watching manifest entry [%s] in bundle [%s]", entry, bundle ) );
                        }
                    }

                    final DefaultExecutionContext executionContext = defaultExecutionContext();
                    executionContext.add( executionProperty( BUNDLE, bundle ) );
                    executionContext.add( executionProperty( MANIFEST_ENTRIES, manifestEntries ) );
                    executionContext.add( executionProperty( EVENT, REMOVED ) );

                    fire( executionContext );
                }
            }
        );

        description = format( "Watch bundles with a manifest attribute matching [%s]", regexp );
    }

    @Override
    public BundleManifestWatcher start()
        throws Exception
    {
        if( !isStarted() )
        {
            super.start();
            bundleWatcher.start();
        }
        return itself();
    }

    @Override
    public BundleManifestWatcher stop()
    {
        if( isStarted() )
        {
            bundleWatcher.stop();
            super.stop();
        }
        return itself();
    }

    @Override
    public String toString()
    {
        return description;
    }

    @Override
    protected BundleManifestWatcher itself()
    {
        return this;
    }

    /**
     * JAVADOC
     *
     * @author Alin Dreghiciu
     */
    public static class Factory
        implements TriggerFactory<BundleManifestWatcher>
    {

        public static final TriggerType TYPE = triggerType( BundleManifestWatcher.class );

        public static final PropertyName REGEXP = propertyName( "regexp" );

        private final BundleContext bundleContext;

        private long counter;

        @Inject
        public Factory( final BundleContext bundleContext )
        {
            // VALIDATE
            this.bundleContext = bundleContext;
        }

        public TriggerType type()
        {
            return TYPE;
        }

        public BundleManifestWatcher create( final Configuration configuration,
                                             final ExecutionTarget target )
        {

            final TypedConfiguration config = typedConfiguration( configuration );

            final String regexp = config.mandatory( REGEXP, String.class );

            return new BundleManifestWatcher(
                triggerName( format( "%s::%d", type(), counter++ ) ),
                target,
                bundleContext,
                regexp
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