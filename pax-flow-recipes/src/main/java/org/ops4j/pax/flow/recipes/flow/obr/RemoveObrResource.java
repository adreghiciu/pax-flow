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
import java.net.URL;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.impl.bundle.obr.resource.BundleInfo;
import org.osgi.impl.bundle.obr.resource.RepositoryImpl;
import org.osgi.impl.bundle.obr.resource.ResourceImpl;
import org.osgi.service.obr.Resource;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import org.ops4j.pax.flow.api.helpers.TypedExecutionContext;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class RemoveObrResource
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( RemoveObrResource.class );

    public static final PropertyName REPOSITORY = propertyName( "repository" );
    public static final PropertyName BUNDLE = propertyName( "bundle" );

    public void run( final ExecutionContext context )
        throws Exception
    {
        final TypedExecutionContext executionContext = typedExecutionContext( context );
        final RepositoryImpl repo = executionContext.mandatory( REPOSITORY, RepositoryImpl.class );
        final File bundle = executionContext.mandatory( BUNDLE, File.class );

        final URL bundleUrl = bundle.toURI().toURL();

        final Iterator iterator = repo.getResourceList().iterator();
        while( iterator.hasNext() )
        {
            final Resource entry = (Resource) iterator.next();
            if( bundleUrl.equals( entry.getURL() ) )
            {
                iterator.remove();
            }
        }

        LOG.debug( String.format( "Removed [%s] to repository [%s]", bundle.getPath(), repo.getURL() ) );
    }

    @Override
    public String toString()
    {
        return "Remove a resource from an obr repository";
    }

}