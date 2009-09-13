package org.ops4j.pax.flow.osworkflow.internal;

import java.util.HashMap;
import java.util.Map;
import com.opensymphony.workflow.Workflow;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.base.AttributeName;
import org.ops4j.pax.flow.api.base.FlowName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class OSWorkflowFlow
    implements Flow
{

    private final Workflow m_workflow;
    private final FlowName m_name;
    private final ExecutionContext m_context;
    
    private long m_workflowId;

    public OSWorkflowFlow( final Workflow workflow,
                           final FlowName name,
                           final ExecutionContext context )
    {
        m_workflow = workflow;
        m_name = name;
        m_context = context;
    }

    public FlowName name()
    {
        return m_name;
    }

    public void execute()
        throws Exception
    {
        // TODO maybe we should allow customization of initial action
        m_workflowId = m_workflow.initialize( m_name.stringValue(), 1, asMap( m_context ) );
    }

    public void cancel()
        throws Exception
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    private Map<String, Object> asMap( final ExecutionContext context )
    {
        final Map<String, Object> map = new HashMap<String, Object>();
        for( Map.Entry<AttributeName, Object> entry : context.getAll().entrySet() )
        {
            map.put( entry.getKey().stringValue(), entry.getValue() );
        }
        return map;
    }


}
