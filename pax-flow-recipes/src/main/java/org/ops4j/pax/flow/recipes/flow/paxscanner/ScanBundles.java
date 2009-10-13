package org.ops4j.pax.flow.recipes.flow.paxscanner;

import static java.lang.String.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.ExecutionContext;
import static org.ops4j.pax.flow.api.ExecutionProperty.*;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowName;
import static org.ops4j.pax.flow.api.FlowName.*;
import org.ops4j.pax.flow.api.FlowType;
import static org.ops4j.pax.flow.api.FlowType.*;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import org.ops4j.pax.flow.api.helpers.TypedConfiguration;
import static org.ops4j.pax.flow.api.helpers.TypedConfiguration.*;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;
import org.ops4j.pax.scanner.ProvisionService;
import org.ops4j.pax.scanner.ScannedBundle;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ScanBundles
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( ScanBundles.class );

    public static final PropertyName URLS = PropertyName.propertyName( "urls" );
    public static final PropertyName TIMESTAMPS = PropertyName.propertyName( "timestamps" );

    public static final PropertyName NEW = PropertyName.propertyName( "new" );
    public static final PropertyName MODIFIED = PropertyName.propertyName( "modified" );
    public static final PropertyName REMOVED = PropertyName.propertyName( "removed" );

    private final ProvisionService provisionService;
    private final String url;

    public ScanBundles( final FlowName flowName,
                        final ProvisionService provisionService,
                        final String url )
    {
        super( flowName );
        this.provisionService = provisionService;
        this.url = url;
    }

    @Override
    protected void run( final ExecutionContext context )
        throws Exception
    {
        final List<ScannedBundle> scannedBundles = provisionService.scan( url );

        // TODO figure out how to use generics
        final Map<URL, Long> previousUrls = new HashMap<URL, Long>(
            typedExecutionContext( context ).optional( TIMESTAMPS, Map.class, new HashMap<URL, Long>() )
        );
        final Map<URL, Long> urls = new HashMap<URL, Long>();

        final Collection<URL> newUrls = new ArrayList<URL>();
        final Collection<URL> modifiedUrls = new ArrayList<URL>();
        final Collection<URL> removedUrls = new ArrayList<URL>( previousUrls.keySet() );

        for( ScannedBundle scannedBundle : scannedBundles )
        {

            // TODO shall it fails at first malformed url?
            final URL url = new URL( scannedBundle.getLocation() );
            // TODO support for proxy?
            final URLConnection urlConnection = url.openConnection();

            urls.put( url, urlConnection.getLastModified() );

            final Long timestamp = previousUrls.get( url );

            if( timestamp == null )
            {
                newUrls.add( url );
            }
            else if( urlConnection.getLastModified() > timestamp )
            {
                modifiedUrls.add( url );
            }
            removedUrls.remove( url );
        }

        context.add( persistentExecutionProperty( TIMESTAMPS, urls ) );
        context.add( executionProperty( URLS, urls ) );
        context.add( executionProperty( NEW, newUrls ) );
        context.add( executionProperty( MODIFIED, modifiedUrls ) );
        context.add( executionProperty( REMOVED, removedUrls ) );

        final String message = format(
            "Found %d new, %d modified, %d deleted bundles while scanning url [%s]",
            newUrls.size(), modifiedUrls.size(), removedUrls.size(),
            url
        );
        if( newUrls.size() > 0 || modifiedUrls.size() > 0 || removedUrls.size() > 0 )
        {
            LOG.info( message );
        }
        else
        {
            LOG.debug( message );
        }
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( ScanBundles.class );

        public static final PropertyName URL = propertyName( "url" );

        private final ProvisionService provisionService;

        private long counter;

        @Inject
        public Factory( final ProvisionService provisionService )
        {
            // VALIDATE
            this.provisionService = provisionService;
        }

        public FlowType type()
        {
            return TYPE;
        }

        public ScanBundles create( final Configuration configuration )
        {
            final TypedConfiguration cfg = typedConfiguration( configuration );

            return new ScanBundles(
                flowName( format( "%s::%d", type(), counter++ ) ),
                provisionService,
                cfg.mandatory( URL, String.class )
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