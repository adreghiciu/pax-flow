package org.ops4j.pax.flow.api.helper;

import org.ops4j.pax.flow.api.Job;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.api.Workflow;
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

    private final Workflow m_workflow;

    public ImmutableJob( final JobName jobName,
                         final TriggerFactory triggerFactory,
                         final Workflow workflow )
    {
        m_jobName = jobName;
        m_triggerFactory = triggerFactory;
        m_workflow = workflow;
    }

    public JobName name()
    {
        return m_jobName;
    }

    public TriggerFactory factory()
    {
        return m_triggerFactory;
    }

    public Workflow workflow()
    {
        return m_workflow;
    }

    public static ImmutableJob job( final JobName jobName,
                                    final TriggerFactory triggerFactory,
                                    final Workflow workflow )
    {
        return new ImmutableJob( jobName, triggerFactory, workflow );
    }

}
