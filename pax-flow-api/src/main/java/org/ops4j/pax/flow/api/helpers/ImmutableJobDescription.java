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

    private final JobName jobName;
    private final FlowType flowType;
    private final Configuration flowConfiguration;
    private final TriggerType triggerType;
    private final Configuration triggerConfiguration;

    private ImmutableJobDescription( final JobName jobName,
                                     final FlowType flowType,
                                     final Configuration flowConfiguration,
                                     final TriggerType triggerType,
                                     final Configuration triggerConfiguration )
    {
        this.jobName = jobName;
        this.flowType = flowType;
        this.flowConfiguration = flowConfiguration;
        this.triggerType = triggerType;
        this.triggerConfiguration = triggerConfiguration;
    }

    public JobName name()
    {
        return jobName;
    }

    public FlowType flowType()
    {
        return flowType;
    }

    public Configuration flowConfiguration()
    {
        return flowConfiguration;
    }

    public TriggerType triggerType()
    {
        return triggerType;
    }

    public Configuration triggerConfiguration()
    {
        return triggerConfiguration;
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
        return format( "When [%s] fires execute [%s]", triggerType, flowType );
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
