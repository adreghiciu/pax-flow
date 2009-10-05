package org.ops4j.pax.flow.api.helpers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.PropertyName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class SwitchFlow
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( SwitchFlow.class );

    private final SwitchCase[] cases;
    private final PropertyName switchPropertyName;

    public SwitchFlow( final PropertyName switchPropertyName,
                       final SwitchCase... cases )
    {
        super();

        // VALIDATE
        this.switchPropertyName = switchPropertyName;

        if( cases == null )
        {
            this.cases = new SwitchCase[0];
        }
        else
        {
            this.cases = cases;
        }
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final Object value = context.get( switchPropertyName );

        if( value != null )
        {
            for( SwitchCase switchCase : cases )
            {
                if( value.equals( switchCase.value() ) )
                {
                    LOG.debug(
                        String.format(
                            "[%s] is equal to [%s]. Executing %s",
                            switchPropertyName, switchCase.value(), switchCase.flow()
                        )
                    );
                    switchCase.flow().execute( context );
                    return;
                }
            }
        }
    }

    public static class SwitchCase
    {

        private final Object value;
        private final Flow flow;

        public SwitchCase( final Object value,
                           final Flow flow )
        {
            this.value = value;
            this.flow = flow;
        }

        public Flow flow()
        {
            return flow;
        }

        public Object value()
        {
            return value;
        }

        public static SwitchCase switchCase( final Object value,
                                             final Flow flow )
        {
            return new SwitchCase( value, flow );
        }
    }


}