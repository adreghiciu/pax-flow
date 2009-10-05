package org.ops4j.pax.flow.recipes.flow.bundle;

import static java.lang.String.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import com.google.inject.Inject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.ExecutionProperty;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowType;
import static org.ops4j.pax.flow.api.FlowType.*;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import org.ops4j.pax.flow.api.helpers.TypedConfiguration;
import static org.ops4j.pax.flow.api.helpers.TypedConfiguration.*;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;
import org.ops4j.pax.flow.recipes.internal.flow.bundle.BundleUtils;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class InstallOrUpdateBundle
    extends CancelableFlow
    implements Flow
{

    public static final PropertyName URL = propertyName( "url" );
    public static final PropertyName BUNDLE = propertyName( "bundle" );

    private final BundleContext bundleContext;
    private final PropertyName urlPropertyName;
    private final PropertyName bundlePropertyName;
    private final boolean autoStart;

    @Inject
    public InstallOrUpdateBundle( final BundleContext bundleContext,
                                  final boolean autoStart )
    {
        // VALIDATE
        this( bundleContext, null, null, autoStart );
    }

    @Inject
    public InstallOrUpdateBundle( final BundleContext bundleContext,
                                  final PropertyName urlPropertyName,
                                  final PropertyName bundlePropertyName,
                                  final boolean autoStart )
    {
        // VALIDATE
        this.bundleContext = bundleContext;
        this.urlPropertyName = urlPropertyName == null ? URL : urlPropertyName;
        this.bundlePropertyName = bundlePropertyName == null ? BUNDLE : bundlePropertyName;
        this.autoStart = autoStart;
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final URL url = typedExecutionContext( context ).mandatory( urlPropertyName, URL.class );
        Bundle bundle = BundleUtils.getBundle( bundleContext, url.toExternalForm() );
        if( bundle == null )
        {
            bundle = bundleContext.installBundle( url.toExternalForm(), url.openStream() );
            if( autoStart )
            {
                bundle.start();
            }
        }
        context.add( ExecutionProperty.executionProperty( bundlePropertyName, bundle ) );
    }

    @Override
    public String toString()
    {
        return "Installs a bundle";
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( InstallOrUpdateBundle.class );

        public static final PropertyName URL_PROPERTY = propertyName( "urlPropertyName" );
        public static final PropertyName BUNDLE_PROPERTY = propertyName( "bundlePropertyName" );
        public static final PropertyName AUTO_START = propertyName( "autoStart" );

        private final BundleContext bundleContext;

        private long counter;

        @Inject
        public Factory( final BundleContext bundleContext )
        {
            // VALIDATE
            this.bundleContext = bundleContext;
        }

        public FlowType type()
        {
            return TYPE;
        }

        public InstallOrUpdateBundle create( final Configuration configuration )
        {
            final TypedConfiguration cfg = typedConfiguration( configuration );

            final String urlProperty = cfg.optional( URL_PROPERTY, String.class );
            final String bundleProperty = cfg.optional( BUNDLE_PROPERTY, String.class );
            String autoStart = cfg.optional( AUTO_START, String.class );
            if( "yes".equalsIgnoreCase( autoStart ) )
            {
                autoStart = "true";
            }

            return new InstallOrUpdateBundle(
                bundleContext,
                urlProperty == null ? null : propertyName( urlProperty ),
                bundleProperty == null ? null : propertyName( bundleProperty ),
                autoStart == null ? true : Boolean.valueOf( autoStart )
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