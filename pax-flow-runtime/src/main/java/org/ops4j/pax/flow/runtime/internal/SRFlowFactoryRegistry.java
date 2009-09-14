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
public class SRFlowFactoryRegistry
    implements FlowFactoryRegistry
{

    private final Iterable<FlowFactory> m_factories;

    @Inject
    public SRFlowFactoryRegistry( final Iterable<FlowFactory> factories )
    {
        // VALIDATE
        m_factories = factories;
    }

    public FlowFactory get( final FlowType type )
    {
        if( type != null )
        {
            for( FlowFactory factory : m_factories )
            {
                if( type.equals( factory.type() ) )
                {
                    return factory;
                }
            }
        }

        return null;
    }
    
}
