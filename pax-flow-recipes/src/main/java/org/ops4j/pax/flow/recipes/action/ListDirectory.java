package org.ops4j.pax.flow.recipes.action;

import java.io.File;
import static java.lang.String.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.io.DirectoryLister;
import org.ops4j.io.ListerUtils;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.PropertyName;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;

/**
 * Lists a file system directory.
 *
 * @author Alin Dreghiciu
 */
public class ListDirectory
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( ListDirectory.class );

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

    public void run( final ExecutionContext context )
        throws Exception
    {
        final Collection<File> files = new ArrayList<File>();

        if( m_directory.exists() )
        {
            final List<URL> foundUrls = m_lister.list();

            LOG.debug( format( "Found %d files in [%s]", foundUrls.size(), m_directory ) );

            for( URL url : foundUrls )
            {
                files.add( new File( url.toURI() ) );
            }
        }
        else
        {
            LOG.debug( format( "Directory [%s] does not exist, so it cannot be scanned", m_directory ) );
        }
        
        context.set( FILES, files );
    }

    @Override
    public String toString()
    {
        return format(
            "Lists directory [%s] including(%s) excluding (%s) ",
            m_directory, Arrays.deepToString( m_includes ), Arrays.deepToString( m_excludes )
        );
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

}