package org.ops4j.pax.flow.api.helpers;

import static java.lang.String.*;
import org.ops4j.pax.flow.api.Configuration;
import org.ops4j.pax.flow.api.FlowType;
import org.ops4j.pax.flow.api.JobDescription;
import org.ops4j.pax.flow.api.JobName;
import org.ops4j.pax.flow.api.TriggerType;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class ImmutableJobDescription
    implements JobDescription
{

    private final JobName m_jobName;
    private final FlowType m_flowType;
    private final Configuration m_flowConfiguration;
    private final TriggerType m_triggerType;
    private final Configuration m_triggerConfiguration;

    private ImmutableJobDescription( final JobName jobName,
                                     final FlowType flowType,
                                     final Configuration flowConfiguration,
                                     final TriggerType triggerType,
                                     final Configuration triggerConfiguration )
    {
        m_jobName = jobName;
        m_flowType = flowType;
        m_flowConfiguration = flowConfiguration;
        m_triggerType = triggerType;
        m_triggerConfiguration = triggerConfiguration;
    }

    public JobName name()
    {
        return m_jobName;
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
    public boolean equals( final Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( !( o instanceof JobDescription ) )
        {
            return false;
        }

        final JobDescription that = (JobDescription) o;

        if( !name().equals( that.name() ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return name().hashCode();
    }

    @Override
    public String toString()
    {
        return format( "When [%s] fires execute [%s]", m_triggerType, m_flowType );
    }

    public static ImmutableJobDescription immutableJobDescription( final JobName jobName,
                                                                   final FlowType flowType,
                                                                   final Configuration flowConfiguration,
                                                                   final TriggerType triggerType,
                                                                   final Configuration triggerConfiguration )
    {
        return new ImmutableJobDescription( jobName, flowType, flowConfiguration, triggerType, triggerConfiguration );
    }

}
