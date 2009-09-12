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

package org.ops4j.pax.flow.trigger.internal;

import com.google.inject.Inject;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.ExecutionTarget;
import org.ops4j.pax.flow.api.Trigger;
import org.ops4j.pax.flow.api.base.TriggerName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public abstract class AbstractTrigger
    implements Trigger
{

    private final TriggerName m_name;
    private final ExecutionContext m_context;
    private final ExecutionTarget m_target;

    private boolean m_started;

    @Inject
    private Scheduler m_scheduler;

    public AbstractTrigger( final TriggerName name,
                            final ExecutionContext context,
                            final ExecutionTarget target )
    {
        m_name = name;
        m_target = target;
        m_context = context;
    }

    public TriggerName name()
    {
        return m_name;
    }

    public Trigger start()
        throws Exception
    {
        m_started = true;
        return this;
    }

    public Trigger stop()
    {
        m_started = false;
        return this;
    }

    protected ExecutionContext context()
    {
        return m_context;
    }

    protected ExecutionTarget target()
    {
        return m_target;
    }

    protected boolean isStarted()
    {
        return m_started;
    }
    

    protected JobDetail createJobDetail()
    {
        final JobDataMap dataMap = new JobDataMap();
        dataMap.put( ExecutionContext.class.getName(), context() );
        dataMap.put( ExecutionTarget.class.getName(), target() );

        final JobDetail jobDetail = new JobDetail();
        jobDetail.setJobDataMap( dataMap );
        jobDetail.setJobClass( ExecutionTargetAdapter.class );
        return jobDetail;
    }

}