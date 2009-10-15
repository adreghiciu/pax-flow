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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.impl.bundle.obr.resource.RepositoryImpl;
import org.osgi.impl.bundle.obr.resource.ResourceImpl;
import org.osgi.impl.bundle.obr.resource.Tag;
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
public class SaveObrRepository
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( SaveObrRepository.class );

    public static final PropertyName REPOSITORY = propertyName( "repository" );

    private final File repository;

    public SaveObrRepository( final File repository )
    {
        this.repository = repository;
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final RepositoryImpl repo = typedExecutionContext( context ).mandatory( REPOSITORY, RepositoryImpl.class );

        Tag tag = new Tag( "repository" );
        tag.addAttribute( "lastmodified", new Date() );
        tag.addAttribute( "name", repo.getName() );

        for( final Object o : repo.getResourceList() )
        {
            ResourceImpl resource = (ResourceImpl) o;
            tag.addContent( resource.toXML() );
        }

        final PrintWriter pw = new PrintWriter( new FileWriter( repository ) );

        pw.println( "<?xml version='1.0' encoding='utf-8'?>" );
        pw.println( "<?xml-stylesheet type='text/xsl' href='http://www2.osgi.org/www/obr2html.xsl'?>" );

        tag.print( 0, pw );

        pw.close();
    }

    @Override
    public String toString()
    {
        return "Saves an OBR repository to a (XML) file";
    }

}