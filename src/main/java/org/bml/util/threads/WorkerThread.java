package org.bml.util.threads;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2014 Brian M. Lima
 * %%
 * This file is part of ORG.BML.
 *     ORG.BML is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *     ORG.BML is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with ORG.BML.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public WorkerThread(final ThreadGroup theThreadGroup, final String theThreadName) {
        super(theThreadGroup, theThreadName);
    }
    /**
     * Most classes use a static Log. Because this is a base class its log is
     * only added here for bare implementations. Leaf classes should have a
     * standard private static Log LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(WorkerThread.class);
    /**
     * Controls if a thread is capable of starting or if it should keep running.
     */
    private boolean shouldRun = false;
    /**
     * If true the thread will attempt to track the elapsed time for each run
     * cycle using the DescriptiveStatistics object if it is not null.
     */
    private boolean trackInstanceCycles = false;

    /**
     * Commons Math based statistics object for cycle telemetry tracking.
     */
    private DescriptiveStatistics theDescriptiveStatistics = null;
    /**
     * Helps track application behavior and provide a way to avoid all workers
     * entering into the DB at once. IE: starving the incoming queue. Also
     * enables fine grained telemetry monitoring. Eventually this should be
     * moved out to a new class called StateTracker and StateMonitor
     */
    private WORKER_STATE theState = WORKER_STATE.STOPPED;
    /**
     * The last time this threads state changed.
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
        return this.theState;
    }

    /**
     * Getter for a Date object representing the date/time this worker last
     * changed states.
     *
     * @return Date the date/time this worker last changed states.
     */
    public Date getLastStateChangeDate() {
        return (Date) this.lastStateChange.clone();
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
     * @param aState the state this worker thread is entering into.
     */
    public void setWorkerState(final WORKER_STATE aState) {
        this.theState = aState;
        this.lastStateWatch.reset();
        this.lastStateWatch.start();
        this.lastStateChange = new Date();
    }

    /**
     * Defines possible worker states.
     *
     * @TODO Pull this Enum out of the base class or figure out a better way to manage state.
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
         * Indicates a worker thread that is polling. Commonly used in
         * blocking IO.
         */
        POLLING,
        /**
         * Indicates a worker thread that is acquiring a permit. This is common
         * in systems with rate limits.
         */
        ACQUIRING_PERMIT,
        /**
         * Indicates a worker thread that is acquiring a connection to some
         * outside API such as a JDBC or URL connection.
         */
        ACQUIRING_CONNECTION,
        /**
         * Indicates a worker thread that is configuring a connection. This state
         * exists as some configurations depend on the underlying database and can
         * stall or fail.
         */
        CONFIGURING_CONNECTION,
        /**
         * Indicates a worker thread that is waiting on a pull operation from a
         * queue or stack like structure.
         */
        PULLING,
        /**
         * Indicates a worker thread that is sleeping.
         */
        WAITING,
        /**
         * Indicates a worker thread that is waiting on a push operation. The
         * equivalent of an offer on a queue.
         */
        PUSHING,
        /**
         * Used for generic entrance states where a thread is transitioning to
         * another state.
         */
        ENTERING,
        /**
         * Indicates a worker thread that is in the process of building up a
         * batch. NOTE: This state should be very short lived and always
         * followed by an EXECUTE_BATCH state. Long periods of batch building
         * can result in loss of data in hard power loss and or other
         * catastrophic system failures. As a result limiting batching time or
         * better yet make batch sizes a moving numeric based on traffic and the
         * response time of the recording system.
         */
        BATCHING,
        /**
         * Indicates a worker thread that is executing a batch operation such as
         * a bulk write to a database or flushing a batch amount of data to
         * disk.
         */
        EXECUTING_BATCH,
        /**
         * Indicates a {@link WorkerThread} that has executed a batch operation such as
         * a bulk write to a database or flushing a batch amount of data to
         * disk and is currently in the process of confirming the batch
         * operation completed without error.
         */
        VERIFYING_BATCH,
        /**
         * Indicates a worker thread that is in the process of or waiting for a
         * commit operation. This is most commonly found when committing bulk
         * inserts to databases.
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
         * you are defensively programming at every step as failure at this level
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
         * Indicates a {@link WorkerThread} that is in the process of preparing
         * an SQL statement usually via {@link java.sql.Connection#prepareStatement(java.lang.String)}.
         * Extentions or implementations of {@link WorkerThreadStateWatcher}
         * should treat this state as short lived, IE: Not exceeding a few
         * hundred milliseconds. A longer period in this state indicates an issue
         * with either the SQL and or a thread that is not correctly managing
         * it's own state.
         */
        PREPARING_SQL
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
        long startTime, endTime;
        /**
         * check shoudlRun. We commit to a full run so overrides of doIt are
         * expected to operate as an atomic transaction.
         */
        while (shouldRun) {
            startTime = System.currentTimeMillis(); //TRACK TELEMETRY
            doIt(); //EXECUTE
            endTime = System.currentTimeMillis(); //TRACK TELEMETRY
            if (trackInstanceCycles) {
                //Be definsive. NOTE: This should probubly be encapsulated or
                //initialization should be revisited to ensure these checks are
                //not necessary.
                if (theDescriptiveStatistics == null) {
                    this.trackInstanceCycles = Boolean.FALSE;
                    String tmpWarning = this.getLogPrefix() + "WorkerThread object Cycle Telemtry Tracking sub-system is misconfigured. I am taking action and halting Cycle Telemtry Tracking for THREAD #";
                    if (LOG != null) {
                        LOG.warn(tmpWarning);
                    } else {
                        System.out.println(tmpWarning); //Last ditch effort log.
                    }
                    continue; //skip to next loop cycle.
                }
                theDescriptiveStatistics.addValue(endTime - startTime); //Log telemetry in stats object.
            }
        }
        doShutdown(); //call shutdown handler.
    }

    /**
     * The actual run operation. this is called continuously by the
     * WorkerThread.run method until shouldRun is false.
     */
    protected abstract void doIt();

    /**
     * Override me for custom shutdown.
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
    public void setShouldRun(final boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    /**
     * Override for workers that use storage if you want to force a flush to
     * storage. This is provisioned here so monitors and handlers can use the
     * WorkerThead in pipelines where the operations are not specific to the
     * implementation class. This should be implemented as a logical equivalent
     * of {@link java.io.OutputStream#flush()}.
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
     * Setter for track instance cycles to avoid null pointer exceptions from run when under very high
     * load. <b>WARNING</b> This method synchronizes on trackInstanceCycles.
     *
     * @param trackInstanceCycles the trackInstanceCycles to set.
     */
    public void setTrackInstanceCycles(final boolean trackInstanceCycles) {
        if (trackInstanceCycles) {
            if (!this.trackInstanceCycles) {
                this.setTheDescriptiveStatistics(new DescriptiveStatistics());
            }
        } else {
            /**
             * Because we are looking for speed, we use a DescriptiveStatistics
             * with an empty addValue method so no logic needs to be executed
             * during run loops to synchronize and avoid null pointers.
             */
            this.trackInstanceCycles = Boolean.FALSE;
            this.setTheDescriptiveStatistics(new EMPTYDescriptiveStatistics());
        }
    }

    /**
     * Getter for the {@link DescriptiveStatistics} object used to track this {@link WorkerThread}.
     *
     * @return the theStatisticalSummary or null if tracking is disabled.
     */
    public DescriptiveStatistics getTheDescriptiveStatistics() {
        if (!trackInstanceCycles) {
            return null;
        }
        return theDescriptiveStatistics;
    }

    /**
     * Allow classes to set the {@link DescriptiveStatistics} this {@link WorkerThread} uses to hold telemetry.
     *
     * @param theDescriptiveStatistics a DescriptiveStatistics object used to log telemetry.
     */
    public void setTheDescriptiveStatistics(final DescriptiveStatistics theDescriptiveStatistics) {
        if (theDescriptiveStatistics == null) {
            this.trackInstanceCycles = Boolean.FALSE;
            this.theDescriptiveStatistics = null;
            return;
        }
        this.theDescriptiveStatistics = theDescriptiveStatistics;
        trackInstanceCycles = Boolean.TRUE;
    }

    /**
     * Prefix prepended to all logging messages.
     */
    private String logPrefix = null;
    /**
     * Name of this specific object for logging & telemetry purposes.
     */
    private String logName = null;

    /**
     * Getter for this objects logging prefix.
     *
     * @return {@link String} containing the prefix for any logging messages from this {@link WorkerThread} instance
     */
    public String getLogPrefix() {
        if (logPrefix == null) {
            logPrefix = new StringBuilder().append("LOG_NAME=").append(logName).append(" THREAD_ID=").append(this.getId()).toString();
        }
        return logPrefix;
    }

    /**
     * Getter for this objects log name.
     *
     * @return the logName.
     */
    public String getLogName() {
        return logName;
    }

    /**
     * Setter for this objects log name.
     *
     * @param logName the logName to set
     */
    public void setLogName(final String logName) {
        this.logName = logName;
    }

    /**
     * Empty class for pass through telemetry. This is just a DescriptiveStatistics object that drops all added data on the floor.
     */
    private static final class EMPTYDescriptiveStatistics extends DescriptiveStatistics {

        /**
         * Version UID required. NEVER USE!
         */
        static final long serialVersionUID = 42L;

        /**
         * Creates a new EMPTY_DescriptiveStatistics.
         */
        EMPTYDescriptiveStatistics() {
        }

        @Override
        public void addValue(final double v) {
        }
    }

}
