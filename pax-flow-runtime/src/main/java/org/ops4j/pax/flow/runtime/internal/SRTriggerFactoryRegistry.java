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
public class SRTriggerFactoryRegistry
    implements TriggerFactoryRegistry
{

    private final Iterable<TriggerFactory> factories;

    @Inject
    public SRTriggerFactoryRegistry( final Iterable<TriggerFactory> factories )
    {
        // VALIDATE
        this.factories = factories;
    }

    public TriggerFactory get( final TriggerType type )
    {
        if( type != null )
        {
            for( TriggerFactory factory : factories )
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