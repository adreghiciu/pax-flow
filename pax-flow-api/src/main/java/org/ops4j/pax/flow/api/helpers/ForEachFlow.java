package org.ops4j.pax.flow.api.helpers;

import org.ops4j.pax.flow.api.ExecutionContext;
import static org.ops4j.pax.flow.api.ExecutionProperty.*;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowName;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ForEachFlow
    extends CancelableFlow
    implements Flow
{

    private final Flow iterableFlow;
    private final PropertyName iterablePropertyName;
    private final PropertyName iteratorPropertyName;

    public ForEachFlow( final PropertyName iterablePropertyName,
                        final PropertyName iteratorPropertyName,
                        final Flow iterableFlow
    )
    {
        super();
        // VALIDATE
        this.iterableFlow = iterableFlow;
        this.iterablePropertyName = iterablePropertyName;
        this.iteratorPropertyName = iteratorPropertyName;
    }

    public ForEachFlow( final FlowName flowName,
                        final Flow iterableFlow,
                        final PropertyName iterablePropertyName,
                        final PropertyName iteratorPropertyName )
    {
        super( flowName );
        // VALIDATE
        this.iterableFlow = iterableFlow;
        this.iterablePropertyName = iterablePropertyName;
        this.iteratorPropertyName = iteratorPropertyName;
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        final Iterable<?> iterable =
            typedExecutionContext( context ).optional( iterablePropertyName, Iterable.class );

        if( iterable != null )
        {
            for( Object object : iterable )
            {
                context.add( executionProperty( iteratorPropertyName, object ) );
                iterableFlow.execute( context );
            }
        }
    }

}