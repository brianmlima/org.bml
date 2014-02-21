
package org.bml.util.threads;

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

import java.util.concurrent.TimeUnit;

/**
 * Used like an alarm clock or timer task but is more portable and has the
 * advantage of being an extension of the boiler plate WorkerThread.
 *
 * @author Brian M. Lima
 */
public abstract class TimeoutNotificationThread extends WorkerThread {

  /**
   * The TimeUnit this thread uses to convert unitCount to milliseconds.
   */
  private TimeUnit theTimeUnit;
  /**
   * The number of TimeUnit's converted to milliseconds.
   */
  private long unitCount;
  /**
   * The long used to sleep between calls to the worker threads doIt() method.
   */
  private long timeoutInMills;

  /**
   * @param theTimeUnit The time unit this notification should alert at
   * @param unitCount The number of TimeUnits this notification should trigger
   */
  public TimeoutNotificationThread(ThreadGroup tg, String string, TimeUnit theTimeUnit, long unitCount) {
    super(tg, string);
    configure(theTimeUnit, unitCount);
  }

  /**
   * @param theTimeUnit The time unit this notification should alert at
   * @param unitCount The number of TimeUnits this notification should trigger
   */
  public TimeoutNotificationThread(TimeUnit theTimeUnit, long unitCount) {
    super();
    configure(theTimeUnit, unitCount);
  }

  /** A Configure helper method in order to avoid duplicate code in 
   * constructions.
   * @param theTimeUnit The time unit this notification should alert at
   * @param unitCount The number of TimeUnits this notification should trigger
   */
  private void configure(TimeUnit theTimeUnit, long unitCount) {
    this.theTimeUnit = theTimeUnit;
    this.unitCount = unitCount;
    timeoutInMills = TimeUnit.MILLISECONDS.convert(unitCount, theTimeUnit);
    this.setShouldRun(true);
    super.start();
  }

  /** POJO timeoutInMills
   * @return the timeoutInMills
   */
  public long getTimeoutInMills() {
    return timeoutInMills;
  }

  /** POJO timeoutInMills
   * @param timeoutInMills the timeoutInMills to set
   */
  public void setTimeoutInMills(long timeoutInMills) {
    this.timeoutInMills = timeoutInMills;
  }
  
  //Unnecessary as the doIt Method serves this purpose from WorkerThread
  //public abstract runOnTimeOut(WORKER_THREAD threadSa)

}
