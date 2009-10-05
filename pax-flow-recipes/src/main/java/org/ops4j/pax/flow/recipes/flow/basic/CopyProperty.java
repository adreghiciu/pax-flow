package org.ops4j.pax.flow.recipes.flow.basic;

import org.ops4j.pax.flow.api.ExecutionContext;
import static org.ops4j.pax.flow.api.ExecutionProperty.*;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.PropertyName;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * Copy a property (source), from {@link ExecutionContext} to another property (target).
 *
 * @author Alin Dreghiciu
 */
public class CopyProperty<T>
    extends CancelableFlow
    implements Flow
{

    private final PropertyName source;
    private final PropertyName target;
    private final Class<T> propertyType;

    public CopyProperty( final PropertyName source,
                         final PropertyName target,
                         final Class<T> propertyType )
    {
        // VALIDATE
        this.source = source;
        this.target = target;
        this.propertyType = propertyType;
    }

    public void run( final ExecutionContext context )
    {
        final T source = typedExecutionContext( context ).mandatory( this.source, propertyType );
        context.add( executionProperty( target, source ) );
    }

    @Override
    public String toString()
    {
        return String.format( "Copy property [%s] -> [%s]", source, target );
    }

}