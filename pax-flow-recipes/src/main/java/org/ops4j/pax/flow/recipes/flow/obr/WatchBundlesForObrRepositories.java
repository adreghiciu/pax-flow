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

package org.ops4j.pax.flow.recipes.flow.obr;

import static java.lang.String.*;
import java.util.HashMap;
import java.util.Map;
import com.google.inject.Inject;
import org.osgi.framework.BundleContext;
import org.osgi.service.obr.RepositoryAdmin;
import org.ops4j.pax.flow.api.Configuration;
import static org.ops4j.pax.flow.api.ConfigurationProperty.*;
import org.ops4j.pax.flow.api.ExecutionTarget;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowName;
import static org.ops4j.pax.flow.api.FlowName.*;
import org.ops4j.pax.flow.api.FlowType;
import static org.ops4j.pax.flow.api.FlowType.*;
import org.ops4j.pax.flow.api.TriggerName;
import org.ops4j.pax.flow.api.TriggerType;
import static org.ops4j.pax.flow.api.TriggerType.*;
import org.ops4j.pax.flow.api.helpers.ForEachFlow;
import static org.ops4j.pax.flow.api.helpers.ImmutableConfiguration.*;
import org.ops4j.pax.flow.api.helpers.SequentialFlow;
import org.ops4j.pax.flow.api.helpers.SwitchFlow;
import static org.ops4j.pax.flow.api.helpers.SwitchFlow.SwitchCase.*;
import org.ops4j.pax.flow.recipes.flow.basic.ReplaceLinkFileWithURL;
import org.ops4j.pax.flow.recipes.trigger.BundleContentWatcher;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class WatchBundlesForObrRepositories
    extends SequentialFlow
    implements Flow
{

    public WatchBundlesForObrRepositories( final FlowName flowName,
                                           final RepositoryAdmin repositoryAdmin )
    {
        super(
            flowName,
            new SwitchFlow(
                BundleContentWatcher.EVENT,
                switchCase(
                    BundleContentWatcher.NEW,
                    new ForEachFlow(
                        BundleContentWatcher.URLS, AddObrRepository.REPOSITORY_URL,
                        new SequentialFlow(
                            flowName( format( "%s::%s", flowName, "New" ) ), // TODO do we need a name?
                            new ReplaceLinkFileWithURL( AddObrRepository.REPOSITORY_URL ),
                            new AddObrRepository( repositoryAdmin )
                        )
                    )
                ),
                switchCase(
                    BundleContentWatcher.REMOVED,
                    new ForEachFlow(
                        BundleContentWatcher.URLS, RemoveObrRepository.REPOSITORY_URL,
                        new SequentialFlow(
                            flowName( format( "%s::%s", flowName, "Removed" ) ), // TODO do we need a name?
                            new ReplaceLinkFileWithURL( RemoveObrRepository.REPOSITORY_URL ),
                            new RemoveObrRepository( repositoryAdmin )
                        )
                    )
                )
            )
        );
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( WatchBundlesForObrRepositories.class );

        private final RepositoryAdmin repositoryAdmin;

        private long counter;

        @Inject
        public Factory( final RepositoryAdmin repositoryAdmin )
        {
            // VALIDATE
            this.repositoryAdmin = repositoryAdmin;
        }

        public FlowType type()
        {
            return TYPE;
        }

        public WatchBundlesForObrRepositories create( final Configuration configuration )
        {
            return new WatchBundlesForObrRepositories(
                flowName( String.format( "%s::%d", type(), counter++ ) ),
                repositoryAdmin
            );
        }

        @Override
        public String toString()
        {
            return String.format( "Flow factory for type [%s] (%d instances)", type(), counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "flowType", TYPE.toString() );

            return attributes;
        }

    }

    public static class DefaultTriggerFactory
        extends BundleContentWatcher.Factory
    {

        public static final TriggerType TYPE = triggerType( WatchBundlesForObrRepositories.class,
                                                            TriggerName.DEFAULT_TRIGGER_SUFFIX
        );

        @Inject
        public DefaultTriggerFactory( final BundleContext bundleContext )
        {
            super( bundleContext );
        }

        public TriggerType type()
        {
            return TYPE;
        }

        public BundleContentWatcher create( final Configuration configuration,
                                            final ExecutionTarget target )
        {
            return super.create(
                immutableConfiguration(
                    configuration,
                    configurationProperty( PATH, "META-INF/obr" ),
                    configurationProperty( PATTERN, "*.xml" )
                ),
                target
            );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "triggerType", TYPE.toString() );

            return attributes;
        }

    }

}