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

    private Iterable<FlowFactoryRegistry> registries;

    @Inject
    public CompositeFlowFactoryRegistry( final Iterable<FlowFactoryRegistry> registries )
    {
        // VALIDATE
        this.registries = registries;
    }

    public FlowFactory get( final FlowType type )
    {
        if( type != null )
        {
            for( FlowFactoryRegistry registry : registries )
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