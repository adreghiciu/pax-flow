package org.ops4j.pax.flow.recipes.flow.cm;

import java.io.File;
import org.ops4j.pax.flow.api.ExecutionContext;
import static org.ops4j.pax.flow.api.ExecutionProperty.*;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.PropertyName;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ExtractPidFromFile
    extends CancelableFlow
    implements Flow
{

    private final PropertyName m_filePropertyName;
    private final PropertyName m_pidPropertyName;
    private final PropertyName m_factoryPidPropertyName;

    public ExtractPidFromFile( final PropertyName filePropertyName,
                               final PropertyName pidPropertyName,
                               final PropertyName factoryPidPropertyName )
    {
        m_filePropertyName = filePropertyName;
        m_pidPropertyName = pidPropertyName;
        m_factoryPidPropertyName = factoryPidPropertyName;
    }

    public void run( final ExecutionContext context )
    {
        final File file = typedExecutionContext( context ).mandatory( m_filePropertyName, File.class );

        String pid = file.getName();
        String factoryPid = null;

        // remove extension, if any
        final int startOfExtension = pid.lastIndexOf( "." );
        if( startOfExtension > 0 )
        {
            pid = pid.substring( 0, startOfExtension );
        }

        // split into pid / factory pid
        final int startOfFactoryPid = pid.indexOf( "-" );
        if( startOfFactoryPid > 0 )
        {
            factoryPid = pid.substring( startOfFactoryPid + 1 );
            pid = pid.substring( 0, startOfExtension );
        }

        context.add( executionProperty( m_pidPropertyName, pid ) );
        context.add( executionProperty( m_factoryPidPropertyName, factoryPid ) );
    }

    @Override
    public String toString()
    {
        return String.format( "Extract file name from [%s] to [%s]", m_filePropertyName, m_pidPropertyName );
    }

}