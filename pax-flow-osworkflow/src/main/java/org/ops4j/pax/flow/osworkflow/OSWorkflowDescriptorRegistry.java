package org.ops4j.pax.flow.osworkflow;

import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public interface OSWorkflowDescriptorRegistry
{

    WorkflowDescriptor get( OSWorkflowName name );
    
}
