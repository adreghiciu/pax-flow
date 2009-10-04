package org.ops4j.pax.flow.runtime.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowFactoryRegistry;
import org.ops4j.pax.flow.api.Transformer;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.api.TriggerFactoryRegistry;
import static org.ops4j.peaberry.Peaberry.*;
import static org.ops4j.peaberry.util.TypeLiterals.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class GuiceConfig extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( iterable( FlowFactory.class ) )
            .toProvider( service( FlowFactory.class ).multiple() );
        bind( iterable( TriggerFactory.class ) )
            .toProvider( service( TriggerFactory.class ).multiple() );

        bind( export( FlowFactoryRegistry.class ) )
            .toProvider( service( SRFlowFactoryRegistry.class ).export() );
        bind( export( TriggerFactoryRegistry.class ) )
            .toProvider( service( SRTriggerFactoryRegistry.class ).export() );

        bind( iterable( FlowFactoryRegistry.class ) )
            .toProvider( service( FlowFactoryRegistry.class ).multiple() );
        bind( iterable( TriggerFactoryRegistry.class ) )
            .toProvider( service( TriggerFactoryRegistry.class ).multiple() );

        bind( FlowFactoryRegistry.class ).to( CompositeFlowFactoryRegistry.class );
        bind( TriggerFactoryRegistry.class ).to( CompositeTriggerFactoryRegistry.class );

        // TODO make number of threads configurable
        bind( ExecutorService.class ).toInstance( Executors.newFixedThreadPool( 10 ) );
        bind( export( Transformer.class ) ).toProvider(
            service( DefaultTransformer.class ).attributes( DefaultTransformer.attributes() ).export()
        );
        bind( DefaultTransformer.class ).in( Singleton.class );
    }

}
