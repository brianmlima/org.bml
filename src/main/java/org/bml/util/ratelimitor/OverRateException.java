
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
 *
 * @author brianmlima
 */
public class OverRateException extends Exception {

    /**
     * Creates a new instance of
     * <code>OverRateException</code> without detail message.
     */
    public OverRateException() {
    }

    /**
     * Constructs an instance of
     * <code>OverRateException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public OverRateException(String msg) {
        super(msg);
    }
}