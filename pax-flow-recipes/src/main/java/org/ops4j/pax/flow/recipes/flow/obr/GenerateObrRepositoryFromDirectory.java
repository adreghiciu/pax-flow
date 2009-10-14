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
public class GenerateObrRepositoryFromDirectory
    extends SequentialFlow
    implements Flow
{

    public GenerateObrRepositoryFromDirectory( final FlowName flowName,
                                               final File directory,
                                               final String[] includes,
                                               final String[] excludes,
                                               final File repositoryFile )
    {
        super(
            flowName,
            new LoadObrRepository( repositoryFile == null ? new File( directory, "obr.xml" ) : repositoryFile ),
            new ListDirectory( directory, includes, excludes ),
            new ForEachFlow(
                ListDirectory.NEW, AddObrResource.BUNDLE,
                new AddObrResource()
            ),
            new ForEachFlow(
                ListDirectory.MODIFIED, AddObrResource.BUNDLE,
                new AddObrResource()
            ),
            new ForEachFlow(
                ListDirectory.REMOVED, RemoveObrResource.BUNDLE,
                new RemoveObrResource()
            ),
            new SaveObrRepository( repositoryFile == null ? new File( directory, "obr.xml" ) : repositoryFile )
        );
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( GenerateObrRepositoryFromDirectory.class );

        public static final PropertyName DIRECTORY = propertyName( "directory" );
        public static final PropertyName INCLUDES = propertyName( "includes" );
        public static final PropertyName EXCLUDES = propertyName( "excludes" );
        public static final PropertyName REPOSITORY = propertyName( "repository" );

        private long counter;

        public FlowType type()
        {
            return TYPE;
        }

        public GenerateObrRepositoryFromDirectory create( final Configuration configuration )
        {
            final TypedConfiguration cfg = typedConfiguration( configuration );

            return new GenerateObrRepositoryFromDirectory(
                flowName( format( "%s::%d", type(), counter++ ) ),
                cfg.mandatory( DIRECTORY, File.class ),
                cfg.optional( INCLUDES, String[].class ),
                cfg.optional( EXCLUDES, String[].class ),
                cfg.optional( REPOSITORY, File.class )
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