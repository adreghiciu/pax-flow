package org.ops4j.pax.flow.it;

import static org.ops4j.pax.exam.CoreOptions.*;
import org.ops4j.pax.exam.Option;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.*;
import org.ops4j.pax.exam.options.CompositeOption;
import org.ops4j.pax.exam.options.WrappedUrlProvisionOption;

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
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-api" ).versionAsInProject(),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-trigger" ).versionAsInProject(),
            mavenBundle( "org.ops4j.pax.flow", "pax-flow-runtime" ).versionAsInProject(),
            wrappedBundle( mavenBundle( "org.mockito", "mockito-all", "1.8.0" ) )
                .overwriteManifest( WrappedUrlProvisionOption.OverwriteMode.MERGE )
                .imports( "*;resolution:=optional" )
                .exports( "*" ),
            //mockitoBundles().version( "1.8.0" ),
            compendiumProfile(),
            logProfile()
        );
    }

}
