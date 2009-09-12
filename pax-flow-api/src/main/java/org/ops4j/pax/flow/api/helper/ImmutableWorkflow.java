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

package org.ops4j.pax.flow.api.helper;

import com.opensymphony.workflow.loader.WorkflowDescriptor;
import org.ops4j.pax.flow.api.Workflow;
import org.ops4j.pax.flow.api.base.WorkflowName;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ImmutableWorkflow
    implements Workflow
{

    private final WorkflowName m_name;
    private final WorkflowDescriptor m_descriptor;

    public ImmutableWorkflow( final WorkflowName name,
                              final WorkflowDescriptor descriptor )
    {
        assert name != null : "Workflow name must be specified (cannot be null)";
        assert descriptor != null : "Workflow descriptor must be specified (cannot be null)";

        m_name = name;
        m_descriptor = descriptor;
    }

    public WorkflowName name()
    {
        return m_name;
    }

    public WorkflowDescriptor descriptor()
    {
        return m_descriptor;
    }

    public static ImmutableWorkflow workflow( final WorkflowName name,
                                              final WorkflowDescriptor descriptor )
    {
        return new ImmutableWorkflow( name, descriptor );
    }

}