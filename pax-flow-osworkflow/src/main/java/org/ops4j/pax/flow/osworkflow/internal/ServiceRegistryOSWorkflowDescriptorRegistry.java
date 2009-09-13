package org.ops4j.pax.flow.osworkflow.internal;

import java.util.ArrayList;
import java.util.Collection;
import com.google.inject.Inject;
import org.ops4j.pax.flow.osworkflow.OSWorkflowDescriptor;
import org.ops4j.pax.flow.osworkflow.OSWorkflowDescriptorRegistry;
import org.ops4j.pax.flow.osworkflow.OSWorkflowName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ServiceRegistryOSWorkflowDescriptorRegistry
    implements OSWorkflowDescriptorRegistry
{

    private final Iterable<OSWorkflowDescriptor> m_descriptors;

    @Inject
    public ServiceRegistryOSWorkflowDescriptorRegistry( final Iterable<OSWorkflowDescriptor> descriptors )
    {
        // VALIDATE
        m_descriptors = descriptors;
    }

    public OSWorkflowDescriptor get( final OSWorkflowName name )
    {
        if( name == null )
        {
            return null;
        }

        for( OSWorkflowDescriptor descriptor : m_descriptors )
        {
            if( name.equals( descriptor.name() ) )
            {
                return descriptor;
            }
        }

        return null;
    }

    public Iterable<OSWorkflowName> getNames()
    {
        final Collection<OSWorkflowName> names = new ArrayList<OSWorkflowName>();
        for( OSWorkflowDescriptor descriptor : m_descriptors )
        {
            names.add( descriptor.name() );
        }
        return names;
    }


}
