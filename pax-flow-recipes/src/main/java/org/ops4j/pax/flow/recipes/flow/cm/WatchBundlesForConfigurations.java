package org.ops4j.pax.flow.recipes.flow.cm;

import static java.lang.String.*;
import java.util.HashMap;
import java.util.Map;
import com.google.inject.Inject;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowName;
import static org.ops4j.pax.flow.api.FlowName.*;
import org.ops4j.pax.flow.api.FlowType;
import static org.ops4j.pax.flow.api.FlowType.*;
import org.ops4j.pax.flow.api.helpers.ForEachFlow;
import org.ops4j.pax.flow.api.helpers.SequentialFlow;
import org.ops4j.pax.flow.api.helpers.SwitchFlow;
import static org.ops4j.pax.flow.api.helpers.SwitchFlow.SwitchCase.*;
import org.ops4j.pax.flow.recipes.trigger.BundleContentWatcher;
import org.ops4j.pax.flow.recipes.trigger.ServiceWatcher;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class WatchBundlesForConfigurations
    extends SequentialFlow
    implements Flow
{

    public WatchBundlesForConfigurations( final FlowName flowName,
                                          final ConfigurationAdmin configurationAdmin )
    {
        super(
            flowName,
            new SwitchFlow(
                ServiceWatcher.EVENT,
                switchCase(
                    ServiceWatcher.ADDED,
                    new ForEachFlow(
                        BundleContentWatcher.URLS, ParsePropertiesFileAsConfiguration.FILE,
                        new SequentialFlow(
                            flowName( format( "%s::%s", flowName, "Added" ) ), // TODO do we need a name?
                            new DeterminePidFromURL(
                                ParsePropertiesFileAsConfiguration.FILE,
                                AddConfiguration.PID,
                                AddConfiguration.FACTORY_PID
                            ),
                            new ParsePropertiesFileAsConfiguration(),
                            new AddConfiguration( configurationAdmin )
                        )
                    )
                ),
                switchCase(
                    ServiceWatcher.REMOVED,
                    new ForEachFlow(
                        BundleContentWatcher.URLS, ParsePropertiesFileAsConfiguration.FILE,
                        new SequentialFlow(
                            flowName( format( "%s::%s", flowName, "Deleted" ) ), // TODO do we need a name?
                            new DeterminePidFromURL(
                                ParsePropertiesFileAsConfiguration.FILE,
                                DeleteConfiguration.PID,
                                DeleteConfiguration.FACTORY_PID
                            ),
                            new DeleteConfiguration( configurationAdmin )
                        )
                    )
                )
            )
        );
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( WatchBundlesForConfigurations.class );

        private final ConfigurationAdmin m_configurationAdmin;

        private long m_counter;

        @Inject
        public Factory( final ConfigurationAdmin configurationAdmin )
        {
            // VALIDATE
            m_configurationAdmin = configurationAdmin;
        }

        public FlowType type()
        {
            return TYPE;
        }

        public WatchBundlesForConfigurations create( final Configuration configuration )
        {
            return new WatchBundlesForConfigurations(
                flowName( String.format( "%s::%d", type(), m_counter++ ) ),
                m_configurationAdmin
            );
        }

        @Override
        public String toString()
        {
            return String.format( "Flow factory for type [%s] (%d instances)", type(), m_counter );
        }

        public static Map<String, String> attributes()
        {
            final Map<String, String> attributes = new HashMap<String, String>();

            attributes.put( "flowType", TYPE.toString() );

            return attributes;
        }

    }

}