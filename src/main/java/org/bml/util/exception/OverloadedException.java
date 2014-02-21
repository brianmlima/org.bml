package org.bml.util.exception;

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

/**Thrown when a service is overloaded. There is a distinct difference between
 * RateExceeded which is when a third party imposed limit is reached and this 
 * exception which is used to describe a condition where the service being 
 * called can not keep up with the demand and is backing up, causing RT 
 * operations to time out. This exception should usually be followed by a 
 * refactoring of the service to batch.
 *
 * @author Brian M. Lima
 */
public class OverloadedException extends Exception {

  /**
   * Creates a new instance of
   * <code>OverloadedException</code> without detail message.
   */
  public OverloadedException() {
  }

  /**
   * Constructs an instance of
   * <code>OverloadedException</code> with the specified detail message.
   *
   * @param msg the detail message.
   */
  public OverloadedException(String msg) {
    super(msg);
  }
}
