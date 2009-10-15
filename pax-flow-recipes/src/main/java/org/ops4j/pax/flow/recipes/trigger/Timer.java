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

package org.ops4j.pax.flow.recipes.trigger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.ExecutionTarget;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
import org.ops4j.pax.flow.api.RunNow;
import org.ops4j.pax.flow.api.Trigger;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.api.TriggerName;
import static org.ops4j.pax.flow.api.TriggerName.*;
import org.ops4j.pax.flow.api.TriggerType;
import static org.ops4j.pax.flow.api.TriggerType.*;
import org.ops4j.pax.flow.api.helpers.TypedConfiguration;
import static org.ops4j.pax.flow.api.helpers.TypedConfiguration.*;
import org.ops4j.pax.flow.recipes.internal.trigger.AbstractTrigger;
import org.ops4j.pax.flow.recipes.internal.trigger.TimerUtils;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class Timer
    extends AbstractTrigger<Timer>
    implements Trigger, RunNow<Timer>
{

    private static final Log LOG = LogFactory.getLog( Timer.class );

    private final ScheduledExecutorService executorService;

    private final long initialDelay;
    private final long repeatPeriod;

    private ScheduledFuture<?> scheduledFuture;

    public Timer( final TriggerName name,
                  final ExecutionTarget target,
                  final ScheduledExecutorService executorService,
                  final long initialDelay,
                  final long repeatPeriod )
    {
        super( name, target );
        // VALIDATE
        this.executorService = executorService;
        this.initialDelay = initialDelay;
        this.repeatPeriod = repeatPeriod;
    }

    @Override
    public Timer fire()
    {
        return super.fire();
    }

    @Override
    public Timer start()
        throws Exception
    {
        if( !isStarted() )
        {
            LOG.debug( String.format( "Starting [%s]", this ) );
            super.start();
            scheduledFuture = executorService.scheduleAtFixedRate(
                new Runnable()
                {
                    public void run()
                    {
                        fire();
                    }
                },
                initialDelay,
                repeatPeriod,
                TimeUnit.MILLISECONDS
            );
        }
        return itself();
    }

    @Override
    public Timer stop()
    {
        if( isStarted() )
        {
            LOG.debug( String.format( "Stopping [%s]", this ) );
            scheduledFuture.cancel( true );
            super.stop();
        }
        return itself();
    }

    @Override
    protected Timer itself()
    {
        return this;
    }

    @Override
    public String toString()
    {
        return String.format(
            "[%s ms] elapsed (initial delay of [%s ms])",
            repeatPeriod,
            initialDelay
        );
    }

    /**
     * JAVADOC
     *
     * @author Alin Dreghiciu
     */
    public static class Factory
        implements TriggerFactory<Timer>
    {

        public static final TriggerType TYPE = triggerType( Timer.class );

        public static final PropertyName INITIAL_DELAY = propertyName( "initialDelay" );
        public static final PropertyName REPEAT_PERIOD = propertyName( "repeatPeriod" );

        private final ScheduledExecutorService executorService;

        private long counter;

        @Inject
        public Factory( final ScheduledExecutorService executorService )
        {
            // VALIDATE
            this.executorService = executorService;
        }

        public TriggerType type()
        {
            return TYPE;
        }

        public Timer create( final Configuration configuration,
                             final ExecutionTarget target )
        {

            final TypedConfiguration config = typedConfiguration( configuration );

            final String initialDelay = config.optional( INITIAL_DELAY, String.class, "0s" );
            final String repeatPeriod = config.optional( REPEAT_PERIOD, String.class, "10s" );

            return new Timer(
                triggerName( String.format( "%s::%d", type(), counter++ ) ),
                target,
                executorService,
                TimerUtils.convertToMillis( initialDelay ),
                TimerUtils.convertToMillis( repeatPeriod )
            );
        }

        @Override
        public String toString()
        {
            return String.format( "Trigger factory for type [%s] (%d instances)", type(), counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "triggerType", TYPE.toString() );

            return attributes;
        }

    }

}