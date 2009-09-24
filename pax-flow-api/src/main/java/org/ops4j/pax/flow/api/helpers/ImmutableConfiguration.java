/*
 * Copyright 2009 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.flow.api.helpers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.ConfigurationProperty;
import org.ops4j.pax.flow.api.PropertyName;
import static org.ops4j.pax.flow.api.PropertyName.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ImmutableConfiguration
    implements Configuration
{

    /**
     * Pattern used for replacing placeholders.
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile( "(.*?\\$\\{)([.[^\\$]]+?)(\\}.*)" );

    private final Map<PropertyName, ConfigurationProperty<?>> m_properties;

    private ImmutableConfiguration( final ConfigurationProperty<?>... properties )
    {
        m_properties = new HashMap<PropertyName, ConfigurationProperty<?>>();
        if( properties != null && properties.length > 0 )
        {
            for( ConfigurationProperty<?> property : properties )
            {
                m_properties.put( property.name(), property );
            }
        }
    }

    public <T> T get( final PropertyName name )
    {
        final ConfigurationProperty<?> property = m_properties.get( name );
        if( property == null )
        {
            return null;
        }
        final Object value = property.value();

        if( value instanceof String
            && !name.value().equals( value ) )
        {
            return (T) replacePlaceholders( (String) value );
        }
        return (T) value;
    }

    public <T> T get( final PropertyName name, final T defaultValue )
    {
        final ConfigurationProperty<?> property = m_properties.get( name );

        Object value;
        if( property == null )
        {
            value = defaultValue;
        }
        else
        {
            value = property.value();
        }

        if( value == null )
        {
            return null;
        }

        if( value instanceof String
            && !name.value().equals( value ) )
        {
            return (T) replacePlaceholders( (String) value );
        }
        return (T) value;
    }

    public Iterable<PropertyName> getNames()
    {
        return Collections.unmodifiableSet( m_properties.keySet() );
    }

    public static ImmutableConfiguration immutableConfiguration( final ConfigurationProperty<?>... properties )
    {
        return new ImmutableConfiguration( properties );
    }

    public static ImmutableConfiguration immutableConfiguration( final Collection<ConfigurationProperty<?>> properties )
    {
        if( properties == null )
        {
            return new ImmutableConfiguration( (ConfigurationProperty<?>[]) null );
        }
        return new ImmutableConfiguration( properties.toArray( new ConfigurationProperty<?>[properties.size()] ) );
    }

    public static ImmutableConfiguration withoutConfiguration()
    {
        return new ImmutableConfiguration();
    }

    /**
     * Replaces placeholders = ${*}.
     *
     * @param value the string where the place holders should be replaced
     *
     * @return replaced place holders or the original if there are no place holders or a value for place holder could
     *         not be found
     */
    private String replacePlaceholders( final String value )
    {
        if( value == null )
        {
            return null;
        }
        String replaced = value;
        String rest = value;
        while( rest != null && rest.length() != 0 )
        {
            final Matcher matcher = PLACEHOLDER_PATTERN.matcher( rest );
            if( matcher.matches() && matcher.groupCount() == 3 )
            {
                // groups 2 contains the placeholder name
                final String fullPlaceholderName = matcher.group( 2 );
                String placeHolderName = fullPlaceholderName;
                String defaultValue = null;
                int indexOfSeparator = fullPlaceholderName.indexOf( ":" );
                if( indexOfSeparator > 0 )
                {
                    placeHolderName = fullPlaceholderName.substring( 0, indexOfSeparator );
                    defaultValue = fullPlaceholderName.substring( indexOfSeparator + 1 );
                }
                final Object placeholderValue = get( propertyName( placeHolderName ) );
                if( placeholderValue != null )
                {
                    replaced = replaced.replace( "${" + fullPlaceholderName + "}", placeholderValue.toString() );
                }
                else if( defaultValue != null )
                {
                    replaced = replaced.replace( "${" + fullPlaceholderName + "}", defaultValue );
                }
                rest = matcher.group( 3 );
            }
            else
            {
                rest = null;
            }
        }
        if( replaced != null && !replaced.equals( value ) )
        {
            replaced = replacePlaceholders( replaced );
        }
        return replaced;
    }

}