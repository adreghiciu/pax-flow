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

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.ExecutionTarget;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ExecutionTargetAdapter
    implements Job
{

    public void execute( final JobExecutionContext jobExecutionContext )
        throws JobExecutionException
    {
        final ExecutionContext context = (ExecutionContext) jobExecutionContext.get( ExecutionContext.class.getName() );
        final ExecutionTarget target = (ExecutionTarget) jobExecutionContext.get( ExecutionTarget.class.getName() );

        target.fire( context );
    }

}