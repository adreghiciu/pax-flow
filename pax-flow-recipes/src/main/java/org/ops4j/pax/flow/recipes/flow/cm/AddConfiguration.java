package org.ops4j.pax.flow.recipes.flow.cm;

import static java.lang.String.*;
import java.util.Hashtable;
import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.pax.flow.api.ExecutionContext;
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
public class AddConfiguration
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( AddConfiguration.class );

    public static final PropertyName CONFIGURATION = propertyName( "configuration" );
    public static final PropertyName PID = propertyName( "pid" );
    public static final PropertyName FACTORY_PID = propertyName( "factoryPid" );

    private final ConfigurationAdmin configurationAdmin;

    @Inject
    public AddConfiguration( final ConfigurationAdmin configurationAdmin )
    {
        // VALIDATE
        this.configurationAdmin = configurationAdmin;
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final TypedExecutionContext executionContext = typedExecutionContext( context );
        final String pid = executionContext.mandatory( PID, String.class );
        final String factoryPid = executionContext.optional( FACTORY_PID, String.class );
        final Hashtable<String,Object> properties = new Hashtable<String, Object>();
        properties.putAll( executionContext.mandatory( CONFIGURATION, Hashtable.class ) );

        String filter = String.format( "(service.pid=%s)", pid );
        if( factoryPid != null )
        {
            filter = String.format( "&%s(service.factoryPid.marker=%s)", filter, factoryPid );
        }

        final Configuration[] configurations = configurationAdmin.listConfigurations( filter );

        if( configurations == null || configurations.length == 0 )
        {
            final Configuration configuration;

            if( factoryPid == null )
            {
                configuration = configurationAdmin.getConfiguration( pid, null );
            }
            else
            {
                configuration = configurationAdmin.createFactoryConfiguration( pid, null );
            }

            if( factoryPid != null )
            {
                properties.put( "service.factoryPid.marker", factoryPid );
            }
            configuration.update( properties );

            LOG.info(
                format(
                    "Added configuration with pid %s%s.",
                    pid,
                    factoryPid == null ? "" : format( " and factory pid %s", factoryPid )
                )
            );
        }
        else
        {
            LOG.debug(
                format(
                    "Configuration with pid %s%s already exists. Skipped.",
                    pid,
                    factoryPid == null ? "" : format( " and factory pid %s", factoryPid )
                )
            );
        }
    }

    @Override
    public String toString()
    {
        return "Schedule job available in execution context";
    }
}