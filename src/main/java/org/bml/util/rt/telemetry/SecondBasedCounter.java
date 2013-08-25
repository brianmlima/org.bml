package org.bml.util.rt.telemetry;

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
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * This utility came out of an interview question. After some thought I figured
 * this would be a very handy utility for keeping cumulative time based
 * telemetry. NOTE there are a few places where I think there can be speed
 * improvements such as in the current second calculations. I chose to go with a
 * Calendar as it handles leap seconds and should keep telemetry tracking in
 * line with UTC. There are some drawbacks though as this could result in
 * seconds that have 0 during the leap second. This is where the trade off comes
 * as a real time system may be dependant on the last 60 seconds and not care
 * about keeping in sync with the actual calendar second. It is because of this
 * I plan on implementing an option to use a rolling secondID method that
 * ignores the Calendar adjustments although I am not sure what the exact
 * behavior this would cause at a high level in a hard real time system.
 *
 * Only testing and analysis will tell what if any modifications are necessary.
 *
 * @author Brian M Lima
 */
public class SecondBasedCounter {
  private final int windowSize;
  private AtomicLong numOperations;
  private final AtomicInteger[] counterArray;
  private final String telemetryStreamId;

  public SecondBasedCounter(final int windowSize, final String telemetryStreamId) {
    this.telemetryStreamId=telemetryStreamId;
    this.windowSize = windowSize;
    this.counterArray = new AtomicInteger[this.windowSize];
    
    for(int c=0;c<counterArray.length;c++){
      counterArray[c] = new AtomicInteger(0);
    }
    numOperations = new AtomicLong(0);
  }

  
  /**
   * 
   * @return 
   */
  public AtomicInteger[] getCounterArray(){
      return counterArray;
  }
  
  /**Telemetry for accessing usage.. Use full for usage telemetry, debugging,
   * and component use trending.
   * 
   * @return the number of times increment has been called for the life of this 
   * object
   */
  public long getNumOperations(){
    return this.numOperations.get();
  }
  
  /**
   * Increments the counter for the current second. If it becomes necessary I
   * may store the current second id in the class for telemetry however at this 
   * point it looks like there is no reason for it and it will introduce more 
   * complexity to ensure the id is accurate without manual locking.
   */
  public void increment() {
    //increment total operations
    numOperations.incrementAndGet();
    //Handle the increment
    this.counterArray[this.getCurrentSecondID()].incrementAndGet();
 }

  /**
   * There may be a way to do this faster however I chose to use a bound
   * DescriptiveStatistics object to allow the caller to encapsulate more
   * operations than just a simple sum.
   *
   * @return DescriptiveStatistics containing the last 60 seconds of telemetry
   * data. because of the use of atomics this should be as up to date as
   * possible without getting into manual locking which this method and all
   * methods in this class are contracted to avoid at all costs.
   */
  public DescriptiveStatistics getLastMinutesTelemetry() {
    DescriptiveStatistics stats = new DescriptiveStatistics(60);
    for (int c = 0; c < 60; c++) {
      stats.addValue(counterArray[c].doubleValue());
    }
    return stats;
  }

  /**
   * This method should be studied carefully to see if there is a faster way to
   * get the current second without creating a Calendar instance.
   * Either way this method an be overridden if a faster or more appropriate 
   * calculation is found.
   *
   * @return
   */
  public static int getCurrentSecondID() {
    return Calendar.getInstance(TimeZone.getTimeZone("UTC".intern())).get(Calendar.SECOND);
  }
}