package org.ops4j.pax.flow.recipes.flow.paxscanner;

import static java.lang.String.*;
import java.util.HashMap;
import java.util.Map;
import com.google.inject.Inject;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowName;
import static org.ops4j.pax.flow.api.FlowName.*;
import org.ops4j.pax.flow.api.FlowType;
import static org.ops4j.pax.flow.api.FlowType.*;
import org.ops4j.pax.flow.api.helpers.ForEachFlow;
import org.ops4j.pax.flow.api.helpers.SequentialFlow;
import org.ops4j.pax.flow.api.helpers.TypedConfiguration;
import static org.ops4j.pax.flow.api.helpers.TypedConfiguration.*;
import org.ops4j.pax.flow.recipes.flow.bundle.InstallOrUpdateBundle;
import org.ops4j.pax.flow.recipes.flow.bundle.UninstallBundle;
import org.ops4j.pax.scanner.ProvisionService;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class SyncBundlesWithScanner
    extends SequentialFlow
    implements Flow
{

    public SyncBundlesWithScanner( final FlowName flowName,
                                   final BundleContext bundleContext,
                                   final ProvisionService provisionService,
                                   final String url,
                                   final boolean autoStart,
                                   final long startLevel )
    {
        super(
            flowName,
            new ScanBundles(
                flowName( format( "%s::%s", flowName, "Scan" ) ),
                provisionService,
                url
            ),
            new ForEachFlow(
                ScanBundles.NEW, InstallOrUpdateBundle.URL,
                new InstallOrUpdateBundle( bundleContext, autoStart, startLevel )
            ),
            new ForEachFlow(
                ScanBundles.MODIFIED, InstallOrUpdateBundle.URL,
                new InstallOrUpdateBundle( bundleContext, false, startLevel )
            ),
            new ForEachFlow(
                ScanBundles.REMOVED, UninstallBundle.URL,
                new UninstallBundle( bundleContext )
            )
        );
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( SyncBundlesWithScanner.class );

        private final BundleContext bundleContext;
        private final ProvisionService provisionService;

        private long counter;

        @Inject
        public Factory( final BundleContext bundleContext,
                        final ProvisionService provisionService )
        {
            // VALIDATE
            this.bundleContext = bundleContext;
            this.provisionService = provisionService;
        }

        public FlowType type()
        {
            return TYPE;
        }

        public SyncBundlesWithScanner create( final Configuration configuration )
        {
            final TypedConfiguration cfg = typedConfiguration( configuration );

            final String url = cfg.mandatory( ScanBundles.Factory.URL, String.class );
            final Boolean autoStart = cfg.optional( InstallOrUpdateBundle.Factory.AUTO_START, Boolean.class, true );
            final Long startLevel = cfg.optional( InstallOrUpdateBundle.Factory.START_LEVEL, Long.class, 5L );
            
            return new SyncBundlesWithScanner(
                flowName( format( "%s::%d", type(), counter++ ) ),
                bundleContext,
                provisionService,
                url,
                autoStart,
                startLevel
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