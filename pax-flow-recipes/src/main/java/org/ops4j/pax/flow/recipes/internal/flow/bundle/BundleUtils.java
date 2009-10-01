package org.ops4j.pax.flow.recipes.internal.flow.bundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class BundleUtils
{

    private BundleUtils()
    {
        // utility class
    }

    public static Bundle getBundle( final BundleContext bundleContext,
                                    final String location )
    {
        if( location != null )
        {
            for( Bundle bundle : bundleContext.getBundles() )
            {
                if( location.equals( bundle.getLocation() ) )
                {
                    return bundle;
                }
            }
        }

        return null;
    }

}
