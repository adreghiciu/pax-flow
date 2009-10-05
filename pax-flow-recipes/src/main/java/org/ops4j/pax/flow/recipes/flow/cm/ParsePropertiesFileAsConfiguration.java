package org.ops4j.pax.flow.recipes.flow.cm;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
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
public class ParsePropertiesFileAsConfiguration
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( ParsePropertiesFileAsConfiguration.class );

    public static final PropertyName FILE = propertyName( "file" );

    public static final PropertyName CONFIGURATION = propertyName( "configuration" );

    @Override
    protected void run( final ExecutionContext context )
        throws Exception
    {

        final TypedExecutionContext executionContext = typedExecutionContext( context );

        InputStream source = null;

        // try to transform files with a "link" extension to an "link:" URL
        final File file = executionContext.optional( FILE, File.class );
        if( file != null )
        {
            if( file.getName().endsWith( ".link" ) )
            {
                URL url = null;
                String urlAsString = null;
                try
                {
                    urlAsString = "link:" + file.toURI().toURL().toExternalForm();
                    url = new URL( urlAsString );
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
                if( url != null )
                {
                    source = url.openStream();
                }
            }
        }

        if( source == null )
        {
            source = executionContext.mandatory( FILE, InputStream.class );
        }

        final Properties properties = new Properties();
        properties.load( source );

        context.add( executionProperty( CONFIGURATION, properties ) );
    }

    @Override
    public String toString()
    {
        return "Parse properties file as configuration";
    }

}