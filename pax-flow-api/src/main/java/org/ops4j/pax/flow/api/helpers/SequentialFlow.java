package org.ops4j.pax.flow.api.helpers;

import static java.lang.String.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class SequentialFlow
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( SequentialFlow.class );

    private final Flow[] flows;

    public SequentialFlow( final FlowName flowName,
                           final Flow... flows )
    {
        super( flowName );

        if( flows == null )
        {
            this.flows = new Flow[0];
        }
        else
        {
            this.flows = flows;
        }
    }

    public void run( final ExecutionContext context )
    {
        final long startTime = System.currentTimeMillis();
        LOG.debug( format( "Starting flow [%s]", name() ) );

        try
        {
            for( Flow flow : flows )
            {
                final long duration = System.currentTimeMillis() - startTime;
                LOG.debug(
                    format(
                        "Executing flow [%s:%s] after %d millis from start", name(), flow, duration
                    )
                );
                flow.execute( context );
            }
        }
        catch( Exception e )
        {
            final long duration = System.currentTimeMillis() - startTime;
            LOG.warn( format( "Execution of flow [%s] failed after %d millis ", name(), duration ), e );
        }
        final long duration = System.currentTimeMillis() - startTime;
        LOG.debug( format( "Flow [%s] executed successfully in %d millis", name(), duration ) );
    }

}
