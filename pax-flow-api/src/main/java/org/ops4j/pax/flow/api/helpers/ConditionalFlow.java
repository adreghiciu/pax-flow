/*
 * Copyright 2009 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.flow.api.helpers;

import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ConditionalFlow
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( ConditionalFlow.class );

    private final Condition condition;
    private final Flow flow;

    public ConditionalFlow( final Condition condition,
                            final Flow flow )
    {
        super();

        // VALIDATE
        this.condition = condition;
        this.flow = flow;
    }

    public void run( final ExecutionContext context )
        throws Exception
    {
        if( condition.isSatisfied( context ) )
        {
            flow.execute( context );
        }
    }

    public static IsTrue isTrue( final PropertyName propertyName )
    {
        return new IsTrue( propertyName );
    }

    public static HasValue hasValue( final PropertyName propertyName )
    {
        return new HasValue( propertyName );
    }

    public static CollectionIsEmpty collectionIsEmpty( final PropertyName propertyName )
    {
        return new CollectionIsEmpty( propertyName );
    }

    public static Not not( final Condition condition )
    {
        return new Not( condition );
    }

    public static And and( final Condition... conditions )
    {
        return new And( conditions );
    }

    public static Or or( final Condition... conditions )
    {
        return new Or( conditions );
    }

    public static interface Condition
    {

        boolean isSatisfied( ExecutionContext context );

    }

    public static class IsTrue
        implements Condition
    {

        private final PropertyName propertyName;

        public IsTrue( final PropertyName propertyName )
        {
            // VALIDATE
            this.propertyName = propertyName;
        }

        public boolean isSatisfied( final ExecutionContext context )
        {
            final Boolean value = typedExecutionContext( context ).optional( propertyName, Boolean.class );
            return value != null && value.equals( true );
        }

    }

    public static class HasValue
        implements Condition
    {

        private final PropertyName propertyName;

        public HasValue( final PropertyName propertyName )
        {
            // VALIDATE
            this.propertyName = propertyName;
        }

        public boolean isSatisfied( final ExecutionContext context )
        {
            final Object value = context.get( propertyName );
            return value != null;
        }

    }

    public static class CollectionIsEmpty
        implements Condition
    {

        private final PropertyName propertyName;

        public CollectionIsEmpty( final PropertyName propertyName )
        {
            // VALIDATE
            this.propertyName = propertyName;
        }

        public boolean isSatisfied( final ExecutionContext context )
        {
            final Collection value = typedExecutionContext( context ).optional( propertyName, Collection.class );
            return value == null || value.isEmpty();
        }

    }

    public static class Not
        implements Condition
    {

        private final Condition condition;

        public Not( final Condition condition )
        {
            // VALIDATE
            this.condition = condition;
        }

        public boolean isSatisfied( final ExecutionContext context )
        {
            return !condition.isSatisfied( context );
        }

    }

    public static class And
        implements Condition
    {

        private final Condition[] conditions;

        public And( final Condition... conditions )
        {
            // VALIDATE
            this.conditions = conditions;
        }

        public boolean isSatisfied( final ExecutionContext context )
        {
            for( Condition condition : conditions )
            {
                if( !condition.isSatisfied( context ) )
                {
                    return false;
                }
            }
            return true;
        }

    }

    public static class Or
        implements Condition
    {

        private final Condition[] conditions;

        public Or( final Condition... conditions )
        {
            // VALIDATE
            this.conditions = conditions;
        }

        public boolean isSatisfied( final ExecutionContext context )
        {
            for( Condition condition : conditions )
            {
                if( condition.isSatisfied( context ) )
                {
                    return true;
                }
            }
            return false;
        }

    }

}