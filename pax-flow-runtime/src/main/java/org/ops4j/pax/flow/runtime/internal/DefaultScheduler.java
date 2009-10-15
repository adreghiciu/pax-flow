package org.ops4j.pax.flow.runtime.internal;

import static java.lang.String.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
import org.ops4j.pax.flow.api.JobName;
import org.ops4j.pax.flow.api.RunNow;
import org.ops4j.pax.flow.api.Scheduler;
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
public class DefaultScheduler
    implements Scheduler
{

    private static final Log LOG = LogFactory.getLog( DefaultScheduler.class );

    private final Map<JobName, Job> jobs;

    private final ExecutorService executorService;

    private final FlowFactoryRegistry flowFactoryRegistry;
    private final TriggerFactoryRegistry triggerFactoryRegistry;

    @Inject
    public DefaultScheduler( final ExecutorService executorService,
                             final FlowFactoryRegistry flowFactoryRegistry,
                             final TriggerFactoryRegistry triggerFactoryRegistry )
    {
        this.executorService = executorService;
        this.flowFactoryRegistry = flowFactoryRegistry;
        this.triggerFactoryRegistry = triggerFactoryRegistry;

        jobs = new HashMap<JobName, Job>();
    }

    @Start
    public void start()
    {
        LOG.info( format( "Starting scheduler (%s jobs active)", jobs.size() ) );
        for( Job job : jobs.values() )
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
        for( Job job : jobs.values() )
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
        LOG.info( "Stopped scheduler" );
    }

    public void schedule( final JobDescription description )
        throws Exception
    {
        // VALIDATE

        final TriggerFactory triggerFactory = triggerFactoryRegistry.get( description.triggerType() );
        if( triggerFactory == null )
        {
            throw new Exception( format( "Could not find a trigger factory of type [%s]", description.triggerType() ) );
        }

        final FlowScheduler flowScheduler = new FlowScheduler( description );
        final Trigger trigger = triggerFactory.create(
            description.triggerConfiguration(),
            flowScheduler
        );

        final Job job = new Job( description, trigger, flowScheduler );
        jobs.put( description.name(), job );

        LOG.info( format( "Scheduled job [%s]", description.name() ) );

        trigger.start();
    }

    public void reschedule( final JobDescription description )
        throws Exception
    {
        // VALIDATE
        final Job exitingJob = jobs.get( description.name() );
        if( exitingJob == null )
        {
            throw new Exception( format( "Rescheduled job [%s] could not be found", description.name() ) );
        }
        exitingJob.trigger.stop();

        final TriggerFactory triggerFactory = triggerFactoryRegistry.get( description.triggerType() );
        if( triggerFactory == null )
        {
            throw new Exception( format( "Could not find a trigger factory of type [%s]", description.triggerType() ) );
        }

        final FlowScheduler flowScheduler = new FlowScheduler(
            description, exitingJob.flowScheduler.getExecutionContext()
        );

        final Trigger trigger = triggerFactory.create(
            description.triggerConfiguration(),
            flowScheduler
        );

        final Job job = new Job( description, trigger, flowScheduler );
        jobs.put( description.name(), job );

        LOG.info( format( "Rescheduled job [%s]", description.name() ) );

        trigger.start();
    }

    public void unschedule( final JobName jobName )
        throws Exception
    {
        final Job job = jobs.remove( jobName );
        if( job != null )
        {
            job.trigger.stop();
        }
        LOG.info( format( "Unscheduled job named [%s]", jobName ) );
    }

    public void list()
    {
        if( jobs.isEmpty() )
        {
            System.out.println( "There are no scheduled jobs" );
            return;
        }

        System.out.println( format( " %4.4s   %-40.40s   %-80.80s", "Id", "Flow type", "Trigger" ) );

        for( Job job : new TreeSet<Job>( jobs.values() ) )
        {
            System.out.println(
                format(
                    "[%4.4s] [%-40.40s] [%-80.80s]",
                    job.id,
                    job.jobDescription.flowType(),
                    job.trigger
                )
            );
        }
    }

    public void detail()
    {
        if( jobs.isEmpty() )
        {
            System.out.println( "There are no scheduled jobs" );
            return;
        }

        boolean first = true;

        for( Job entry : new TreeSet<Job>( jobs.values() ) )
        {
            if( !first )
            {
                System.out.println();
            }
            first = false;
            detail( entry.id );
        }
    }

    public void detail( final int id )
    {
        Job job = null;

        for( Job entry : jobs.values() )
        {
            if( entry.id == id )
            {
                job = entry;
                break;
            }
        }

        if( job == null )
        {
            System.out.println( format( "There is no job with id [%s]", id ) );
        }
        else
        {
            System.out.println( format( "%15s : %s", "Id", job.id ) );
            System.out.println( format( "%15s : %s", "Flow type", job.jobDescription.flowType() ) );
            System.out.println( format( "%15s : %s", "Trigger type", job.jobDescription.triggerType() ) );
            System.out.println( format( "%15s : %s", "Trigger", job.trigger ) );
            System.out.println( format( "%15s : %s", "Job name", job.jobDescription.name() ) );
        }
    }

    public void runNow( final int id )
    {
        Job job = null;

        for( Job entry : jobs.values() )
        {
            if( entry.id == id )
            {
                job = entry;
                break;
            }
        }

        if( job == null )
        {
            System.out.println( format( "There is no job with id [%s]", id ) );
        }
        else if( !( job.trigger instanceof RunNow ) )
        {
            System.out.println( "Specified job does not support 'runNow'" );
        }
        else
        {
            ( (RunNow) job.trigger ).fire();
        }
    }

    public static Map<String, ?> attributes()
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();

        attributes.put( "osgi.command.scope", "flow" );
        attributes.put( "osgi.command.function", new String[]{ "start", "stop", "list", "detail", "runNow" } );

        return attributes;
    }

    private class FlowScheduler
        implements ExecutionTarget
    {

        private final JobDescription description;
        private final Lock lock;

        final PersistentExecutionContext executionContext;

        public FlowScheduler( final JobDescription description )
        {
            this( description, new PersistentExecutionContext() );
        }

        public FlowScheduler( final JobDescription description,
                              final PersistentExecutionContext executionContext )
        {
            this.description = description;
            this.lock = new ReentrantLock( true );
            this.executionContext = executionContext;
        }

        public void execute( final ExecutionContext executionContext )
        {
            // wait till a previous invocation for this job (trigger/flow) combination finishes
            lock.lock();
            try
            {
                final Future<Object> future = executorService.submit(
                    new Callable<Object>()
                    {
                        public Object call()
                        {
                            try
                            {
                                LOG.debug( format( "Starting flow of type [%s]", description.flowType() ) );
                                final FlowFactory flowFactory = flowFactoryRegistry.get( description.flowType() );
                                if( flowFactory == null )
                                {
                                    LOG.warn(
                                        format( "Could not find a flow factory of type [%s]", description.flowType() )
                                    );
                                }
                                else
                                {
                                    final Flow flow = flowFactory.create( description.flowConfiguration() );
                                    FlowScheduler.this.executionContext.useTransientContext( executionContext );
                                    flow.execute( FlowScheduler.this.executionContext );
                                    LOG.debug( format( "Finished execution of [%s]", flow ) );
                                }
                            }
                            catch( Throwable ignore )
                            {
                                LOG.warn(
                                    format( "Execution of [%s] could not be completed.", description.flowType() ),
                                    ignore
                                );
                            }
                            return null;
                        }
                    }
                );
                future.get();
            }
            catch( Exception ignore )
            {
                // ignore
            }
            finally
            {
                lock.unlock();
            }
        }

        public PersistentExecutionContext getExecutionContext()
        {
            return executionContext;
        }

    }

    private static class Job
        implements Comparable<Job>
    {

        final int id;
        final JobDescription jobDescription;
        final Trigger trigger;
        final FlowScheduler flowScheduler;

        private static int counter;

        public Job( final JobDescription jobDescription,
                    final Trigger trigger,
                    final FlowScheduler flowScheduler )
        {
            this.id = ++counter;
            this.jobDescription = jobDescription;
            this.trigger = trigger;
            this.flowScheduler = flowScheduler;
        }

        @Override
        public String toString()
        {
            return format( "Execute [%s] when [%s]", jobDescription.flowType(), trigger );
        }

        public int compareTo( final Job other )
        {
            return Integer.valueOf( id ).compareTo( other.id );
        }

    }

}
