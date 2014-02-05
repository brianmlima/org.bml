
package org.bml.util.ratelimitor;

import org.bml.util.exception.DisabledException;
import org.bml.util.exception.UnavailableException;

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
 * @author Brian M. Lima
 */
public interface RateLimitedTask<R,C> {

    
    /**
     * 
     * @param callConfiguration An object containing the configuration for the
     * call to be made. This is usually some extension of Properties and or a Map.
     * @param timeout A long denoting the ammount of time to wait for a call to 
     * complete.
     * @return 
     * @throws org.bml.util.exception.RateExceededException 
     * @throws org.bml.util.exception.DisabledException 
     * @throws org.bml.util.exception.UnavailableException 
     */
    public R attemptCall(final C callConfiguration,final long timeout) throws RateExceededException, DisabledException,UnavailableException;
    
    
}
