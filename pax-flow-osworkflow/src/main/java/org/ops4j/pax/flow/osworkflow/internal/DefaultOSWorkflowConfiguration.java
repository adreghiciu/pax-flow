package org.ops4j.pax.flow.osworkflow.internal;

import java.net.URL;
import java.util.Map;
import com.google.inject.Inject;
import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.loader.AbstractWorkflowFactory;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.spi.WorkflowStore;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class DefaultOSWorkflowConfiguration
    implements Configuration
{

    private final AbstractWorkflowFactory m_factory;
    private final WorkflowStore m_store;
    private boolean m_initialized;

    @Inject
    public DefaultOSWorkflowConfiguration( final AbstractWorkflowFactory factory,
                                           final WorkflowStore store )
    {
        m_factory = factory;
        m_store = store;
    }

    public boolean isInitialized()
    {
        return m_initialized;
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

    public WorkflowDescriptor getWorkflow( final String name )
        throws FactoryException
    {
        WorkflowDescriptor workflow = m_factory.getWorkflow( name );
        if( workflow == null )
        {
            throw new FactoryException( "Unknown workflow name" );
        }
        else
        {
            return workflow;
        }
    }

    public String[] getWorkflowNames()
        throws FactoryException
    {
        return m_factory.getWorkflowNames();
    }

    public WorkflowStore getWorkflowStore()
        throws StoreException
    {
        return m_store;
    }

    public void load( final URL url )
    {
        // preloaded in constructor
        m_initialized = true;
    }

    public boolean removeWorkflow( final String name )
        throws FactoryException
    {
        return m_factory.removeWorkflow( name );
    }

    public boolean saveWorkflow( final String name,
                                 final WorkflowDescriptor descriptor,
                                 final boolean replace )
        throws FactoryException
    {
        return m_factory.saveWorkflow( name, descriptor, replace );
    }

}
