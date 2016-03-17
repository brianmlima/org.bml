package org.bml.util.exception;

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
/**
 * <p>
 * {@link RateExceededException} is thrown by
 * {@link RateLimitedTask#attemptCall(java.lang.Object, long)} when calls to the
 * implementation of {@link RateLimitedTask#attemptCall(java.lang.Object, long)}
 * exceeds its call rate limit.</p>
 * <p>
 * This exception must be caught by all callers of
 * {@link RateLimitedTask#attemptCall(java.lang.Object, long)}</p>
 *
 * @author Brian M. Lima
 */
public class RateExceededException extends Exception {

    /**
     * The amount of time in milliseconds a caller should wait before attempting
     * again. Set to -1 if never and 0 if unknown.
     */
    private final long waitForRetry;
    /**
     * The rate that was exceeded.
     */
    private final long rate;

    /**
     * Constructs an instance of <code>RateExceededException</code> with the
     * specified detail message.
     *
     * @param message the detail message.
     * @param rate the rate that was exceeded.
     * @param waitForRetry the amount of time in mills a caller should wait to
     * try again. Set to -1 if never, 0 if unknown
     */
    public RateExceededException(final String message, final long rate, final long waitForRetry) {
        super(message);
        this.waitForRetry = waitForRetry;
        this.rate = rate;
    }

    /**
     * Constructs an instance of <code>RateExceededException</code> with the
     * specified detail message.
     *
     * @param message the detail message.
     * @param cause The Throwable cause
     * @param rate the rate that was exceeded.
     * @param waitForRetry the amount of time in mills a caller should wait to
     * try again. Set to -1 if never, 0 if unknown
     */
    public RateExceededException(final String message, final Throwable cause, final long rate, final long waitForRetry) {
        super(message, cause);
        this.waitForRetry = waitForRetry;
        this.rate = rate;
    }

    /**
     * The amount of time in milliseconds a caller should wait before attempting
     * again. Set to -1 if never and 0 if unknown.
     *
     * @return the amount of time in mills a caller should wait to try again.
     * Set to -1 if never, 0 if unknown.
     */
    public long getWaitForRetry() {
        return waitForRetry;
    }

}
