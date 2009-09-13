package org.ops4j.pax.flow.osworkflow.internal;

import com.google.inject.Inject;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import org.ops4j.pax.flow.osworkflow.OSWorkflowDescriptorRegistry;
import org.ops4j.pax.flow.osworkflow.OSWorkflowName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class CompositeOSWorkflowDescriptorRegistry
    implements OSWorkflowDescriptorRegistry
{

    private final Iterable<OSWorkflowDescriptorRegistry> m_registries;

    @Inject
    public CompositeOSWorkflowDescriptorRegistry( final Iterable<OSWorkflowDescriptorRegistry> registries )
    {
        // VALIDATE
        m_registries = registries;
    }

    public WorkflowDescriptor get( final OSWorkflowName name )
    {
        if( name == null )
        {
            return null;
        }

        for( OSWorkflowDescriptorRegistry registry : m_registries )
        {
            final WorkflowDescriptor descriptor = registry.get( name );
            if( descriptor != null )
            {
                return descriptor;
            }
        }

        return null;
    }


}