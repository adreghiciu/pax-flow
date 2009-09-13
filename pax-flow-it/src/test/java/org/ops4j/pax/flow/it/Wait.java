package org.ops4j.pax.flow.it;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class Wait
{

    public static void waitFor( long s )
    {
        try
        {
            Thread.sleep( s * 1000 );
        }
        catch( InterruptedException e )
        {
            e.printStackTrace();
        }
    }

}
