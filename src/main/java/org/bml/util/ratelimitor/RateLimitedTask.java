 package org.bml.util.ratelimitor;

import org.bml.util.exception.DisabledException;
import org.bml.util.exception.UnavailableException;

/**{@link RateLimitedTask} An interface for rate limited service.
 * @author Brian M. Lima
 */
public interface RateLimitedTask<R, C> {

    /**
     * Check method for seeing if the underlying service is available for usage.
     *
     * @return true if service is available for query false otherwise
     */
    public boolean isAvailable();

    /** 
     *
     * @param callConfiguration An object containing the configuration for the
     * call to be made. This is usually some extension of Properties and or a
     * Map.
     * @param timeout A long denoting the ammount of time to wait for a call to
     * complete.
     * @return
     * @throws RateExceededException if the rate limit of the underlying system
     * is exceeded.
     * @throws DisabledException If the underlying system has been disabled.
     * @throws UnavailableException If The underlying system is unavailable
     * @throws InterruptedException If the call timeout is exceeded and the
     * attempt is interrupted.
     */
    public R attemptCall(final C callConfiguration, final long timeout) throws RateExceededException, DisabledException, UnavailableException, InterruptedException;

}
