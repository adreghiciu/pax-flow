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
import org.ops4j.pax.flow.api.Flow;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class FlowAdapter
    implements Job
{

    public void execute( final JobExecutionContext jobExecutionContext )
        throws JobExecutionException
    {
        final Flow target = (Flow) jobExecutionContext.getJobDetail().getJobDataMap().get( Flow.class.getName() );

        if( target == null )
        {
            throw new JobExecutionException( "Could not determine the flow to be run" );
        }

        try
        {
            target.execute();
        }
        catch( Exception e )
        {
            throw new JobExecutionException( e );
        }
    }

}