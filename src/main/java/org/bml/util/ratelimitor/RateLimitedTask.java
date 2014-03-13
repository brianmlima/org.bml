 package org.bml.util.ratelimitor;

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
     * @return R an object that is the result of a successful call.
     * @throws RateExceededException if the rate limit of the underlying system
     * is exceeded.
     * @throws DisabledException If the underlying system has been disabled.
     * @throws UnavailableException If The underlying system is unavailable
     * @throws InterruptedException If the call timeout is exceeded and the
     * attempt is interrupted.
     */
    public R attemptCall(final C callConfiguration, final long timeout) throws RateExceededException, DisabledException, UnavailableException, InterruptedException;

}
