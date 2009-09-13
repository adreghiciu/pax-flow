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

    public OSWorkflowDescriptor get( final OSWorkflowName name )
    {
        if( name == null )
        {
            return null;
        }

        for( OSWorkflowDescriptorRegistry registry : m_registries )
        {
            final OSWorkflowDescriptor descriptor = registry.get( name );
            if( descriptor != null )
            {
                return descriptor;
            }
        }

        return null;
    }

    public Iterable<OSWorkflowName> getNames()
    {
        final Collection<OSWorkflowName> allNames = new ArrayList<OSWorkflowName>();
        for( OSWorkflowDescriptorRegistry registry : m_registries )
        {
            final Iterable<OSWorkflowName> names = registry.getNames();
            if( names != null )
            {
                for( OSWorkflowName name : names )
                {
                    allNames.add( name );
                }
            }
        }
        return allNames;
    }


}