package org.ops4j.pax.flow.recipes.flow.cm;

import java.net.URL;
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
public class DeterminePidFromURL
    extends CancelableFlow
    implements Flow
{

    private final PropertyName m_urlPropertyName;
    private final PropertyName m_pidPropertyName;
    private final PropertyName m_factoryPidPropertyName;

    public DeterminePidFromURL( final PropertyName urlPropertyName,
                                final PropertyName pidPropertyName,
                                final PropertyName factoryPidPropertyName )
    {
        m_urlPropertyName = urlPropertyName;
        m_pidPropertyName = pidPropertyName;
        m_factoryPidPropertyName = factoryPidPropertyName;
    }

    public void run( final ExecutionContext context )
    {
        final URL url = typedExecutionContext( context ).mandatory( m_urlPropertyName, URL.class );

        final String stringForm = url.toExternalForm();
        final int startOfPid = stringForm.lastIndexOf( "/" );

        String pid = stringForm.substring( startOfPid + 1 );
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
        return String.format(
            "Extract PID / Factory PID from [%s] to [%s] and [%s]",
            m_urlPropertyName, m_pidPropertyName, m_factoryPidPropertyName
        );
    }

}