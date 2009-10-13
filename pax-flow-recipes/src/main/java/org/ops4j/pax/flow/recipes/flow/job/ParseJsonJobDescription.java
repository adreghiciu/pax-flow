package org.ops4j.pax.flow.recipes.flow.job;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.ConfigurationProperty;
import static org.ops4j.pax.flow.api.ConfigurationProperty.*;
import org.ops4j.pax.flow.api.ExecutionContext;
import static org.ops4j.pax.flow.api.ExecutionProperty.*;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowType;
import static org.ops4j.pax.flow.api.FlowType.*;
import org.ops4j.pax.flow.api.JobDescription;
import static org.ops4j.pax.flow.api.JobName.*;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
import org.ops4j.pax.flow.api.TriggerName;
import org.ops4j.pax.flow.api.TriggerType;
import static org.ops4j.pax.flow.api.TriggerType.*;
import org.ops4j.pax.flow.api.helpers.CancelableFlow;
import org.ops4j.pax.flow.api.helpers.ImmutableConfiguration;
import static org.ops4j.pax.flow.api.helpers.ImmutableConfiguration.*;
import org.ops4j.pax.flow.api.helpers.ImmutableJobDescription;
import static org.ops4j.pax.flow.api.helpers.ImmutableJobDescription.*;
import org.ops4j.pax.flow.api.helpers.TypedExecutionContext;
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

    public static final PropertyName FILE = propertyName( "file" );
    public static final PropertyName JOB_NAME = propertyName( "jobName" );

    @Override
    protected void run( final ExecutionContext context )
        throws Exception
    {
        final TypedExecutionContext executionContext = typedExecutionContext( context );

        final String sourceName = executionContext.mandatory( JOB_NAME, String.class );
        final InputStream source = executionContext.mandatory( FILE, InputStream.class );

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
                        else if( "config".equals( currentName )
                                 || "configuration".equals( currentName )
                                 || "configuredWith".equals( currentName ) )
                        {
                            triggerConfig = parseConfig( jp );
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
                        else if( "config".equals( currentName )
                                 || "configuration".equals( currentName )
                                 || "configuredWith".equals( currentName ) )
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

        if( triggerType == null && flowType != null )
        {
            triggerType = triggerType( flowType.value() + TriggerName.DEFAULT_TRIGGER_SUFFIX );
            triggerConfig = flowConfig;
        }

        if( flowType != null && triggerType != null )
        {
            final ImmutableJobDescription description = immutableJobDescription(
                jobName( sourceName ), flowType, flowConfig, triggerType, triggerConfig
            );
            context.add( executionProperty( ScheduleJob.JOB_DESCRIPTION, description ) );
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
        final Collection<ConfigurationProperty<?>> properties = new ArrayList<ConfigurationProperty<?>>();

        while( jp.nextToken() != JsonToken.END_OBJECT )
        {
            final String currentName = jp.getCurrentName();
            jp.nextToken();

            if( jp.getCurrentToken() == JsonToken.START_ARRAY )
            {
                properties.add(
                    configurationProperty( propertyName( currentName ), parseArray( jp ) )
                );
            }
            else
            {
                properties.add(
                    configurationProperty( propertyName( currentName ), jp.getText() )
                );
            }
        }
        return immutableConfiguration( properties );
    }

    private String[] parseArray( final JsonParser jp )
        throws IOException
    {
        final Collection<String> array = new ArrayList<String>();

        while( jp.nextToken() != JsonToken.END_ARRAY )
        {
            array.add( jp.getText() );
        }
        return array.toArray( new String[array.size()] );
    }

    @Override
    public String toString()
    {
        return "Parse JSON job description file";
    }

}