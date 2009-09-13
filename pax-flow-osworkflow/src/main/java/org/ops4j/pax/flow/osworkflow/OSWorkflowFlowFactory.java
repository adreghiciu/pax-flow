package org.ops4j.pax.flow.osworkflow;

import com.google.inject.Inject;
import com.opensymphony.workflow.Workflow;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.base.FlowName;
import org.ops4j.pax.flow.osworkflow.internal.OSWorkflowFlow;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class OSWorkflowFlowFactory
    implements FlowFactory
{

    private Workflow m_workflow;

    @Inject
    public OSWorkflowFlowFactory( final Workflow workflow )
    {
        m_workflow = workflow;
    }

    public Flow create( final FlowName name,
                        final ExecutionContext context )
        throws Exception
    {
        return new OSWorkflowFlow( m_workflow, name, context );
    }
}
