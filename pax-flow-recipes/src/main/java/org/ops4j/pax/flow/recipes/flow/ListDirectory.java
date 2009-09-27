package org.ops4j.pax.flow.recipes.flow;

import java.io.File;
import static java.lang.String.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.io.DirectoryLister;
import org.ops4j.io.ListerUtils;
import org.ops4j.pax.flow.api.ExecutionContext;
import static org.ops4j.pax.flow.api.ExecutionProperty.*;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.PropertyName;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

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
    public static final PropertyName TIMESTAMPS = PropertyName.propertyName( "timestamps" );

    public static final PropertyName ADDED_FILES = PropertyName.propertyName( "addedFiles" );
    public static final PropertyName MODIFIED_FILES = PropertyName.propertyName( "modifiedFiles" );
    public static final PropertyName DELETED_FILES = PropertyName.propertyName( "deletedFiles" );

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
        final Map<File, Long> files = new HashMap<File, Long>();

        if( m_directory.exists() )
        {
            final List<URL> foundUrls = m_lister.list();

            for( URL url : foundUrls )
            {
                final File file = new File( url.toURI() );
                files.put( file, file.lastModified() );
            }
        }
        else
        {
            LOG.debug( format( "Directory [%s] does not exist, so it cannot be scanned", m_directory ) );
        }

        // TODO figure out how to use generics
        final Map<File, Long> previousFiles = new HashMap<File, Long>(
            typedExecutionContext( context ).optional( TIMESTAMPS, Map.class, new HashMap<File, Long>() )
        );

        final Collection<File> added = new ArrayList<File>();
        final Collection<File> modified = new ArrayList<File>();
        final Collection<File> deleted = new ArrayList<File>( previousFiles.keySet() );

        for( Map.Entry<File, Long> entry : files.entrySet() )
        {
            final Long timestamp = previousFiles.get( entry.getKey() );

            boolean exists = entry.getKey().exists();

            if( exists )
            {
                if( timestamp == null )
                {
                    added.add( entry.getKey() );
                }
                else if( entry.getKey().lastModified() > timestamp )
                {
                    modified.add( entry.getKey() );
                }
                deleted.remove( entry.getKey() );
            }
        }

        context.add( persistentExecutionProperty( TIMESTAMPS, files ) );
        context.add( executionProperty( FILES, files ) );
        context.add( executionProperty( ADDED_FILES, added ) );
        context.add( executionProperty( MODIFIED_FILES, modified ) );
        context.add( executionProperty( DELETED_FILES, deleted ) );

        final String message = format(
            "Found %d files (%d added,%d modified,%d deleted) in [%s]",
            files.size(),
            added.size(), modified.size(), deleted.size(),
            m_directory
        );
        if( added.size() > 0 || modified.size() > 0 || deleted.size() > 0 )
        {
            LOG.info( message );
        }
        else
        {
            LOG.debug( message );
        }
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