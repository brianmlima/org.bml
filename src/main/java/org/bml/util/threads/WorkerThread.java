package org.bml.util.threads;

/*
 * #%L
 * orgbml
 * %%
 * Copyright (C) 2008 - 2013 Brian M. Lima
 * %%
 * This file is part of org.bml.
 * 
 * org.bml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.bml is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with org.bml.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * <p>
 * A Thread extension with graceful shutdown extensions and a runtime boolean
 * check of shouldRun to ensure proper initialization. These two extensions are
 * especially handy when implementing pools of threads that may or may not have
 * been initialized correctly by an
 * {@link org.apache.commons.pool.PoolableObjectFactory}.</p>
 * <p>
 * <b>NOTE:</b>This class does not implicitly require the use of a
 * {@link org.apache.commons.pool.PoolableObjectFactory} for creation, however
 * it is recommended that you use the pool/factory pattern where multiple
 * instances of a specific WorkerThread implementation are used.<p/>
 * <p>
 * The WorkerThread is also instrumented to operate with
 * {@link org.bml.util.threads.WorkerThreadStateWatcher WorkerThreadStateWatcher}
 * instances. Implementations should always use the thread state
 * instrumentation. Use of the built in thread state instrumentation allows easy
 * configuration and implementation of
 * {@link org.bml.util.alert.AlertTestExecutor AlertTestExecutor} and
 * {@link org.bml.util.rt.telemetry.RTTelemetrySink RTTelemetrySink} objects and
 * their supporting sub-systems.</p>
 *
 * <p>
 * Commons math <code>DescriptiveStatistics</code> based cycle telemetry and
 * extensions allowing thread state telemetry using the
 * <code>WORKER_STATE</code> Enum. NOTE: Implementations of this class must use
 * <code>WORKER_STATE</code> and the tracking methods at runtime for state to
 * change.
 * </p>
 *
 * <p>
 * <b>Note:</b> Be aware extensions of this class will not 'run' unless<br/>
 * <code>getShouldRun() == true</code> </p>
 *
 * @see org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
 * DescriptiveStatistics
 * @see org.apache.commons.math3.stat.descriptive.SummaryStatistics
 * SummaryStatistics
 * @see java.lang.Thread Thread
 * @see org.bml.util.elasticconsumer.ElasticConsumer ElasticConsumer
 *
 *
 * @author Brian M. Lima
 */
public abstract class WorkerThread extends Thread {

    /**
     * Empty constructor. This constructor may be depreciated in the next
     * release in order to force implementations to use the
     * <code>WorkerThread(ThreadGroup theThreadGroup, String theThreadName)</code>
     * constructor.
     */
    public WorkerThread() {
        super();
    }

    /**
     * The recommended constructor to use when creating an implementation. By
     * giving the thread a group and a name you make lookup and use by other
     * components possible without a direct reference.
     *
     * @param theThreadGroup The thread group to add this thread to.
     * @param theThreadName the name of the thread.
     */
    public WorkerThread(ThreadGroup theThreadGroup, String theThreadName) {
        super(theThreadGroup, theThreadName);
    }
    /**
     * Most classes use a static Log. Because this is a base class its log is
     * only added here for bare implementations. Leaf classes should have a
     * standard private static Log LOG.
     */
    private Log log = LogFactory.getLog(WorkerThread.class);
    /**
     * Controls if a thread is capable of starting or if it should keep running.
     */
    private boolean shouldRun = false;
    /**
     * If true the thread will attempt to track the elapsed time for each run
     * cycle using the DescriptiveStatistics object if it is not null;
     */
    private boolean trackInstanceCycles = false;
    /**
     * Commons Math based stats object for cycle telemetry tracking.
     */
    private DescriptiveStatistics theDescriptiveStatistics = null;
    /**
     * Helps track application behavior and provide a way to avoid all workers
     * entering into the DB at once. IE: starving the incoming queue. Also
     * enables fine grained telemetry monitoring. Eventually this should be
     * moved out to a new class called StateTracker and StateMonitor
     */
    private WORKER_STATE state = WORKER_STATE.STOPPED;
    /**
     * The last time this threads state changed
     */
    private Date lastStateChange = new Date();
    /**
     * The StopWatch used to track state telemetry over time.
     */
    private StopWatch lastStateWatch = new StopWatch();

    /**
     * Getter for the current state this worker is in.
     *
     * @return WORKER_STATE the current state this worker is in.
     */
    public WORKER_STATE getWorkerState() {
        return this.state;
    }

    /**
     * Getter for a Date object representing the date/time this worker last
     * changed states
     *
     * @return Date the date/time this worker last changed states.
     */
    public Date getLastStateChangeDate() {
        return this.lastStateChange;
    }

    /**
     * Getter for the number of seconds this worker has been in its current
     * state.
     *
     * @return long the number of seconds this worker has been in it's current
     * state.
     */
    public long getSecondsSinceLastStateChange() {
        return TimeUnit.SECONDS.convert(this.lastStateWatch.getTime(), TimeUnit.MILLISECONDS);
    }

    /**
     * Sets the WORKER_STATE this thread is moving into. This should always be
     * used as the timing between state changes allows the alert sub-system to
     * find threads that are stuck in a state and perform cleanup. This also
     * helps the telemetry sub-system gather data on current system state.
     *
     * @param state the state this worker thread is entering into.
     */
    public void setWorkerState(WORKER_STATE state) {
        this.state = state;
        this.lastStateWatch.stop();
        this.lastStateWatch.start();
        this.lastStateChange = new Date();
    }

    /**
     * Defines possible worker states. TODO: Pull this Enum out of the base
     * class.
     */
    public static enum WORKER_STATE {

        /**
         * Indicates a worker thread that is starting up.
         */
        STARTING,
        /**
         * Indicates a worker thread that is in the process of stopping.
         */
        STOPPING,
        /**
         * Indicates a worker thread that is stopped.
         */
        STOPPED,
        /**
         * Indicates a worker thread that is aquiring a connection to some
         * outside API such as a JDBC or URL connection.
         */
        AQUIRINGCONNECTION,
        /**
         * Indicates a worker thread that is waiting on a pull operation from a
         * queue or stack like structure
         */
        PULLING,
        /**
         * Indicates a worker thread that is sleeping.
         */
        WAITING,
        /**
         * Indicates a worker thread that is waiting on a push operation. The
         * equivilant of an offer on a queue
         */
        PUSHING,
        /**
         *
         */
        ENTERING,
        /**
         * Indicates a worker thread that is executing a batch operation such as
         * a bulk write to a database or flushing a batch amount of data to
         * disk.
         */
        EXECUTING_BATCH,
        /**
         * Indicates a worker thread that is in the process of or waiting for a
         * commit operation. This is most commonly found when committing bulk
         * inserts to databases
         *
         */
        COMMITTING,
        /**
         * Indicates a worker thread that is verifying a commit. This is usually
         * unnecessary however can be useful for diagnosing issues with
         * replication and or clustered systems that have fuzzy transactional
         * stability.
         */
        CHECKING_COMMIT,
        /**
         * Indicates a worker thread that is reporting an error. If this state
         * is seen often you should immediately examine the configuration of the
         * error system and most likely increase the number of workers.
         */
        OFFERINGERROR,
        /**
         * Indicates a worker thread that is in the process of failing.
         * Generally this means the thread has failed and is in the process of
         * reporting its failure to the error handling subsystem. NOTE:
         * generally workers should never fail. Failing workers is a direct
         * result of bugs in the worker implementation, take some time to ensure
         * you are defensivly programming at every step as failure at this level
         * can result in data loss and or service degradation as thread creation
         * is inherently expensive.
         */
        FAILING,
        /**
         * Indicates a worker thread that is flushing all data out. This returns
         * the worker to the state it was in when it was newly created. This
         * state should always be entered into before shutdown and or
         * replacement operations occur.
         */
        FLUSHING,
        /**
         * Indicates a worker thread that is in the process of building up a
         * batch. NOTE: This state should be very short lived and always
         * followed by an EXECUTE_BATCH state. Long periods of batch building
         * can result in loss of data in hard power loss and or other
         * catastrophic system failures. As a result limiting batching time or
         * better yet make batch sizes a moving numeric based on traffic and the
         * response time of the recording system.
         */
        BATCHING
    }

    /**
     * Gets the shouldRun value.
     *
     * @return shouldRun True if <code>setShouldRun(true); </code> has been
     * called. False otherwise.
     */
    public boolean getShouldRun() {
        return shouldRun;
    }

    /**
     * Overrides <code>Thread.run()</code> to implement
     * <code>getShouldRun()</code> and <code>doShutdown()</code> WorkerThread
     * extensions.
     */
    @Override
    public void run() {
        StopWatch watch = new StopWatch();
        /**
         * check shoudlRun. We commit to a full run so overrides of doIt are
         * expected to handle as an atomic transaction
         */
        while (shouldRun) {
            //TRACK INSTANCE TELEMETRY
            if (trackInstanceCycles) {
                watch.start();
            }
            //EXECUTE
            doIt();
            //TRACK INSTANCE TELEMETRY
            if (trackInstanceCycles) {
                watch.stop();
                //Be definsive. NOTE: This should probubly be encapsulated or
                //initialization should be revisited to ensure these checks are 
                //not necessary.
                if (theDescriptiveStatistics == null) {
                    this.trackInstanceCycles = Boolean.FALSE;
                    String tmpWarning = this.getLogPrefix() + "WorkerThread object Cycle Telemtry Tracking sub-system is misconfigured. I am taking action and halting Cycle Telemtry Tracking for THREAD #";
                    if (log != null) {
                        log.warn(tmpWarning);
                    } else {
                        System.out.println(tmpWarning);
                    }
                    continue;
                }
                theDescriptiveStatistics.addValue(watch.getTime());
            }
        }
        //call shutdown handler.
        doShutdown();
    }

    /**
     * The actual run operation. this is called continuously by the
     * WorkerThread.run method until shouldRun is false.
     */
    protected abstract void doIt();

    /**
     * Override me for special shutdown
     */
    protected void doShutdown() {
        System.out.println("WorkerThread " + this.getId() + " shutting down");
        setShouldRun(false);
    }

    /**
     * Sets the shouldRun control for the WorkerThread.
     *
     * @param shouldRun A boolean to set shouldRun to.
     */
    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    /**
     * Override for workers that use storage if you want to force a flush to
     * storage. This is provisioned here so monitors and handlers can use the
     * WorkerThead in pipelines where the operations are not specific to the
     * implementation class. This should be implemented as a logical equivalent
     * of {@link java.io.OutputStream#flush()}
     *
     *
     * @return the number of objects that were flushed. May throw an
     * UnsupportedOperationException if the implementation class does not
     * override correctly.
     */
    public synchronized int flush() {
        throw new UnsupportedOperationException("Base WorkerThread does not implement flush");
    }

    /**
     * Get the Boolean used to by this class to turn instance cycle telemetry on
     * and off.
     *
     * @return the trackCycles
     */
    public boolean getTrackInstanceCycles() {
        return trackInstanceCycles;
    }

    /**
     * Synched to avoid null pointer exceptions from run when under very high
     * load.
     *
     * @param trackInstanceCycles the trackInstanceCycles to set.
     */
    public synchronized void setTrackInstanceCycles(boolean trackInstanceCycles) {
        if (trackInstanceCycles == true) {
            if (this.trackInstanceCycles == false) {
                this.setTheDescriptiveStatistics(new DescriptiveStatistics());
            }
        } else {
            this.setTheDescriptiveStatistics(null);
            this.trackInstanceCycles = Boolean.FALSE;
        }
    }

    /**
     * @return the theStatisticalSummary
     */
    public DescriptiveStatistics getTheDescriptiveStatistics() {
        return theDescriptiveStatistics;
    }

    /**
     * @param theDescriptiveStatistics
     */
    public void setTheDescriptiveStatistics(DescriptiveStatistics theDescriptiveStatistics) {
        if (theDescriptiveStatistics == null) {
            this.trackInstanceCycles = Boolean.FALSE;
            this.theDescriptiveStatistics = null;
            return;
        }
        this.theDescriptiveStatistics = theDescriptiveStatistics;
        trackInstanceCycles = Boolean.TRUE;
    }

    /**
     */
    private String logPrefix = null, logName = null;

    /**
     * @return
     */
    public String getLogPrefix() {
        if (logPrefix == null) {
            logPrefix = new StringBuilder().append("LOG_NAME=").append(logName).append(" THREAD_ID=").append(this.getId()).toString();
        }
        return logPrefix;
    }

    /**
     * @return the logName
     */
    public String getLogName() {
        return logName;
    }

    /**
     * @param logName the logName to set
     */
    public void setLogName(String logName) {
        this.logName = logName;
    }
}
