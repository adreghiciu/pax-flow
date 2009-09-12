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

import java.util.Map;
import java.net.URL;
import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.spi.WorkflowStore;
import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class DefaultConfiguration
    implements Configuration
{

    public boolean isInitialized()
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    public String getPersistence()
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    public Map getPersistenceArgs()
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    public WorkflowDescriptor getWorkflow( final String s )
        throws FactoryException
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    public String[] getWorkflowNames()
        throws FactoryException
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    public WorkflowStore getWorkflowStore()
        throws StoreException
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    public void load( final URL url )
        throws FactoryException
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    public boolean removeWorkflow( final String s )
        throws FactoryException
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    public boolean saveWorkflow( final String s, final WorkflowDescriptor workflowDescriptor, final boolean b )
        throws FactoryException
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

}