package org.ops4j.pax.flow.api.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ops4j.pax.flow.api.Configuration;
import static org.ops4j.pax.flow.api.PropertyName.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ConfigurationUtils
{

    /**
     * Pattern used for replacing placeholders.
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile( "(.*?\\$\\{)([.[^\\$]]+?)(\\}.*)" );

    private ConfigurationUtils()
    {
        // utility class
    }

    /**
     * Replaces placeholders = ${*}.
     *
     * @param configuration configuration to be searched for value
     * @param value         the string where the place holders should be replaced
     *
     * @return replaced place holders or the original if there are no place holders or a value for place holder could
     *         not be found
     */
    public static String replacePlaceholders( final Configuration configuration,
                                              final String value )
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
                final Object placeholderValue = configuration.get( propertyName( placeHolderName ) );
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
            replaced = replacePlaceholders( configuration, replaced );
        }
        return replaced;
    }


}
