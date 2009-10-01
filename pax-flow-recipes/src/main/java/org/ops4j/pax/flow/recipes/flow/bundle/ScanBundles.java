package org.ops4j.pax.flow.recipes.flow.bundle;

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

    public static final PropertyName ADDED = PropertyName.propertyName( "added" );
    public static final PropertyName MODIFIED = PropertyName.propertyName( "modified" );
    public static final PropertyName DELETED = PropertyName.propertyName( "deleted" );

    private final ProvisionService m_provisionService;
    private final String m_url;

    public ScanBundles( final FlowName flowName,
                        final ProvisionService provisionService,
                        final String url )
    {
        super( flowName );
        m_provisionService = provisionService;
        m_url = url;
    }

    @Override
    protected void run( final ExecutionContext context )
        throws Exception
    {
        final List<ScannedBundle> scannedBundles = m_provisionService.scan( m_url );

        // TODO figure out how to use generics
        final Map<URL, Long> previousUrls = new HashMap<URL, Long>(
            typedExecutionContext( context ).optional( TIMESTAMPS, Map.class, new HashMap<URL, Long>() )
        );
        final Map<URL, Long> newUrls = new HashMap<URL, Long>();

        final Collection<URL> added = new ArrayList<URL>();
        final Collection<URL> modified = new ArrayList<URL>();
        final Collection<URL> deleted = new ArrayList<URL>( previousUrls.keySet() );

        for( ScannedBundle scannedBundle : scannedBundles )
        {

            // TODO shall it fails at first malformed url?
            final URL url = new URL( scannedBundle.getLocation() );
            // TODO support for proxy?
            final URLConnection urlConnection = url.openConnection();

            newUrls.put( url, urlConnection.getLastModified() );

            final Long timestamp = previousUrls.get( url );

            if( timestamp == null )
            {
                added.add( url );
            }
            else if( urlConnection.getLastModified() > timestamp )
            {
                modified.add( url );
            }
            deleted.remove( url );
        }

        context.add( persistentExecutionProperty( TIMESTAMPS, newUrls ) );
        context.add( executionProperty( URLS, newUrls ) );
        context.add( executionProperty( ADDED, added ) );
        context.add( executionProperty( MODIFIED, modified ) );
        context.add( executionProperty( DELETED, deleted ) );

        final String message = format(
            "Found %d bundles (%d added,%d modified,%d deleted) scanning url [%s]",
            newUrls.size(),
            added.size(), modified.size(), deleted.size(),
            m_url
        );
        if( added.size() > 0 || modified.size() > 0 || deleted.size() > 0 )
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

        private final ProvisionService m_provisionService;

        private long m_counter;

        @Inject
        public Factory( final ProvisionService provisionService )
        {
            // VALIDATE
            m_provisionService = provisionService;
        }

        public FlowType type()
        {
            return TYPE;
        }

        public ScanBundles create( final Configuration configuration )
        {
            final TypedConfiguration cfg = typedConfiguration( configuration );

            return new ScanBundles(
                flowName( format( "%s::%d", type(), m_counter++ ) ),
                m_provisionService,
                cfg.mandatory( URL, String.class )
            );
        }

        @Override
        public String toString()
        {
            return format( "Flow factory for type [%s] (%d instances)", type(), m_counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "flowType", TYPE.toString() );

            return attributes;
        }

    }

}