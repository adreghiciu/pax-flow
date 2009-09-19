package org.ops4j.pax.flow.runtime.setup.internal;

import com.google.inject.Inject;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowName;
import static org.ops4j.pax.flow.api.FlowName.*;
import org.ops4j.pax.flow.api.FlowType;
import org.ops4j.pax.flow.api.Transformer;
import org.ops4j.pax.flow.api.helpers.ImmutableFlow;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ScheduleJobFlow
    extends ImmutableFlow
    implements Flow
{

    public ScheduleJobFlow( final FlowName flowName,
                            final Transformer transformer )
    {
        super(
            flowName,
            new ScheduleJob( transformer )
        );
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = FlowType.flowType( "scheduleJob" );

        private final Transformer m_transformer;

        private long m_counter;

        @Inject
        public Factory( final Transformer transformer )
        {
            // VALIDATE
            m_transformer = transformer;
        }

        public FlowType type()
        {
            return TYPE;
        }

        public ScheduleJobFlow create( final Configuration configuration )
        {
            return new ScheduleJobFlow(
                flowName( String.format( "%s::%d", type(), m_counter++ ) ),
                m_transformer
            );
        }

    }

}
