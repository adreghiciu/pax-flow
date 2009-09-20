package org.ops4j.pax.flow.recipes.flow;

import java.io.File;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowName;
import static org.ops4j.pax.flow.api.FlowName.*;
import org.ops4j.pax.flow.api.FlowType;
import static org.ops4j.pax.flow.api.FlowType.*;
import org.ops4j.pax.flow.api.helpers.ImmutableFlow;
import org.ops4j.pax.flow.api.helpers.TypedConfiguration;
import static org.ops4j.pax.flow.api.helpers.TypedConfiguration.*;
import org.ops4j.pax.flow.recipes.action.ListDirectory;
import org.ops4j.pax.flow.recipes.internal.Properties;
import static org.ops4j.pax.flow.recipes.internal.Properties.*;
import static org.ops4j.pax.flow.recipes.internal.Properties.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ScanDirectoryForJobDescriptionsFlow
    extends ImmutableFlow
    implements Flow
{

    public ScanDirectoryForJobDescriptionsFlow( final FlowName flowName,
                                                final File directory,
                                                final String[] includes,
                                                final String[] excludes )
    {
        super(
            flowName,
            new ListDirectory( directory, includes, excludes )
        );
    }

    public static class Factory
        implements FlowFactory
    {

        public static final FlowType TYPE = flowType( ScanDirectoryForJobDescriptionsFlow.class );

        private long m_counter;

        public FlowType type()
        {
            return TYPE;
        }

        public ScanDirectoryForJobDescriptionsFlow create( final Configuration configuration )
        {
            final TypedConfiguration cfg = typedConfiguration( configuration );

            return new ScanDirectoryForJobDescriptionsFlow(
                flowName( String.format( "%s::%d", type(), m_counter++ ) ),
                new File( cfg.mandatory( DIRECTORY, String.class ) ),
                cfg.optional( INCLUDES, String[].class ),
                cfg.optional( EXCLUDES, String[].class )
            );
        }

        @Override
        public String toString()
        {
            return String.format( "Flow factory for type [%s] (%d instances)", type(), m_counter );
        }

    }

}