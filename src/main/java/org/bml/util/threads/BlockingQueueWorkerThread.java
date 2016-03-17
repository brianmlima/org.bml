package org.bml.util.threads;

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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Extension of Worker Thread for encapsulating thread infrastructure
 * designed to specifically feed off of a {@link BlockingQueue}.This extension is
 * specifically designed to be the consumer in a producer consumer pattern.
 *
 * @author Brian M. Lima
 * @param <T> The object type that will be worked on. This is usually some type of data container or object that needs an operation to be performed.
 */
public abstract class BlockingQueueWorkerThread<T> extends WorkerThread {

    /**
     * Standard Logging. All logging should be funneled through this log.
     */
    private static final Logger LOG = LoggerFactory.getLogger(BlockingQueueWorkerThread.class);
    /**
     * Use to avoid calling .class.getName() in high throughput situations.
     */
    private static final String CLASS_NAME = BlockingQueueWorkerThread.class.getName().intern();
    /**
     * Use to avoid calling .class.getSimpleName() in high throughput situations.
     */
    private static final String SIMPLE_CLASS_NAME = BlockingQueueWorkerThread.class.getSimpleName().intern();
    /**
     * The input queue {@link BlockingQueue}.
     */
    private final BlockingQueue<T> theInputBlockingQueue;
    /**
     * The worker threads poll timeout.
     */
    private final long theTimeout;
    /**
     * The worker threads poll timeout TimeUnit.
     */
    private final TimeUnit unit = TimeUnit.MILLISECONDS;
    /**
     * The worker threads sleep time on an empty queue.
     */
    private final long theEmptyQueueWait;

    /**
     * The worker threads sleep time on an empty queue.
     */
    public static final long DEFAULT_EMPTY_QUEUE_WAIT = 1000L;

    /**
     * Creates a new instance of BlockingQueueWorkerThread.
     *
     * @param theInputBlockingQueue The BlockingQueue for worker threads to poll.
     * @param theTimeout The worker threads poll timeout in milliseconds.
     * @param theEmptyQueueWait The worker threads sleep time on an empty queue in milliseconds
     * queue.
     * @pre theInputBlockingQueue!=null
     * @pre theTimeout>0
     * @pre theTimeoutTimeUnit!=null
     * @pre theEmptyQueueWaitPeriod >0
     */
    public BlockingQueueWorkerThread(final BlockingQueue<T> theInputBlockingQueue, final long theTimeout, final long theEmptyQueueWait) {
        super();
        checkNotNull(theInputBlockingQueue, "Can not create a %s with a null theInputBlockingQueue parameter.", SIMPLE_CLASS_NAME);
        checkArgument((theTimeout > 0), "Can not create a %s  with a theTimeout parameter that does not meet (theTimeout > 0).", SIMPLE_CLASS_NAME);
        checkArgument((theEmptyQueueWait > 0), "Can not create a %s  with a theEmptyQueueWaitPeriod parameter that does not meet (theTimeout > 0).", SIMPLE_CLASS_NAME);
        this.theInputBlockingQueue = theInputBlockingQueue;
        this.theTimeout = theTimeout;
        this.theEmptyQueueWait = theEmptyQueueWait;
    }

    /**
     * The main run method substitute from WorkerThread. This method is called
     * repeatedly until its {@link Thread} is stopped or the base
     * {@link WorkerThread} is shutdown.
     *
     * This method handles the pulling of data off the queue and passes it to
     * <code>protected abstract void doIt(T obj);</code>
     * It handles common exceptions and shuts down gracefully.
     */
    @Override
    protected void doIt() {
        final T data;
        try {
            data = theInputBlockingQueue.poll(theTimeout, unit);
            if (data == null) {
                sleep(theEmptyQueueWait);
            } else {
                doIt(data);
            }
        } catch (InterruptedException ex) {
            LOG.warn("InterruptedException caught. Shutting down gracefully", ex);
            this.setShouldRun(false);
        } catch (OutOfMemoryError oome) {
            LOG.error("OutOfMemoryError caught. Generally unrecoverable. Shutting down gracefully", oome);
            this.setShouldRun(false);
        } catch (Exception ex) {
            LOG.error("Exception caught. It must have bubbled up from the doIt(data) implementation. Generally unrecoverable. Shutting down gracefully", ex);
            this.setShouldRun(false);
        }
    }

    /**
     * This is the operation method.
     *
     * @param obj T object to be acted upon.
     */
    protected abstract void doIt(final T obj);
}
