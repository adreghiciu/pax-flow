package org.ops4j.pax.flow.recipes.internal.trigger;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class TimerUtils
{

    public static long convertToMillis( final String duration )
    {
        if( duration == null || duration.trim().length() == 0 )
        {
            return 0L;
        }
        int factor = 1;
        long converted = 0;
        try
        {
            converted = Long.parseLong( duration );
        }
        catch( NumberFormatException ignore )
        {
            try
            {
                if( duration.endsWith( "ms" ) )
                {
                    factor = 1;
                    converted = Long.parseLong( duration.replace( "ms", "" ) );
                }
                else if( duration.endsWith( "s" ) )
                {
                    factor = 1000;
                    converted = Long.parseLong( duration.replace( "s", "" ) );
                }
                else if( duration.endsWith( "m" ) )
                {
                    factor = 60 * 1000;
                    converted = Long.parseLong( duration.replace( "m", "" ) );
                }
                else if( duration.endsWith( "h" ) )
                {
                    factor = 60 * 60 * 1000;
                    converted = Long.parseLong( duration.replace( "h", "" ) );
                }
                else if( duration.endsWith( "D" ) )
                {
                    factor = 24 * 60 * 60 * 1000;
                    converted = Long.parseLong( duration.replace( "D", "" ) );
                }
                else if( duration.endsWith( "W" ) )
                {
                    factor = 7 * 24 * 60 * 60 * 1000;
                    converted = Long.parseLong( duration.replace( "W", "" ) );
                }
                else
                {
                    throw new NumberFormatException(
                        String.format( "Could not convert [%s] to a valid duration", duration )
                    );
                }
            }
            catch( NumberFormatException e )
            {
                throw new NumberFormatException(
                    String.format( "Could not convert [%s] to a valid duration", duration )
                );
            }
        }
        return converted * factor;
    }

}
