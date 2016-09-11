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
package org.bml.util.rt.telemetry;

import com.google.common.util.concurrent.RateLimiter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.exception.OverloadedException;
import org.bml.util.exception.UnavailableException;
import org.bml.util.threads.WorkerThread;

/**
 * A telemetry sink for T data. All methods implement timeouts for
 * soft-real-time support. Because there is no way to implement true real time
 * operations this should always be thought of as a best case without true real
 * time support. In this the RT classes use timeouts on all operations in order
 * to add time as a parameter for method success.
 *
 * The class uses a LinkedBlockingQueue for managing buffer capacity and an
 * attempt at order preservation however order is not absolute as worker
 * threads pulling from the queue and writing to whatever the telemetry consumer
 * is may bulk write out of order.
 *
 * NOTE: Because this class extends WorkerThread it is necessary to distinguish
 * between enabled and should run. Enabled controls whether this sink will take
 * on new offers and can be toggled. ShouldRun controls the sink subsystems and
 * setting should run to false flushes and shuts the sink and it's sub-systems
 * down permanently.
 *
 * @author Brian M. Lima
 * @param <T> The type of the telemetry object this sink will accept.
 */
public abstract class RTTelemetrySink<T> extends WorkerThread {

    private Log LOG = null;

    /**
     */
    private Double permitsPerSecond;
    /**
     */
    private final int bufferCapacity;
    /**
     */
    private final RateLimiter theRateLimiter;
    /**
     */
    private final boolean useRateLimiter;
    /**
     */
    protected final LinkedBlockingQueue<T> telemetryBuffer;
    /**
     */
    private final AtomicBoolean isEnabled;

    /**
     * Reason for shutdown info
     */
    private String reasonForShutdown = null;
    /**
     * This is set if there has been an internal issue and this sink can no
     * longer be used
     */
    private UnavailableException theUnavailableException = null;

    /**
     * Creates a new RTTelemetrySink.
     *
     * @param permitsPerSecond number of offers per second this sink will take
     * before throwing OverloadedException. Set to 0 to disable rate limiting.
     * @param theThreadGroup The thread group to add this thread to.
     * @param theThreadName the name of the thread.
     * @param bufferCapacity sets the capacity of the telemetry object buffer.
     * @param isEnabled If passed as True. This sink will be enabled but not
     * started available on construction. If passed False this sink will be
     * disabled by default.
     *
     */
    public RTTelemetrySink(
            final ThreadGroup theThreadGroup,
            final String theThreadName,
            final Double permitsPerSecond,
            final int bufferCapacity,
            final boolean isEnabled
    ) {
        super(theThreadGroup, theThreadName);
        this.LOG = LogFactory.getLog(getClass());
        this.permitsPerSecond = permitsPerSecond;
        if (permitsPerSecond <= 0) {
            useRateLimiter = false;
            this.theRateLimiter = null;
        } else {
            useRateLimiter = true;
            this.theRateLimiter = RateLimiter.create(permitsPerSecond);
        }
        this.bufferCapacity = bufferCapacity;
        this.telemetryBuffer = new LinkedBlockingQueue<T>(bufferCapacity);
        this.isEnabled = new AtomicBoolean(isEnabled);
        this.setShouldRun(isEnabled);
    }

    /**
     * Standard offer call permit and offer are timed separately.
     *
     * @param telemetry The telemetry data object to be recorded.
     * @param permitTimeout The maximum milliseconds to wait for a permit to offer.
     * Permits provide the rate limiting service.
     * @param offerTimeout The maximum milliseconds to spend in the offer operation
     * @return True if the telemetry was offered. False otherwise.
     * @throws UnavailableException If this sink is unavailable.
     * @throws OverloadedException If the telemetryBuffer is filled to capacity.
     */
    private boolean offerTelemetrySTD(final T telemetry, final long permitTimeout, final long offerTimeout) throws UnavailableException, OverloadedException {
        if (useRateLimiter) {
            if (!theRateLimiter.tryAcquire(offerTimeout, TimeUnit.MILLISECONDS)) {
                throw new OverloadedException();
            }
        }
        return offer(telemetry, offerTimeout);
    }

    /**
     * Additive offer call. If the permit call takes less time than permitTimeout.
     * The left over time is added to the offerTimeout.
     *
     * @param telemetry The telemetry data object to be recorded.
     * @param permitTimeout The maximum milliseconds to wait for a permit to offer.
     * Permits provide the rate limiting service.
     * @param offerTimeout The maximum milliseconds to spend in the offer operation
     * @return True if the telemetry was offered. False otherwise.
     * @throws UnavailableException If this sink is unavailable.
     * @throws OverloadedException If the telemetryBuffer is filled to capacity.
     */
    private boolean offerTelemetryAdditive(final T telemetry, final long permitTimeout, final long offerTimeout) throws UnavailableException, OverloadedException {
        long time;
        if (useRateLimiter) {
            time = System.currentTimeMillis();
            if (!theRateLimiter.tryAcquire(offerTimeout, TimeUnit.MILLISECONDS)) {
                throw new OverloadedException();
            }
            time = offerTimeout - (System.currentTimeMillis() - time);
        } else {
            time = permitTimeout;
        }
        return offer(telemetry, offerTimeout + time);
    }

    /**
     * Implement the actual offer operation.
     *
     * @param telemetry The telemetry data object to be recorded.
     * @param offerTimeout The maximum milliseconds to spend in the offer operation
     * @return True if the telemetry was offered. False otherwise.
     * @throws OverloadedException if the telemetryBuffer is filled to capacity.
     */
    protected boolean offer(final T telemetry, final long offerTimeout) throws OverloadedException {
        try {
            return this.telemetryBuffer.offer(telemetry, offerTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            OverloadedException theOverloadedException = new OverloadedException();
            theOverloadedException.initCause(ex);
            throw theOverloadedException;
        }
    }

    /**
     * Provides an interface to write telemetry objects. Uses two timeouts.
     * 1. A timeout to wait for the ability to write. This time is the number of
     * milliseconds to wait for this sink to become available if it is currently
     * overloaded.
     * <br/>
     * 2. A timeout on the actual offer operation.
     *
     * Expect to have to tune the two timeout values in production to get
     * desired behavior.
     *
     * @param telemetry The telemetry data object to be recorded.
     * @param permitTimeout The maximum milliseconds to wait for a permit to offer.
     * Permits provide the rate limiting service.
     * @param offerTimeout The maximum milliseconds to spend in the offer operation
     * @param additiveTimeouts If true and a permit is issued before the
     * permitTimeout occurs any extra time will be added to the offerTimeout.
     * If false the permit and offer operations are timed independently and this
     * method can be expected to timeout at permitTimeout+offerTimeout in the
     * worst case. NOTE: If the classes
     * @return True if the telemetry was offered. False otherwise.
     * @throws UnavailableException If the system this sink writes to is not
     * available for some reason.
     * @throws OverloadedException If this sinks rate has been exceeded and a
     * permit is not available within the passed permitTimeout period.
     */
    public boolean offerTelemetry(final T telemetry, final long permitTimeout, final long offerTimeout, final boolean additiveTimeouts) throws UnavailableException, OverloadedException {
        //check enabled
        if (!this.isEnabled.get()) {
            throw new UnavailableException();
        }
        //Broker to apropriate offer
        if (additiveTimeouts) {
            return offerTelemetryAdditive(telemetry, permitTimeout, offerTimeout);
        } else {
            return offerTelemetrySTD(telemetry, permitTimeout, offerTimeout);
        }
    }

    /*
     * Updates the stable rate of this RTTelemetrySink, that is, the
     * maxOffersPerSecond argument provided in the method that
     * constructed the RTTelemetrySink. Currently throttled threads will not be
     * awakened as a result of this invocation, thus they do not observe the new
     * rate; only subsequent requests will.
     * Note though that, since each request repays (by waiting, if necessary) the
     * cost of the previous request, this means that the very next request after
     * an invocation to setRate will not be affected by the new rate; it will pay
     * the cost of the previous request, which is in terms of the previous rate.
     */
    public void setPermitsPerSecond(Double permitsPerSecond) {
        this.theRateLimiter.setRate(permitsPerSecond);
        this.permitsPerSecond = permitsPerSecond;
    }

    /**
     * Enable this telemetry sink.
     */
    public void enable() {
        isEnabled.compareAndSet(false, true);
    }

    /**
     * Disable this telemetry sink.
     * This is applied to new offer attempts only. In process offers will
     * continue.
     */
    public void disable() {
        isEnabled.compareAndSet(true, false);
    }

    /**
     * 1. Disables this sink so no further offers can be made.
     * 2. Waits 2 seconds for the sink to clear any buffered data.
     * 3. Stops the WorkerThread extension of this class from writing buffered
     * data
     * 4. flushes any left over buffered data.
     * 5. Closes any IO streams used to write data.
     *
     * @param why The reason for the shutdown. This can be retrieved by user
     * objects and used to track sinks that have been shutdown for the same
     * reasons as an example if a call to flushData times out this sink will
     * shutdown and users can find out why.
     */
    public void shutDown(final String why) {
        this.reasonForShutdown = why;
        shutDown();
    }

    public void shutDown() {
        disable();
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        setShouldRun(false);
        while (isAlive()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        try {
            flushData();
        } catch (UnavailableException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void doIt() {
        try {
            flushData();
        } catch (UnavailableException ex) {
            this.shutDown(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Flushes any data in the telemetry buffer to output
     */
    protected abstract void flushData() throws UnavailableException;

}
