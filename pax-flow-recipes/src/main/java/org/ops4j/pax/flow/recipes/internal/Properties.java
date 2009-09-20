package org.ops4j.pax.flow.recipes.internal;

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

    public static final PropertyName DIRECTORY = propertyName( "directory" );
    public static final PropertyName INCLUDES = propertyName( "includes" );
    public static final PropertyName EXCLUDES = propertyName( "excludes" );

    public static final PropertyName INITIAL_DELAY = propertyName( "initialDelay" );
    public static final PropertyName REPEAT_PERIOD = propertyName( "repeatPeriod" );

}
