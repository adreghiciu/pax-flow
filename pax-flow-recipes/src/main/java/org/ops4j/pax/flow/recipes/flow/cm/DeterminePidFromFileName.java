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
public class DeterminePidFromFileName
    extends CancelableFlow
    implements Flow
{

    private final PropertyName filePropertyName;
    private final PropertyName pidPropertyName;
    private final PropertyName factoryPidPropertyName;

    public DeterminePidFromFileName( final PropertyName filePropertyName,
                                     final PropertyName pidPropertyName,
                                     final PropertyName factoryPidPropertyName )
    {
        this.filePropertyName = filePropertyName;
        this.pidPropertyName = pidPropertyName;
        this.factoryPidPropertyName = factoryPidPropertyName;
    }

    public void run( final ExecutionContext context )
    {
        final File file = typedExecutionContext( context ).mandatory( filePropertyName, File.class );

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

        context.add( executionProperty( pidPropertyName, pid ) );
        context.add( executionProperty( factoryPidPropertyName, factoryPid ) );
    }

    @Override
    public String toString()
    {
        return String.format(
            "Extract PID / Factory PID from [%s] to [%s] and [%s]",
            filePropertyName, pidPropertyName, factoryPidPropertyName
        );
    }

}