
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

/**
 *
 * @author Brian M. Lima
 */
public class UnavailableException extends Exception {

  /**
   * Creates a new instance of
   * <code>UnavailableException</code> without detail message.
   */
  public UnavailableException() {
  }

  /**
   * Constructs an instance of
   * <code>UnavailableException</code> with the specified detail message.
   *
   * @param msg the detail message.
   */
  public UnavailableException(String msg) {
    super(msg);
  }

  public UnavailableException(String string, Throwable thrwbl) {
    super(string, thrwbl);
  }
}
