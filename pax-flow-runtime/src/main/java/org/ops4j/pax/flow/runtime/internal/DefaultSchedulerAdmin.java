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

package org.ops4j.pax.flow.runtime.internal;

import java.util.HashMap;
import java.util.Map;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.ExecutionTarget;
import org.ops4j.pax.flow.api.Job;
import org.ops4j.pax.flow.api.SchedulerAdmin;
import org.ops4j.pax.flow.api.Trigger;
import org.ops4j.pax.flow.api.base.JobName;
import static org.ops4j.pax.flow.api.base.TriggerName.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class DefaultSchedulerAdmin
    implements SchedulerAdmin
{

    private final Map<JobName, JobContext> m_jobs;

    public DefaultSchedulerAdmin()
    {
        m_jobs = new HashMap<JobName, JobContext>();
    }

    public SchedulerAdmin schedule( final Job job )
    {
        return schedule( job, new DefaultExecutionContext() );
    }

    public SchedulerAdmin schedule( final Job job,
                                    final ExecutionContext executionContext )
    {
        // VALIDATE

        final JobContext jobContext = new JobContext();
        jobContext.job = job;
        jobContext.executionContext = executionContext;
        jobContext.executionTarget = new WorkflowStarter( job.workflow() );

        m_jobs.put( job.name(), jobContext );

        jobContext.trigger = job.factory().create(
            triggerName( job.name().stringValue() ),
            jobContext.executionContext,
            jobContext.executionTarget
        );

        return this;
    }

    private static class JobContext
    {

        Job job;
        Trigger trigger;
        ExecutionContext executionContext;
        ExecutionTarget executionTarget;
    }

}
