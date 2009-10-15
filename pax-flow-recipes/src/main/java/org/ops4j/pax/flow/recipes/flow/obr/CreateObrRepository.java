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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.impl.bundle.obr.resource.RepositoryImpl;
import org.ops4j.pax.flow.api.ExecutionContext;
import static org.ops4j.pax.flow.api.ExecutionProperty.*;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class CreateObrRepository
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( CreateObrRepository.class );

    public static final PropertyName REPOSITORY = propertyName( "repository" );

    private final File repository;

    public CreateObrRepository( final File repository )
    {
        this.repository = repository;
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final RepositoryImpl repo = new RepositoryImpl( repository.toURI().toURL() );

        context.add( executionProperty( REPOSITORY, repo ) );
    }

    @Override
    public String toString()
    {
        return "Creates an OBR repository";
    }

}