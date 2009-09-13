package org.ops4j.pax.flow.osworkflow.helper;

import com.opensymphony.workflow.loader.WorkflowDescriptor;
import org.ops4j.pax.flow.osworkflow.OSWorkflowDescriptor;
import org.ops4j.pax.flow.osworkflow.OSWorkflowName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ImmutableOSWorkflowDescriptor
    implements OSWorkflowDescriptor
{

    private final OSWorkflowName m_name;
    private final WorkflowDescriptor m_descriptor;

    public ImmutableOSWorkflowDescriptor( final OSWorkflowName name,
                                          final WorkflowDescriptor descriptor )
    {
        m_name = name;
        m_descriptor = descriptor;
    }

    public OSWorkflowName name()
    {
        return m_name;
    }

    public WorkflowDescriptor descriptor()
    {
        return m_descriptor;
    }
}
