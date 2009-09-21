package org.ops4j.pax.flow.recipes.flow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowType;
import static org.ops4j.pax.flow.api.FlowType.*;
import org.ops4j.pax.flow.api.JobDescription;
import org.ops4j.pax.flow.api.Property;
import static org.ops4j.pax.flow.api.Property.*;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
import org.ops4j.pax.flow.api.TriggerType;
import static org.ops4j.pax.flow.api.TriggerType.*;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import org.ops4j.pax.flow.api.helpers.ImmutableConfiguration;
import static org.ops4j.pax.flow.api.helpers.ImmutableConfiguration.*;
import org.ops4j.pax.flow.api.helpers.ImmutableJobDescription;
import static org.ops4j.pax.flow.api.helpers.ImmutableJobDescription.*;
import static org.ops4j.pax.flow.api.helpers.TypedExecutionContext.*;

/**
 * Parses a JSON file containing a job description into a {@link JobDescription}.
 *
 * @author Alin Dreghiciu
 */
public class ParseJsonJobDescription
    extends CancelableFlow
    implements Flow
{

    private static final Log LOG = LogFactory.getLog( ParseJsonJobDescription.class );

    public static final PropertyName FILE = propertyName( "file" );

    @Override
    protected void run( final ExecutionContext context )
        throws Exception
    {
        final InputStream source = typedExecutionContext( context ).mandatory( FILE, InputStream.class );

        FlowType flowType = null;
        Configuration flowConfig = ImmutableConfiguration.withoutConfiguration();
        TriggerType triggerType = null;
        Configuration triggerConfig = ImmutableConfiguration.withoutConfiguration();

        final JsonFactory jFactory = new JsonFactory();
        jFactory.enable( JsonParser.Feature.ALLOW_COMMENTS );

        final JsonParser jp = jFactory.createJsonParser( source );
        if( jp.nextToken() != JsonToken.START_OBJECT )
        {
            throw new Exception( "Parsed file is not in JSON format" );
        }

        try
        {
            while( jp.nextToken() != JsonToken.END_OBJECT )
            {
                String currentName = jp.getCurrentName();
                jp.nextToken();

                if( "when".equals( currentName ) || "trigger".equals( currentName ) )
                {
                    while( jp.nextToken() != JsonToken.END_OBJECT )
                    {
                        currentName = jp.getCurrentName();
                        jp.nextToken();

                        if( "trigger".equals( currentName ) || "triggerType".equals( currentName ) )
                        {
                            triggerType = triggerType( jp.getText() );
                        }
                        else if( "config".equals( currentName ) || "configuration".equals( currentName ) )
                        {
                            flowConfig = parseConfig( jp );
                        }
                    }
                }

                if( "do".equals( currentName ) || "flow".equals( currentName ) )
                {
                    while( jp.nextToken() != JsonToken.END_OBJECT )
                    {
                        currentName = jp.getCurrentName();
                        jp.nextToken();

                        if( "flow".equals( currentName ) || "flow".equals( currentName ) )
                        {
                            flowType = flowType( jp.getText() );
                        }
                        else if( "config".equals( currentName ) || "configuration".equals( currentName ) )
                        {
                            flowConfig = parseConfig( jp );
                        }
                    }
                }
            }
        }
        finally
        {
            jp.close();
        }

        if( flowType != null && triggerType != null )
        {
            final ImmutableJobDescription description = immutableJobDescription(
                flowType, flowConfig, triggerType, triggerConfig
            );
            context.set( ScheduleJob.JOB_DESCRIPTION, description );
        }
        else
        {
            throw new Exception(
                String.format(
                    "Parsed file is missing [%s %s] fields",
                    ( flowType == null ? "flowType" : "" ),
                    ( triggerType == null ? "triggerType" : "" )
                )
            );
        }
    }

    private Configuration parseConfig( final JsonParser jp )
        throws IOException
    {
        final Collection<Property<?>> properties = new ArrayList<Property<?>>();

        while( jp.nextToken() != JsonToken.END_OBJECT )
        {
            final String currentName = jp.getCurrentName();
            jp.nextToken();

            properties.add( property( propertyName( currentName ), jp.getText() ) );
        }
        return immutableConfiguration( properties );
    }

    @Override
    public String toString()
    {
        return "Parse JSON job description file";
    }

}