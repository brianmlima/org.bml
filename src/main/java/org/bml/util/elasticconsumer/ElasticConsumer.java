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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.bml.util.threads.WorkerThread;

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
    private Log log = LogFactory.getLog(ElasticConsumer.class);

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
    public Log getLog() {
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
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * Should track and print debugging data.
     */
    private boolean debug = true;

    /**
     * Debug Check
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
    public void setDebug(boolean debug) {
        this.debug = debug;
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

        private String stringValue;

        /**
         *
         * @param stringValue the String representation for a REPORT_KEYS enum.
         */
        REPORT_KEYS(String stringValue) {
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

    private BlockingQueue queueIn = null;
    private PoolableObjectFactory<W> threadFactory = null;
    private int numWorkers = 0;
    private boolean maintainNumWorkers = false;
    private long reportInterval = 10000;
    private Date lastFlushCallDate = null;

    @Override
    public synchronized void start() {
        if (this.isAlive()) {
            IllegalThreadStateException ex = new IllegalThreadStateException("ElasticConsumer: " + getLogPrefix() + " Is already alive");
            if (log.isWarnEnabled()) {
                log.warn(debug, null);
            }
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
            if (log.isInfoEnabled()) {
                log.info(getLogPrefix() + " MSG='SUCCESS: Passed configuration check.'");
            }
            super.start();
        } else {
            if (log.isFatalEnabled()) {
                log.fatal(getLogPrefix() + " MSG='FAILURE: Not configured correctly. FAILING SAFE.'");
            }
            doShutdown();
        }
    }

    @Override
    public void doIt() {
        StringBuilder builder = new StringBuilder();
        while (this.getShouldRun()) {
            if (log.isInfoEnabled()) {
                this.logWorkerProfileMetricsBrief();
            }
            try {
                sleep(reportInterval);
            } catch (InterruptedException ex) {
                if (log.isWarnEnabled()) {
                    log.warn(getLogPrefix() + " InterruptedException caught: Attempting soft shutdown.");
                }
                this.softShutdown();
            }
        }
    }

    @Override
    public synchronized void doShutdown() {
        long id = this.getId();
        String myLogPrefix = getLogPrefix();
        if (log.isInfoEnabled()) {
            log.info(myLogPrefix + " MSG=Setting Maintain Workers to false.");
        }
        this.maintainNumWorkers = false;
        if (log.isInfoEnabled()) {
            log.info(myLogPrefix + " MSG=Calling softShutDown.");
        }
        this.softShutdown();
        if (log.isInfoEnabled()) {
            log.info(myLogPrefix + " MSG=Waiting for worker threads to die.");
        }
        int numThreads = 0, deadThreads = 0;
        if (this.workers != null && !this.workers.isEmpty()) {
            numThreads = this.workers.size();
            deadThreads = this.getWorkerProfileMetrics().get(REPORT_KEYS.REPORT_MAP_KEY_DEAD);
        }
        if (log.isInfoEnabled()) {
            log.info(myLogPrefix + " MSG=Increasing report speed.");
        }
        while (numThreads != deadThreads) {
            deadThreads = this.getWorkerProfileMetrics().get(REPORT_KEYS.REPORT_MAP_KEY_DEAD);
            try {
                sleep(500);

            } catch (InterruptedException ex) {
                if (log.isWarnEnabled()) {
                    log.info(myLogPrefix + " InterruptedException caught while waiting for worker threads to die.", ex);
                }
            }
        }
        if (log.isInfoEnabled()) {
            log.info(getLogPrefix() + " MSG=" + deadThreads + " Worker Threads Shutdowm.");
        }
        if (log.isInfoEnabled()) {
            this.logWorkerProfileMetricsBrief();
        }
        setShouldRun(false);
    }

    /**
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
    public ElasticConsumer(PoolableObjectFactory<W> factory, BlockingQueue<D> queueIn, int numWorkers, boolean maintainNumWorkers) {
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

        if (log.isDebugEnabled()) {
            StopWatch watch = new StopWatch();
            watch.start();
            boolean result = doOffer(theObject, theTimeout, theTimeUnit);
            watch.stop();
            log.debug(getLogPrefix() + " DEBUG: OFFER result=" + result + " timeout=" + theTimeout + " time unit " + theTimeUnit + " actual time in mills=" + watch.getTime());
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
     * it in the {@link PoolableObjectFactory};
     *
     * @return a WorkerThread ready to be started.
     */
    protected WorkerThread makeWorkerThread() {
        if (log.isInfoEnabled()) {
            log.info(getLogPrefix() + " MSG='Creating WorkerThread'");
        }
        try {
            return threadFactory.makeObject();
        } catch (Exception ex) {
            if (log.isFatalEnabled()) {
                log.fatal("Unable to operate. WorkerThread factory is throwing Exceptions on makeObject");
            }
        }
        return null;
    }

    /**
     * Shut down ElasticConsumer workers using interrupt.
     */
    public synchronized void hardShutdown() {
        if (log.isWarnEnabled()) {
            log.info(getLogPrefix() + " hardShutdown() CALLED: IMMINENT DATA LOSS: This method is for hard unloading in environments where the ElasticConsumer is in a locaked error state and the environment can not be restarted.");
        }
        softShutdown();
        if (log.isWarnEnabled()) {
            log.info(getLogPrefix() + " IMMINENT DATA LOSS: Manually interrupting worker threads.");
        }

        for (WorkerThread thread : workers) {
            thread.interrupt();
        }
        if (log.isWarnEnabled()) {
            log.info(getLogPrefix() + " IMMINENT DATA LOSS: Manually interrupting ElasticConsumer thread.");
        }
        this.interrupt();
    }

    /**
     * Shut down ElasticConsumer workers using built in soft shutdown. WARNING!
     * This method does not block and does not stop the super class
     */
    public synchronized void softShutdown() {
        if (log.isInfoEnabled()) {
            log.info(getLogPrefix() + " softShutdown() CALLED");
        }
        //Stop uninitialized ElasticConsumer from throwing null pointer exception.
        if (workers == null || workers.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn(getLogPrefix() + " An attempt to call softShutdown() on an un-started instance of ElasticConsumer was made.");
            }
            return;
        }
        for (WorkerThread thread : workers) {
            thread.setShouldRun(false);
        }
    }

    /**
     * Allows a controller to increase the number of worker threads
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
     * @return True on success, false otherwise
     */
    public synchronized Boolean removeWorkerThread(boolean soft) {
        if (workers == null || workers.isEmpty()) {
            return null;
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
        return true;
    }

    public synchronized void logWorkerProfileMetricsBrief() {
        if (log == null || !log.isInfoEnabled()) {
            return;
        }
        Map<REPORT_KEYS, Integer> map = getWorkerProfileMetrics();
        if (map == null) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        buf.append(getLogPrefix())
                .append(" ALIVE=").append(map.get(REPORT_KEYS.REPORT_MAP_KEY_ALIVE)).
                append(" DEAD=").append(map.get(REPORT_KEYS.REPORT_MAP_KEY_DEAD)).
                append(" SHOULD_RUN=").append(map.get(REPORT_KEYS.REPORT_MAP_KEY_SHOULD_RUN)).
                append(" SHOULD_NOT_RUN=").append(map.get(REPORT_KEYS.REPORT_MAP_KEY_SHOULD_NOT_RUN));
        log.info(buf.toString());
    }

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

        Map<REPORT_KEYS, Integer> map = new LinkedHashMap<REPORT_KEYS, Integer>(4, 1.333f);
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
            //LOGGING
        }
        lastFlushCallDate = new Date();
        return c;
    }

    /**
     * @return the lastFlushCallDate
     */
    public Date getLastFlushCallDate() {
        return lastFlushCallDate;
    }
}
