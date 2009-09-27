package org.ops4j.pax.flow.recipes.flow;

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
public class ExtractFileNameFromFile
    extends CancelableFlow
    implements Flow
{

    private final PropertyName m_filePropertyName;
    private final PropertyName m_fileNamePropertyName;

    public ExtractFileNameFromFile( final PropertyName filePropertyName,
                                    final PropertyName fileNamePropertyName )
    {
        m_filePropertyName = filePropertyName;
        m_fileNamePropertyName = fileNamePropertyName;
    }

    public void run( final ExecutionContext context )
    {
        final File file = typedExecutionContext( context ).mandatory( m_filePropertyName, File.class );
        context.add( executionProperty( m_fileNamePropertyName, file.getAbsolutePath() ) );
    }

    @Override
    public String toString()
    {
        return String.format( "Extract file name from [%s] to [%s]", m_filePropertyName, m_fileNamePropertyName );
    }

}