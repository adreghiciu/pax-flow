package org.ops4j.pax.flow.osworkflow.internal;

import java.util.ArrayList;
import java.util.Collection;
import com.google.inject.Inject;
import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.loader.XMLWorkflowFactory;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.flow.osworkflow.OSWorkflowDescriptor;
import org.ops4j.pax.flow.osworkflow.OSWorkflowDescriptorRegistry;
import org.ops4j.pax.flow.osworkflow.OSWorkflowName;
import static org.ops4j.pax.flow.osworkflow.OSWorkflowName.*;
import org.ops4j.pax.flow.osworkflow.helper.ImmutableOSWorkflowDescriptor;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class BasicOSWorkflowDescriptorRegistry
    implements OSWorkflowDescriptorRegistry
{

    private final XMLWorkflowFactory m_workflowFactory;
    private static final String RESOURCE = "resource";
    private static final String BASIC_WORK_FLOWS = "META-INF/org/ops4j/pax/flow/basic-workflows.xml";

    @Inject
    public BasicOSWorkflowDescriptorRegistry( final BundleContext bundleContext )
    {
        // VALIDATE
        XMLWorkflowFactory workflowFactory = new XMLWorkflowFactory();
        workflowFactory.getProperties().setProperty(
            RESOURCE,
            bundleContext.getBundle().getEntry( BASIC_WORK_FLOWS ).toExternalForm()
        );
        try
        {
            // TODO replace this with OPS4j BAse do with class loader
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader());
            workflowFactory.initDone();
        }
        catch( FactoryException ignore )
        {
            workflowFactory = null;
        }
        m_workflowFactory = workflowFactory;
    }

    public OSWorkflowDescriptor get( final OSWorkflowName name )
    {
        if( name == null || m_workflowFactory == null )
        {
            return null;
        }

        try
        {
            return new ImmutableOSWorkflowDescriptor( name, m_workflowFactory.getWorkflow( name.stringValue() ) );
        }
        catch( FactoryException e )
        {
            e.printStackTrace( System.err );
            return null;
        }
    }

    public Iterable<OSWorkflowName> getNames()
    {
        if( m_workflowFactory == null )
        {
            return null;
        }
        final Collection<OSWorkflowName> names = new ArrayList<OSWorkflowName>();
        for( String name : m_workflowFactory.getWorkflowNames() )
        {
            names.add( osWorkflowName( name ) );
        }
        return names;
    }


}