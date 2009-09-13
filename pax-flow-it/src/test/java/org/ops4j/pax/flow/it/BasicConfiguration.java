package org.ops4j.pax.flow.it;

import static org.ops4j.pax.exam.CoreOptions.*;
import org.ops4j.pax.exam.Option;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.*;
import static org.ops4j.pax.exam.junit.JUnitOptions.*;
import org.ops4j.pax.exam.options.CompositeOption;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class BasicConfiguration
    implements CompositeOption
{

    public Option[] getOptions()
    {
        return options(
            profile( "peaberry" ).version( "1.1.1" ),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-osw-complete" ).versionAsInProject(),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-osq-complete" ).versionAsInProject(),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-api" ).versionAsInProject(),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-trigger" ).versionAsInProject(),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-osworkflow" ).versionAsInProject(),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-runtime" ).versionAsInProject(),
            mockitoBundles(),
            logProfile()
        );
    }

}
