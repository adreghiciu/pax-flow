package org.ops4j.pax.flow.recipes.flow.cm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
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
import static org.ops4j.pax.flow.api.JobName.*;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;
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
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ParsePropertiesFileAsConfiguration
    extends CancelableFlow
    implements Flow
{

    public static final PropertyName FILE = propertyName( "file" );

    public static final PropertyName CONFIGURATION = propertyName( "configuration" );

    @Override
    protected void run( final ExecutionContext context )
        throws Exception
    {
        final InputStream source = typedExecutionContext( context ).mandatory( FILE, InputStream.class );

        final Properties properties = new Properties();
        properties.load( source );

        context.add( executionProperty( CONFIGURATION, properties ) );
    }

    @Override
    public String toString()
    {
        return "Parse Properties file as configuration";
    }

}