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

package org.ops4j.pax.flow.recipes.flow.basic;

import java.io.File;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.ExecutionContext;
import static org.ops4j.pax.flow.api.ExecutionProperty.*;
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
public class ReplaceLinkFileWithURL
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( ReplaceLinkFileWithURL.class );

    public static final PropertyName LINK_FILE = propertyName( "file" );

    private final PropertyName linkFilePropertyName;

    public ReplaceLinkFileWithURL()
    {
        this( LINK_FILE );
    }

    public ReplaceLinkFileWithURL( final PropertyName linkFilePropertyName )
    {
        this.linkFilePropertyName = linkFilePropertyName;
    }

    @Override
    protected void run( final ExecutionContext context )
        throws Exception
    {

        final TypedExecutionContext executionContext = typedExecutionContext( context );

        // try to transform files with a "link" extension to an "link:" URL
        final File file = executionContext.optional( linkFilePropertyName, File.class );
        if( file != null )
        {
            if( file.getName().endsWith( ".link" ) )
            {
                String urlAsString = null;
                try
                {
                    urlAsString = "link:" + file.toURI().toURL().toExternalForm();
                    final URL url = new URL( urlAsString );
                    context.add( executionProperty( linkFilePropertyName, url ) );
                }
                catch( Exception ignore )
                {
                    // probably "link" protocol is not installed
                    LOG.debug(
                        String.format(
                            "Could not transform [%s] into an [%s]. Reason [%s].",
                            file.getPath(), urlAsString, ignore.getMessage()
                        )
                    );
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return "Replace link file with url";
    }

}