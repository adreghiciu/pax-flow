package org.ops4j.pax.flow.osworkflow.internal;

import com.google.inject.Inject;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import org.ops4j.pax.flow.osworkflow.OSWorkflowDescriptor;
import org.ops4j.pax.flow.osworkflow.OSWorkflowDescriptorRegistry;
import org.ops4j.pax.flow.osworkflow.OSWorkflowName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class DefaultOSWorkflowDescriptorRegistry
    implements OSWorkflowDescriptorRegistry
{

    private final Iterable<OSWorkflowDescriptor> m_descriptors;

    @Inject
    public DefaultOSWorkflowDescriptorRegistry( final Iterable<OSWorkflowDescriptor> descriptors )
    {
        // VALIDATE
        m_descriptors = descriptors;
    }

    public WorkflowDescriptor get( final OSWorkflowName name )
    {
        if( name == null )
        {
            return null;
        }

        for( OSWorkflowDescriptor descriptor : m_descriptors )
        {
            if( name.equals( descriptor.name() ) )
            {
                return descriptor.descriptor();
            }
        }

        return null;
    }


}
