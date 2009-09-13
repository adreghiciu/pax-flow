package org.ops4j.pax.flow.api.helper;

import org.ops4j.pax.flow.api.Job;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.base.JobName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ImmutableJob
    implements Job
{

    private final JobName m_jobName;

    private final TriggerFactory m_triggerFactory;

    private final FlowFactory m_flowFactory;

    public ImmutableJob( final JobName jobName,
                         final TriggerFactory triggerFactory,
                         final FlowFactory flowFactory )
    {
        m_jobName = jobName;
        m_triggerFactory = triggerFactory;
        m_flowFactory = flowFactory;
    }

    public JobName name()
    {
        return m_jobName;
    }

    public TriggerFactory triggerFactory()
    {
        return m_triggerFactory;
    }

    public FlowFactory flowFactory()
    {
        return m_flowFactory;
    }

    public static ImmutableJob job( final JobName jobName,
                                    final TriggerFactory triggerFactory,
                                    final FlowFactory flowFactory )
    {
        return new ImmutableJob( jobName, triggerFactory, flowFactory );
    }

}
