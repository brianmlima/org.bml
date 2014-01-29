/*
 */

package org.bml.util.rt.telemetry.track;

/*
 * #%L
 * org.bml
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian M. Lima
 */
public interface AtomicIntegerInterface {

  int addAndGet(int delta);

  byte byteValue();

  boolean compareAndSet(int expect, int update);

  int decrementAndGet();

  double doubleValue();

  float floatValue();

  int get();

  int getAndAdd(int delta);

  int getAndDecrement();

  int getAndIncrement();

  int getAndSet(int newValue);

  int incrementAndGet();

  int intValue();

  void lazySet(int newValue);

  long longValue();

  void set(int newValue);

  short shortValue();

  boolean weakCompareAndSet(int expect, int update);
  
}