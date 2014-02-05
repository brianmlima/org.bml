package org.bml.util.ratelimitor;

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
/**
 * <p>
 * {@link RateExceededException} is thrown by
 * {@link RateLimitedTask#attemptCall(java.lang.Object, long)} when calls to the
 * implementation of {@link RateLimitedTask#attemptCall(java.lang.Object, long)}
 * exceeds its call rate limit.</p>
 * <p>This exception must be caught by all callers of
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
     * Constructs an instance of <code>RateExceededException</code> with the
     * specified detail message.
     *
     * @param message the detail message.
     * @param waitForRetry the amount of time in mills a caller should wait to
     * try again. Set to -1 if never, 0 if unknown
     */
    public RateExceededException(final String message, final long waitForRetry) {
        super(message);
        this.waitForRetry = waitForRetry;
    }

    /**
     * Constructs an instance of <code>RateExceededException</code> with the
     * specified detail message.
     *
     * @param message the detail message.
     * @param cause The Throwable cause
     * @param waitForRetry the amount of time in mills a caller should wait to
     * try again. Set to -1 if never, 0 if unknown
     */
    public RateExceededException(final String message, final Throwable cause, final long waitForRetry) {
        super(message, cause);
        this.waitForRetry = waitForRetry;
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
