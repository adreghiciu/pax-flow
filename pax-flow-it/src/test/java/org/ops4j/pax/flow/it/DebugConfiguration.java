package org.ops4j.pax.flow.it;

import static org.ops4j.pax.exam.CoreOptions.*;
import org.ops4j.pax.exam.Option;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.*;
import org.ops4j.pax.exam.options.CompositeOption;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class DebugConfiguration
    implements CompositeOption
{

    public Option[] getOptions()
    {
        return options(
            vmOption( "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005" ),
            waitForFrameworkStartup()
        );
    }

}