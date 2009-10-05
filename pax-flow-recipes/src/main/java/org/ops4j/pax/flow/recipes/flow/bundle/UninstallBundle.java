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
public class UninstallBundle
    extends CancelableFlow
    implements Flow
{

    public static final PropertyName URL = propertyName( "url" );

    private final BundleContext bundleContext;
    private final PropertyName urlPropertyName;

    public UninstallBundle( final BundleContext bundleContext )
    {
        // VALIDATE
        this( bundleContext, null );
    }

    public UninstallBundle( final BundleContext bundleContext,
                            final PropertyName urlPropertyName )
    {
        // VALIDATE
        this.bundleContext = bundleContext;
        this.urlPropertyName = urlPropertyName == null ? URL : urlPropertyName;
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final URL url = typedExecutionContext( context ).mandatory( urlPropertyName, URL.class );
        Bundle bundle = BundleUtils.getBundle( bundleContext, url.toExternalForm() );
        if( bundle != null )
        {
            bundle.uninstall();
        }
    }

    @Override
    public String toString()
    {
        return "Uninstalls a bundle";
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( UninstallBundle.class );

        public static final PropertyName URL_PROPERTY = propertyName( "urlPropertyName" );

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

        public UninstallBundle create( final Configuration configuration )
        {
            final TypedConfiguration cfg = typedConfiguration( configuration );

            final String urlProperty = cfg.optional( URL_PROPERTY, String.class );

            return new UninstallBundle(
                bundleContext,
                urlProperty == null ? null : propertyName( urlProperty )
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