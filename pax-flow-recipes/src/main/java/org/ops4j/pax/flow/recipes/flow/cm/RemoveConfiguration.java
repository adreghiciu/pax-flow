package org.ops4j.pax.flow.recipes.flow.cm;

import static java.lang.String.*;
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
public class RemoveConfiguration
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( RemoveConfiguration.class );

    public static final PropertyName CONFIGURATION = propertyName( "configuration" );
    public static final PropertyName PID = propertyName( "pid" );
    public static final PropertyName FACTORY_PID = propertyName( "factoryPid" );

    private final ConfigurationAdmin configurationAdmin;

    @Inject
    public RemoveConfiguration( final ConfigurationAdmin configurationAdmin )
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

        String filter = format( "(service.pid=%s)", pid );
        if( factoryPid != null )
        {
            filter = format( "&%s(service.factoryPid.marker=%s)", filter, factoryPid );
        }

        final Configuration[] configurations = configurationAdmin.listConfigurations( filter );

        if( configurations == null || configurations.length == 0 )
        {
            LOG.debug(
                format(
                    "Could not find a configuration with pid %s%s. Not deleted.",
                    pid,
                    factoryPid == null ? "" : format( " and factory pid %s", factoryPid )
                )
            );
        }
        else
        {
            for( Configuration configuration : configurations )
            {
                configuration.delete();
            }

            LOG.info(
                format(
                    "Deleted configuration with pid %s%s.",
                    pid,
                    factoryPid == null ? "" : format( " and factory pid %s", factoryPid )
                )
            );
        }
    }

    @Override
    public String toString()
    {
        return "Delete configuration from config admin";
    }
}