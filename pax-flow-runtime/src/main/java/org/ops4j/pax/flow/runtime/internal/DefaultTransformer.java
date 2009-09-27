package org.ops4j.pax.flow.runtime.internal;

import static java.lang.String.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.flow.api.ExecutionContext;
import org.ops4j.pax.flow.api.ExecutionTarget;
import org.ops4j.pax.flow.api.Flow;
import org.ops4j.pax.flow.api.FlowFactory;
import org.ops4j.pax.flow.api.FlowFactoryRegistry;
import org.ops4j.pax.flow.api.JobDescription;
import org.ops4j.pax.flow.api.Transformer;
import org.ops4j.pax.flow.api.Trigger;
import org.ops4j.pax.flow.api.TriggerFactory;
import org.ops4j.pax.flow.api.TriggerFactoryRegistry;
import org.ops4j.peaberry.activation.Start;
import org.ops4j.peaberry.activation.Stop;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
@Singleton
public class DefaultTransformer
    implements Transformer
{

    private static final Log LOG = LogFactory.getLog( DefaultTransformer.class );

    private final Collection<Job> m_jobs;

    private final ExecutorService m_executorService;

    private final FlowFactoryRegistry m_flowFactoryRegistry;
    private final TriggerFactoryRegistry m_triggerFactoryRegistry;

    @Inject
    public DefaultTransformer( final ExecutorService executorService,
                               final FlowFactoryRegistry flowFactoryRegistry,
                               final TriggerFactoryRegistry triggerFactoryRegistry )
    {
        m_executorService = executorService;
        m_flowFactoryRegistry = flowFactoryRegistry;
        m_triggerFactoryRegistry = triggerFactoryRegistry;

        m_jobs = new ArrayList<Job>();
    }

    @Start
    public void start()
    {
        LOG.info( format( "Starting transformer (%s jobs active)", m_jobs.size() ) );
        for( Job job : m_jobs )
        {
            try
            {
                job.trigger.start();
            }
            catch( Exception ignore )
            {
                LOG.error( format( "Could not start trigger [%s]", job.trigger ), ignore );
            }
        }
    }

    @Stop
    public void stop()
    {
        for( Job job : m_jobs )
        {
            try
            {
                job.trigger.stop();
            }
            catch( Exception ignore )
            {
                LOG.error( format( "Could not stop trigger [%s]", job.trigger ), ignore );
            }
        }
        LOG.info( "Stopped transformer" );
    }

    public void schedule( final JobDescription description )
        throws Exception
    {
        // VALIDATE

        final TriggerFactory triggerFactory = m_triggerFactoryRegistry.get( description.triggerType() );
        if( triggerFactory == null )
        {
            throw new Exception( format( "Could not find a trigger factory of type [%s]", description.triggerType() ) );
        }

        final Trigger trigger = triggerFactory.create(
            description.triggerConfiguration(),
            new FlowScheduler( description )
        );

        final Job job = new Job( description, trigger );
        m_jobs.add( job );

        LOG.info( format( "Scheduled job [%s]", job ) );

        trigger.start();
    }

    private class FlowScheduler
        implements ExecutionTarget
    {

        private final JobDescription m_description;

        private final PersistentExecutionContext m_executionContext;

        public FlowScheduler( final JobDescription description )
        {
            m_description = description;
            m_executionContext = new PersistentExecutionContext();
        }

        public void execute( final ExecutionContext executionContext )
        {
            m_executorService.submit(
                new Callable<Object>()
                {
                    public Object call()
                    {
                        try
                        {
                            LOG.debug( format( "Starting flow of type [%s]", m_description.flowType() ) );
                            final FlowFactory flowFactory = m_flowFactoryRegistry.get( m_description.flowType() );
                            if( flowFactory == null )
                            {
                                LOG.warn(
                                    format( "Could not find a flow factory of type [%s]", m_description.flowType() )
                                );
                            }
                            else
                            {
                                final Flow flow = flowFactory.create( m_description.flowConfiguration() );
                                m_executionContext.useTransientContext( executionContext );
                                flow.execute( m_executionContext );
                                LOG.debug( format( "Finished execution of [%s]", flow ) );
                            }
                        }
                        catch( Throwable ignore )
                        {
                            LOG.warn(
                                format( "Execution of [%s] could not be completed.", m_description.flowType() ),
                                ignore
                            );
                        }
                        return null;
                    }
                }
            );
        }
    }

    private static class Job
    {

        final JobDescription jobDescription;
        final Trigger trigger;

        public Job( final JobDescription jobDescription,
                    final Trigger trigger )
        {
            this.jobDescription = jobDescription;
            this.trigger = trigger;
        }

        @Override
        public String toString()
        {
            return format( "Execute [%s] when [%s]", jobDescription.flowType(), trigger );
        }

    }

}
