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
import java.net.URL;
import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.obr.RepositoryAdmin;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class RemoveObrRepository
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( RemoveObrRepository.class );

    public static final PropertyName REPOSITORY_URL = propertyName( "repositoryUrl" );

    private final RepositoryAdmin repositoryAdmin;

    @Inject
    public RemoveObrRepository( final RepositoryAdmin repositoryAdmin )
    {
        // VALIDATE
        this.repositoryAdmin = repositoryAdmin;
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final URL url = typedExecutionContext( context ).mandatory( REPOSITORY_URL, URL.class );
        repositoryAdmin.removeRepository( url );

        LOG.info( format( "Removed obr repository [%s]", url.toExternalForm() ) );
    }

    @Override
    public String toString()
    {
        return "Schedule job available in execution context";
    }
    
}