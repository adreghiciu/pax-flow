package org.ops4j.pax.flow.runtime.internal;

import com.google.inject.Inject;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowFactoryRegistry;
import org.ops4j.pax.flow.api.FlowType;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class CompositeFlowFactoryRegistry
    implements FlowFactoryRegistry
{

    private Iterable<FlowFactoryRegistry> m_registries;

    @Inject
    public CompositeFlowFactoryRegistry( final Iterable<FlowFactoryRegistry> registries )
    {
        // VALIDATE
        m_registries = registries;
    }

    public FlowFactory get( final FlowType type )
    {
        if( type != null )
        {
            for( FlowFactoryRegistry registry : m_registries )
            {
                final FlowFactory factory = registry.get( type );
                if( factory != null )
                {
                    return factory;
                }
            }
        }

        return null;
    }

}