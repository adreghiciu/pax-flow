package org.ops4j.pax.flow.api.helpers;

import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.FlowType;
import org.ops4j.pax.flow.api.JobDescription;
import org.ops4j.pax.flow.api.TriggerType;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ImmutableJobDescription
    implements JobDescription
{

    private final FlowType m_flowType;
    private final Configuration m_flowConfiguration;
    private final TriggerType m_triggerType;
    private final Configuration m_triggerConfiguration;

    public ImmutableJobDescription( final FlowType flowType,
                                    final Configuration flowConfiguration,
                                    final TriggerType triggerType,
                                    final Configuration triggerConfiguration )
    {
        m_flowType = flowType;
        m_flowConfiguration = flowConfiguration;
        m_triggerType = triggerType;
        m_triggerConfiguration = triggerConfiguration;
    }

    public FlowType flowType()
    {
        return m_flowType;
    }

    public Configuration flowConfiguration()
    {
        return m_flowConfiguration;
    }

    public TriggerType triggerType()
    {
        return m_triggerType;
    }

    public Configuration triggerConfiguration()
    {
        return m_triggerConfiguration;
    }

    @Override
    public String toString()
    {
        return String.format( "Flow fo type [%s] fired by [%s]", m_flowType, m_triggerType );
    }

    public static ImmutableJobDescription immutableJobDescription( final FlowType flowType,
                                                                   final Configuration flowConfiguration,
                                                                   final TriggerType triggerType,
                                                                   final Configuration triggerConfiguration )
    {
        return new ImmutableJobDescription( flowType, flowConfiguration, triggerType, triggerConfiguration );
    }

}