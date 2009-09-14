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

package org.ops4j.pax.flow.trigger;

import com.google.inject.Inject;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.Trigger;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.api.TriggerName;
import org.ops4j.pax.flow.api.AttributeName;
import org.ops4j.pax.flow.trigger.internal.AbstractTrigger;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class QuartzCronTrigger
    extends AbstractTrigger<QuartzCronTrigger>
    implements Trigger
{

    @Inject
    private Scheduler m_scheduler;
    private static final String EVERY_MINUTE = "0 * * * * ?";
    private static final AttributeName CRON_EXPRESSION = AttributeName.attributeName( "cronExpression" );

    public QuartzCronTrigger( final TriggerName name,
                              final ExecutionContext context,
                              final Flow target )
    {
        super( name, context, target );
    }

    @Override
    public QuartzCronTrigger start()
        throws Exception
    {
        if( isStarted() )
        {
            return this;
        }

        final String cronExpression = context().get( CRON_EXPRESSION, EVERY_MINUTE );
        final CronTrigger cronTrigger = new CronTrigger( name().stringValue(), null );
        cronTrigger.setCronExpression( cronExpression );

        m_scheduler.scheduleJob( createJobDetail(), cronTrigger );

        return this;
    }

    @Override
    protected QuartzCronTrigger itself()
    {
        return this;
    }

    /**
     * JAVADOC
     *
     * @author Alin Dreghiciu
     */
    public static class Factory
        implements TriggerFactory<QuartzCronTrigger>
    {

        public QuartzCronTrigger create( final TriggerName name,
                               final ExecutionContext context,
                               final Flow target )
        {
            return new QuartzCronTrigger( name, context, target );
        }

    }
}