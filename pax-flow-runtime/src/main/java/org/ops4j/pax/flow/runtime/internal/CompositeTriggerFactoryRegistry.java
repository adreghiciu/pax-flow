package org.ops4j.pax.flow.runtime.internal;

import com.google.inject.Inject;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.api.TriggerFactoryRegistry;
import org.ops4j.pax.flow.api.TriggerType;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class CompositeTriggerFactoryRegistry
    implements TriggerFactoryRegistry
{

    private Iterable<TriggerFactoryRegistry> registries;

    @Inject
    public CompositeTriggerFactoryRegistry( final Iterable<TriggerFactoryRegistry> registries )
    {
        // VALIDATE
        this.registries = registries;
    }

    public TriggerFactory get( final TriggerType type )
    {
        if( type != null )
        {
            for( TriggerFactoryRegistry registry : registries )
            {
                final TriggerFactory factory = registry.get( type );
                if( factory != null )
                {
                    return factory;
                }
            }
        }

        return null;
    }

}