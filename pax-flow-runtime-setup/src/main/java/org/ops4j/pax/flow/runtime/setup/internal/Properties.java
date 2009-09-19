package org.ops4j.pax.flow.runtime.setup.internal;

import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class Properties
{

    public static final PropertyName SERVICE = propertyName( "service" );
    public static final PropertyName JOB = propertyName( "job" );
    
    public static final PropertyName WATCHED_SERVICE_TYPE = propertyName( "watchedServiceType" );
    public static final PropertyName WATCHED_SERVICE_FILTER = propertyName( "watchedServiceFilter" );

}
