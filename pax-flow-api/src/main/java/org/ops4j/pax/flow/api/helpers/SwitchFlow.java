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

    private final SwitchCase[] m_cases;
    private final PropertyName m_switchPropertyName;

    public SwitchFlow( final PropertyName switchPropertyName,
                       final SwitchCase... cases )
    {
        super();

        // VALIDATE
        m_switchPropertyName = switchPropertyName;

        if( cases == null )
        {
            m_cases = new SwitchCase[0];
        }
        else
        {
            m_cases = cases;
        }
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final Object value = context.get( m_switchPropertyName );

        if( value != null )
        {
            for( SwitchCase switchCase : m_cases )
            {
                if( value.equals( switchCase.value() ) )
                {
                    LOG.debug(
                        String.format(
                            "[%s] is equal to [%s]. Executing %s",
                            m_switchPropertyName, switchCase.value(), switchCase.flow()
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

        private final Object m_value;
        private final Flow m_flow;

        public SwitchCase( final Object value,
                           final Flow flow )
        {
            m_value = value;
            m_flow = flow;
        }

        public Flow flow()
        {
            return m_flow;
        }

        public Object value()
        {
            return m_value;
        }

        public static SwitchCase switchCase( final Object value,
                                             final Flow flow )
        {
            return new SwitchCase( value, flow );
        }
    }


}