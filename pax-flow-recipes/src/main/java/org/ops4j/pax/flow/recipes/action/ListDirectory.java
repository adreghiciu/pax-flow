package org.ops4j.pax.flow.recipes.action;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;
import org.ops4j.io.DirectoryLister;
import org.ops4j.io.ListerUtils;
import org.ops4j.pax.flow.api.Action;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.PropertyName;

/**
 * Lists a file system directory.
 *
 * @author Alin Dreghiciu
 */
public class ListDirectory
    implements Action
{

    public static final PropertyName FILES = PropertyName.propertyName( "files" );

    private final File m_directory;
    private final Pattern[] m_includes;
    private final Pattern[] m_excludes;

    private final DirectoryLister m_lister;

    public ListDirectory( final File directory,
                          final String[] includes,
                          final String[] excludes )
    {
        // VALIDATE
        m_directory = directory;
        m_includes = asPatterns( includes );
        m_excludes = asPatterns( excludes );

        m_lister = new DirectoryLister( m_directory, m_includes, m_excludes );
    }

    private Pattern[] asPatterns( final String[] patterns )
    {
        if( patterns == null || patterns.length == 0 )
        {
            return new Pattern[0];
        }
        final Collection<Pattern> converted = new ArrayList<Pattern>();
        for( String pattern : patterns )
        {
            converted.add( ListerUtils.parseFilter( pattern ) );
        }
        return converted.toArray( new Pattern[converted.size()] );
    }

    public void execute( final ExecutionContext context )
        throws Exception
    {
        final Collection<File> files = new ArrayList<File>();
        for( URL url : m_lister.list() )
        {
            files.add( new File( url.toURI() ) );
        }
        context.set( FILES, files );
    }

    public void cancel()
        throws Exception
    {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString()
    {
        return String.format(
            "Lists directory [%s] including(%s) excluding (%s) ",
            m_directory, Arrays.deepToString( m_includes ), Arrays.deepToString( m_excludes )
        );
    }
}