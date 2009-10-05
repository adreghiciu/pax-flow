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

package org.ops4j.pax.flow.recipes.flow.cm;

import static java.lang.String.*;
import java.util.Map;
import org.ops4j.pax.flow.api.ExecutionContext;
import static org.ops4j.pax.flow.api.ExecutionProperty.*;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.PropertyName;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class DeterminePidFromMap
    extends CancelableFlow
    implements Flow
{

    private final PropertyName mapPropertyName;

    private final String pidKey;
    private final String factoryPidKey;

    private final PropertyName pidPropertyName;
    private final PropertyName factoryPidPropertyName;

    public DeterminePidFromMap( final PropertyName mapPropertyName,
                                final String pidKey,
                                final String factoryPidKey,
                                final PropertyName pidPropertyName,
                                final PropertyName factoryPidPropertyName )
    {
        this.mapPropertyName = mapPropertyName;

        this.pidKey = pidKey;
        this.factoryPidKey = factoryPidKey;

        this.pidPropertyName = pidPropertyName;
        this.factoryPidPropertyName = factoryPidPropertyName;
    }

    public void run( final ExecutionContext context )
        throws IllegalStateException
    {
        final Map<String, ?> attributes = typedExecutionContext( context ).mandatory( mapPropertyName, Map.class );

        Object pid = attributes.get( pidKey );
        Object factoryPid = attributes.get( factoryPidKey );

        if( pid == null )
        {
            throw new IllegalStateException(
                format( "Attributes must contain a String property named [%s]", pidKey )
            );
        }
        else if( !( pid instanceof String ) )
        {
            throw new IllegalStateException(
                format( "Found PID [%s] expected to be a String but is an [%s]",
                        factoryPidKey, factoryPid.getClass().getName()
                )
            );
        }

        if( factoryPid != null && !( factoryPid instanceof String ) )
        {
            throw new IllegalStateException(
                format( "Found factory PID [%s] expected to be a String but is an [%s]",
                        factoryPidKey, factoryPid.getClass().getName()
                )
            );
        }

        context.add( executionProperty( pidPropertyName, pid ) );
        context.add( executionProperty( factoryPidPropertyName, factoryPid ) );
    }

    @Override
    public String toString()
    {
        return format(
            "Extract PID / Factory PID from [%s] to [%s] and [%s]",
            mapPropertyName, pidKey, factoryPidKey
        );
    }

}