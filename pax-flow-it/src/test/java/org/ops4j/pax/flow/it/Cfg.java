package org.ops4j.pax.flow.it;

import static org.ops4j.pax.exam.CoreOptions.*;
import org.ops4j.pax.exam.Option;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.*;
import org.ops4j.pax.exam.options.CompositeOption;
import static org.ops4j.pax.exam.options.WrappedUrlProvisionOption.OverwriteMode.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class Cfg
{

    public static class Basic
        implements CompositeOption
    {

        public Option[] getOptions()
        {
            return options(
                profile( "peaberry" ).version( "1.1.1" ),
                mavenBundle( "org.ops4j.pax.flow", "pax-flow-api" ).versionAsInProject(),
                wrappedBundle( mavenBundle( "org.mockito", "mockito-all", "1.8.0" ) )
                    .overwriteManifest( MERGE )
                    .imports( "*;resolution:=optional" )
                    .exports( "*" ),
                //mockitoBundles().version( "1.8.0" ),
                compendiumProfile(),
                logProfile()
            );
        }

    }

    public static class Triggers
        implements CompositeOption
    {

        public Option[] getOptions()
        {
            return options(
                new Basic(),
                mavenBundle( "org.ops4j.pax.flow", "pax-flow-trigger" ).versionAsInProject()
            );
        }

    }

    public static class Runtime
        implements CompositeOption
    {

        public Option[] getOptions()
        {
            return options(
                new Basic(),
                mavenBundle( "org.ops4j.pax.flow", "pax-flow-runtime" ).versionAsInProject()
            );
        }

    }

    public static class RuntimeSetup
        implements CompositeOption
    {

        public Option[] getOptions()
        {
            return options(
                new Runtime(),
                new Triggers(),
                mavenBundle( "org.ops4j.pax.flow", "pax-flow-runtime-setup" ).versionAsInProject()
            );
        }

    }

    /**
     * JAVADOC
     *
     * @author Alin Dreghiciu
     */
    public static class Debug
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
}
