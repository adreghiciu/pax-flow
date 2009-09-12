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

import static java.lang.String.*;
import java.util.ArrayList;
import java.util.Collection;
import com.google.inject.Inject;
import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.loader.AbstractWorkflowFactory;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import org.ops4j.pax.flow.api.Workflow;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class DefaultWorkflowFactory
    extends AbstractWorkflowFactory
{

    private final Iterable<Workflow> m_references;

    @Inject
    public DefaultWorkflowFactory( final Iterable<Workflow> references )
    {
        m_references = references;
    }

    @Override
    public WorkflowDescriptor getWorkflow( final String name )
        throws FactoryException
    {
        if( name != null && name.trim().length() > 0 )
        {
            for( Workflow reference : m_references )
            {
                if( name.equals( reference.name().asString() ) )
                {
                    return reference.descriptor();
                }
            }
        }
        throw new FactoryException( format( "Unknown workflow name: [%s]", name ) );
    }

    @Override
    public String[] getWorkflowNames()
        throws FactoryException
    {
        final Collection<String> names = new ArrayList<String>();
        for( Workflow reference : m_references )
        {
            names.add( reference.name().asString() );
        }
        return names.toArray( new String[names.size()] );
    }

    @Override
    public boolean removeWorkflow( final String name )
        throws FactoryException
    {
        throw new UnsupportedOperationException( format( "Workflow [%s] is read only. Cannot be removed.", name ) );
    }

    @Override
    public boolean saveWorkflow( final String name,
                                 final WorkflowDescriptor workflowDescriptor,
                                 final boolean replace )
        throws FactoryException
    {
        throw new UnsupportedOperationException( format( "Workflow [%s] is read only. Cannot be saved.", name ) );
    }

}
