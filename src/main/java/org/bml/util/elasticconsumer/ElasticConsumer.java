package org.bml.util.elasticconsumer;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2014 Brian M. Lima
 * %%
 * This file is part of ORG.BML.
 * 
 *     ORG.BML is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     ORG.BML is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with ORG.BML.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;

import org.bml.util.threads.WorkerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DESCRIPTION: The ElasticConsumer is designed to as an extendable component
 * that allows a process to be threaded out but have either a widely varying
 * processing time and or want to be able to tune resource allocation at
 * runtime. It is a generic implementation of a scaling consumer end of a producer
 * consumer pattern
 *
 * This particular version latches onto a BlockingQueue and continues working.
 *
 * @author Brian M. Lima
 * @param <D> The Data container class for data that is to be consumed. This is
 * the result of a producer in a producer consumer pattern.
 * @param <W> The extension of {@link WorkerThread} that is the Consumer in this
 * producer consumer pattern.
 */
public class ElasticConsumer<D, W extends WorkerThread> extends WorkerThread {

    /**
     * I have to figure out what the correct pattern for logging is. 1. Static
     * log that uses the classes log name and force a log name to be set at
     * construction. perhaps even throwing an error if there are duplicates 2.
     * Set a default instance scope log and allow for external controllers to
     * change the logger.
     *
     * Either way there should be a default logger and a check method to setup
     * debug level logging to the console if no other setup is provided.
     */
    private Logger log = LoggerFactory.getLogger(ElasticConsumer.class);

    /**
     * I have to figure out what the correct pattern for logging is. 1. Static
     * log that uses the classes log name and force a log name to be set at
     * construction. perhaps even throwing an error if there are duplicates 2.
     * Set a default instance scope log and allow for external controllers to
     * change the logger.
     *
     * Either way there should be a default logger and a check method to setup
     * debug level logging to the console if no other setup is provided.
     *
     * @return the log
     */
    public synchronized Logger getLog() {
        return log;
    }

    /**
     * I have to figure out what the correct pattern for logging is. 1. Static
     * log that uses the classes log name and force a log name to be set at
     * construction. perhaps even throwing an error if there are duplicates 2.
     * Set a default instance scope log and allow for external controllers to
     * change the logger.
     *
     * Either way there should be a default logger and a check method to setup
     * debug level logging to the console if no other setup is provided.
     *
     * @param log the log to set
     */
    public synchronized void setLog(final Logger log) {
        this.log = log;
    }

    /**
     * Should track and print debugging data.
     */
    private boolean debug = true;

    /**
     * Debug Check.
     *
     * @return True if object is in debug mode, false otherwise.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Sets the debug state of this object. NOTE: in the future this will use
     * Commons logging levels for varying levels of vebosity.
     *
     * @param debug true if debug should be set to on , false otherwise.
     */
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    /**
     * The reporting interval.
     *
     * @return the reportInterval
     */
    public long getReportInterval() {
        return reportInterval;
    }

    /**
     * The reporting interval.
     *
     * @param reportInterval the reportInterval to set
     */
    public void setReportInterval(final long reportInterval) {
        this.reportInterval = reportInterval;
    }

    /**
     * Enumeration for the base report key metrics the ElasticConsumer reports
     * on.
     */
    public static enum REPORT_KEYS {

        /**
         * The key for the number of dead worker threads.
         */
        REPORT_MAP_KEY_DEAD("REPORT_MAP_KEY_DEAD"),
        /**
         * The key for the number of alive worker threads.
         */
        REPORT_MAP_KEY_ALIVE("REPORT_MAP_KEY_ALIVE"),
        /**
         * The key for the number of worker threads that have shouldRun set to
         * true.
         */
        REPORT_MAP_KEY_SHOULD_RUN("REPORT_MAP_KEY_SHOULD_RUN"),
        /**
         * The key for the number of worker threads that have shouldRun set to
         * false.
         */
        REPORT_MAP_KEY_SHOULD_NOT_RUN("REPORT_MAP_KEY_SHOULD_NOT_RUN");

        /**
         * the String representation for a REPORT_KEYS enum.
         */
        private final String stringValue;

        /**
         *
         * @param stringValue the String representation for a REPORT_KEYS enum.
         */
        REPORT_KEYS(final String stringValue) {
            this.stringValue = stringValue;
        }

        /**
         * Getter for the String representation of a REPORT_KEYS enum.
         *
         * @return String representation of a REPORT_KEYS enum.
         */
        public String getStringValue() {
            return stringValue;
        }
    }
    /**
     * The Set of WorkerThread implementations operating on behalf of this
     * ElasticConsumer.
     */
    protected Set<WorkerThread> workers = null;

    /**
     * The input queue.
     */
    private BlockingQueue queueIn = null;

    /**
     * The worker thread object factory.
     */
    private PooledObjectFactory<W> threadFactory = null;

    /**
     * The number of workers this consumer should have.
     */
    private int numWorkers = 0;
    /**
     * Should we maintain the number of workers.
     */
    private boolean maintainNumWorkers = false;

    /**
     * The default reporting interval.
     */
    private static final long DEFAULT_REPORT_INTERVAL = 10000L;

    /**
     * The reporting interval.
     */
    private long reportInterval = DEFAULT_REPORT_INTERVAL;

    /**
     * The Date the last flush call was made.
     */
    private Date lastFlushCallDate = null;

    @Override
    public synchronized void start() {
        Logger aLog = this.getLog();
        if (this.isAlive()) {
            IllegalThreadStateException ex = new IllegalThreadStateException(String.format("ElasticConsumer: %s Is already alive", getLogPrefix()));
            aLog.warn("{} An attempt was made to start an ElasticConsumer that is already alive.", getLogPrefix(), ex);
            throw ex;
        }
        if (numWorkers > 0 && this.queueIn != null && this.threadFactory != null) {
            this.setShouldRun(true);
            final int tmpNumWorkers = numWorkers;
            for (int c = 0; c < tmpNumWorkers; c++) {
                this.addWorkerThread();
            }
        } else {
            this.setShouldRun(true);
        }
        if (this.getShouldRun()) {
            aLog.info("{} MSG='SUCCESS: Passed configuration check.'", getLogPrefix());
            super.start();
        } else {
            aLog.error("{} MSG='FAILURE: Not configured correctly. FAILING SAFE.'", getLogPrefix());
            doShutdown();
        }
    }

    @Override
    public void doIt() {
        Logger aLog = this.getLog();
        while (this.getShouldRun()) {
            if (aLog.isInfoEnabled()) {
                this.logWorkerProfileMetricsBrief();
            }
            try {
                sleep(reportInterval);
            } catch (InterruptedException ex) {
                if (aLog.isWarnEnabled()) {
                    aLog.warn("{} InterruptedException caught: Attempting soft shutdown.", getLogPrefix());
                }
                this.softShutdown();
            }
        }
    }

    /**
     * The default number of milliseconds to wait in between checks to see if
     * all threads have shutdown during a system shutdown.
     */
    private static final long DEFAULT_SHUTDOWN_THREAD_SLEEP = 500L;

    @Override
    public synchronized void doShutdown() {
        final String myLogPrefix = getLogPrefix();
        Logger aLog = this.getLog();
        if (aLog.isInfoEnabled()) {
            aLog.info("{} MSG=Setting Maintain Workers to false.", myLogPrefix);
        }
        this.maintainNumWorkers = false;
        if (aLog.isInfoEnabled()) {
            aLog.info("{} MSG=Calling softShutDown.", myLogPrefix);
        }
        this.softShutdown();
        if (aLog.isInfoEnabled()) {
            aLog.info("{} MSG=Waiting for worker threads to die.", myLogPrefix);
        }
        int numThreads = 0, deadThreads = 0;
        if (this.workers != null && !this.workers.isEmpty()) {
            numThreads = this.workers.size();
            deadThreads = this.getWorkerProfileMetrics().get(REPORT_KEYS.REPORT_MAP_KEY_DEAD);
        }
        aLog.info("{} MSG=Increasing report speed.", myLogPrefix);
        while (numThreads != deadThreads) {
            deadThreads = this.getWorkerProfileMetrics().get(REPORT_KEYS.REPORT_MAP_KEY_DEAD);
            try {
                sleep(DEFAULT_SHUTDOWN_THREAD_SLEEP);
            } catch (InterruptedException ex) {
                aLog.info("{} InterruptedException caught while waiting for worker threads to die.", ex);
            }
        }
        aLog.info("{} MSG={} Worker Threads Shutdowm.", getLogPrefix(), deadThreads);
        this.logWorkerProfileMetricsBrief();
        setShouldRun(false);
    }

    /**
     * Creates a new instance of ElasticConsumer.
     *
     * @param factory The factory that manufactures new worker threads when
     * necessary. Implements {@link PoolableObjectFactory} for convience.
     * @param queueIn This is the queue that bridges the producers to this
     * particular consumer implementation.
     * @param numWorkers the original number of worker(consumer) processing
     * threads this object should start with.
     * @param maintainNumWorkers This controls whether a new worker should be
     * created to replace a worker that has suffered a catastrophic failure. In
     * the future this may also stop an out of control elastic controller
     * application from de-allocating too many resources and choking the data
     * pipeline.
     */
    public ElasticConsumer(final PooledObjectFactory<W> factory, final BlockingQueue<D> queueIn, final int numWorkers, final boolean maintainNumWorkers) {
        super();
        this.threadFactory = factory;
        this.queueIn = queueIn;
        this.numWorkers = numWorkers;
        this.maintainNumWorkers = maintainNumWorkers;
    }

    /**
     * Offer an object to be processed to the processing queue.
     *
     * @param theObject The object to be processed.
     * @param theTimeout The max wait time for successful offer.
     * @param theTimeUnit The TimeUnit the argument theTimeout is in.
     * @return boolean true on success false otherwise
     * @throws java.lang.InterruptedException if this thread is interrupted
     * while attempting the offer.
     * @throws IllegalArgumentException If theObject is null, theTimeout is less
     * than 1, or theTimeUnit is null.
     */
    public boolean offer(final D theObject, final long theTimeout, final TimeUnit theTimeUnit) throws InterruptedException, IllegalArgumentException {
        if (theObject == null) {
            throw new IllegalArgumentException("Can not offer a null object.");
        }
        if (theTimeout < 1) {
            throw new IllegalArgumentException("Can not offer an object with a timeout less than 1.");
        }
        if (theTimeUnit == null) {
            throw new IllegalArgumentException("Can not offer an object with a null TimeUnit.");
        }
        Logger aLog = this.getLog();

        if (aLog.isDebugEnabled()) {
            StopWatch watch = new StopWatch();
            watch.start();
            boolean result = doOffer(theObject, theTimeout, theTimeUnit);
            watch.stop();
            aLog.debug("{} DEBUG: OFFER result={} timeout={} time unit {} actual time in mills={}", getLogPrefix(), result, theTimeout, theTimeUnit, watch.getTime());
            return result;
        }
        return doOffer(theObject, theTimeout, theTimeUnit);
    }

    /**
     * Performs the offer. does not handle nulls or bad arguments.
     *
     * @param theObject T The object to be offered to the queue.
     * @param theTimeout long denoting the number of time units to use during the
     * blocking offer call.
     * @param theTimeUnit TimeUnit object telling the offer call what unit of time it
     * should block for if necessary.
     * @return true on success false otherwise.
     * @throws InterruptedException if hard shutdown has been initiated.
     */
    private boolean doOffer(final D theObject, final long theTimeout, final TimeUnit theTimeUnit) throws InterruptedException {
        return queueIn.offer(theObject, theTimeout, theTimeUnit);
    }

    /**
     * You can override this for thread configuration if you do not want to do
     * it in the {@link PoolableObjectFactory}.
     *
     * @return a WorkerThread ready to be started.
     */
    protected WorkerThread makeWorkerThread() {
        Logger aLog = this.getLog();
        if (aLog.isInfoEnabled()) {
            aLog.info(getLogPrefix() + " MSG='Creating WorkerThread'");
        }
        try {
            PooledObject<W> pooledObject = threadFactory.makeObject();
            W object = pooledObject.getObject();
            return (WorkerThread) object;
        } catch (Exception ex) {
            aLog.error("ElasticConsumer {} Unable to operate. WorkerThread factory is throwing Exceptions on makeObject.", getLogPrefix(), ex);
        }
        return null;
    }

    /**
     * Shut down ElasticConsumer workers using interrupt.
     */
    public synchronized void hardShutdown() {
        Logger aLog = this.getLog();
        aLog.warn("{} hardShutdown() CALLED: IMMINENT DATA LOSS: This method is for hard unloading in environments where the ElasticConsumer is in a locaked error state and the environment can not be restarted.", getLogPrefix());
        softShutdown();
        aLog.info("{} IMMINENT DATA LOSS: Manually interrupting worker threads.", getLogPrefix());

        for (WorkerThread thread : workers) {
            thread.interrupt();
        }
        aLog.info("{} IMMINENT DATA LOSS: Manually interrupting ElasticConsumer thread.", getLogPrefix());
        this.interrupt();
    }

    /**
     * Shut down ElasticConsumer workers using built in soft shutdown. WARNING!
     * This method does not block and does not stop the super class
     */
    public synchronized void softShutdown() {
        Logger aLog = this.getLog();
        aLog.info(" softShutdown() CALLED", getLogPrefix());
        //Stop uninitialized ElasticConsumer from throwing null pointer exception.
        if (workers == null || workers.isEmpty()) {
            aLog.warn("{} An attempt to call softShutdown() on an un-started instance of ElasticConsumer was made.", getLogPrefix());
            return;
        }
        for (WorkerThread thread : workers) {
            thread.setShouldRun(false);
        }
    }

    /**
     * Allows a controller to increase the number of worker threads.
     *
     * @return true on success, false on error.
     */
    public synchronized boolean addWorkerThread() {
        if (workers == null) {
            workers = new LinkedHashSet<WorkerThread>();
        }
        WorkerThread thread = makeWorkerThread();
        if (thread == null) {
            return false;
        }
        thread.setShouldRun(true);
        thread.start();
        workers.add(thread);
        this.numWorkers++;
        return true;
    }

    /**
     * Allows a controller to remove worker threads from the consumer pool in
     * order to conserve resources or regulate throughput.
     *
     * @param soft True if we should wait for a worker to finish before removal.
     * False if we should interrupt working threads.
     * @return True on success or if there are no workers left, false otherwise
     */
    public synchronized Boolean removeWorkerThread(final boolean soft) {
        if (workers == null || workers.isEmpty()) {
            return Boolean.FALSE;
        }
        WorkerThread thread;
        Iterator<WorkerThread> iter;
        iter = workers.iterator();
        while (iter.hasNext()) {
            thread = iter.next();
            if (thread.getShouldRun()) {
                thread.setShouldRun(false);
                if (!soft) {
                    thread.interrupt();
                    thread.flush();
                }
            }
        }
        return Boolean.TRUE;
    }

    /**
     * Logs the workers profile metrics in brief.
     */
    public synchronized void logWorkerProfileMetricsBrief() {
        if (log == null || !log.isInfoEnabled()) {
            return;
        }
        Map<REPORT_KEYS, Integer> map = getWorkerProfileMetrics();
        if (map == null) {
            return;
        }
        log.info(
                "{} ALIVE={} DEAD={} SHOULD_RUN={} SHOULD_NOT_RUN={}",
                getLogPrefix(),
                map.get(REPORT_KEYS.REPORT_MAP_KEY_ALIVE),
                map.get(REPORT_KEYS.REPORT_MAP_KEY_DEAD),
                map.get(REPORT_KEYS.REPORT_MAP_KEY_SHOULD_RUN),
                map.get(REPORT_KEYS.REPORT_MAP_KEY_SHOULD_NOT_RUN)
        );
    }

    /**
     * Retrieves the worker profile metrics.
     *
     * @return A map of reporting keys and values.
     */
    public synchronized Map<REPORT_KEYS, Integer> getWorkerProfileMetrics() {
        int deadSet = 0, aliveSet = 0, shouldRunSet = 0, shouldNotRunSet = 0;
        if (workers == null) {
            return null;
        }
        for (WorkerThread thread : workers) {
            if (thread.isAlive()) {
                aliveSet++;
            } else {
                deadSet++;
            }
            if (thread.getShouldRun()) {
                shouldRunSet++;
            } else {
                shouldNotRunSet++;
            }
        }

        Map<REPORT_KEYS, Integer> map = new EnumMap<REPORT_KEYS, Integer>(REPORT_KEYS.class);
        map.put(REPORT_KEYS.REPORT_MAP_KEY_DEAD, deadSet);
        map.put(REPORT_KEYS.REPORT_MAP_KEY_ALIVE, aliveSet);
        map.put(REPORT_KEYS.REPORT_MAP_KEY_SHOULD_RUN, shouldRunSet);
        map.put(REPORT_KEYS.REPORT_MAP_KEY_SHOULD_NOT_RUN, shouldNotRunSet);
        return map;
    }

    /**
     *
     * @return the accumulation of <code>WorkerThread.flush();</code> for the subjugate {@link WorkerThread} extensions.
     */
    @Override
    public synchronized int flush() {
        int c = 0;
        if (workers == null) {
            return c;
        }
        //Use try to ensure lock is always released;
        try {
            for (WorkerThread thread : workers) {
                c += thread.flush();
            }
        } catch (Exception e) {
            this.getLog().error("AN Exception was caught while flushing workers.", e);
        }
        setLastFlushCallDate(new Date());
        return c;
    }

    /**
     * Getter for the last date a flush call was made.
     *
     * @return the lastFlushCallDate
     */
    public synchronized Date getLastFlushCallDate() {
        return new Date(lastFlushCallDate.getTime());
    }

    /**
     * setter for the last date a flush call was made.
     *
     * @param lastFlushCallDate the lastFlushCallDate
     */
    public synchronized void setLastFlushCallDate(final Date lastFlushCallDate) {
        this.lastFlushCallDate = new Date(lastFlushCallDate.getTime());
    }

}
