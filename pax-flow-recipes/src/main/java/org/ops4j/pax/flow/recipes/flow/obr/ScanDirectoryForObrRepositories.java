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

import java.io.File;
import static java.lang.String.*;
import java.util.HashMap;
import java.util.Map;
import com.google.inject.Inject;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.obr.RepositoryAdmin;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowName;
import static org.ops4j.pax.flow.api.FlowName.*;
import org.ops4j.pax.flow.api.FlowType;
import static org.ops4j.pax.flow.api.FlowType.*;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
import org.ops4j.pax.flow.api.helpers.ForEachFlow;
import org.ops4j.pax.flow.api.helpers.SequentialFlow;
import org.ops4j.pax.flow.api.helpers.TypedConfiguration;
import static org.ops4j.pax.flow.api.helpers.TypedConfiguration.*;
import org.ops4j.pax.flow.recipes.flow.basic.ListDirectory;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ScanDirectoryForObrRepositories
    extends SequentialFlow
    implements Flow
{

    public ScanDirectoryForObrRepositories( final FlowName flowName,
                                           final RepositoryAdmin repositoryAdmin,
                                           final File directory,
                                           final String[] includes,
                                           final String[] excludes )
    {
        super(
            flowName,
            new ListDirectory( directory, includes, excludes ),
            new ForEachFlow(
                ListDirectory.ADDED, AddObrRepository.REPOSITORY_URL,
                new SequentialFlow(
                    flowName( format( "%s::%s", flowName, "Added" ) ), // TODO do we need a name?
                    new AddObrRepository( repositoryAdmin )
                )
            ),
            new ForEachFlow(
                ListDirectory.MODIFIED, AddObrRepository.REPOSITORY_URL,
                new SequentialFlow(
                    flowName( format( "%s::%s", flowName, "Modified" ) ), // TODO do we need a name?
                    new AddObrRepository( repositoryAdmin )
                )
            ),
            new ForEachFlow(
                ListDirectory.DELETED, RemoveObrRepository.REPOSITORY_URL,
                new SequentialFlow(
                    flowName( format( "%s::%s", flowName, "Deleted" ) ), // TODO do we need a name?
                    new RemoveObrRepository( repositoryAdmin )
                )
            )
        );
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( ScanDirectoryForObrRepositories.class );

        public static final PropertyName DIRECTORY = propertyName( "directory" );
        public static final PropertyName INCLUDES = propertyName( "includes" );
        public static final PropertyName EXCLUDES = propertyName( "excludes" );

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

        public ScanDirectoryForObrRepositories create( final Configuration configuration )
        {
            final TypedConfiguration cfg = typedConfiguration( configuration );

            return new ScanDirectoryForObrRepositories(
                flowName( format( "%s::%d", type(), counter++ ) ),
                repositoryAdmin,
                new File( cfg.mandatory( DIRECTORY, String.class ) ),
                cfg.optional( INCLUDES, String[].class ),
                cfg.optional( EXCLUDES, String[].class )
            );
        }

        @Override
        public String toString()
        {
            return format( "Flow factory for type [%s] (%d instances)", type(), counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "flowType", TYPE.toString() );

            return attributes;
        }

    }

}