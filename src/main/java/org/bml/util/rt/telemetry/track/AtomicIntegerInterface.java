
package org.bml.util.rt.telemetry.track;

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
