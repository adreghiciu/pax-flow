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

import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.ExecutionTarget;
import org.ops4j.pax.flow.api.Workflow;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class WorkflowStarter
    implements ExecutionTarget
{

    private final Workflow m_workflow;

    public WorkflowStarter( final Workflow workflow )
    {

        m_workflow = workflow;
    }

    public void fire( final ExecutionContext context )
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

}
