/**
 *   This file is part of org.bml.
 *
 *   org.bml is free software: you can redistribute it and/or modify it under the
 *   terms of the GNU General Public License as published by the Free Software
 *   Foundation, either version 3 of the License, or (at your option) any later
 *   version.
 *
 *   org.bml is distributed in the hope that it will be useful, but WITHOUT ANY
 *   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 *   A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License along with
 *   org.bml. If not, see <http://www.gnu.org/licenses/>.
 */


package org.bml.util.alert;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bml.util.threads.TimeoutNotificationThread;

/**
 * A generic Alert Test execution based on the TimeoutNotificationThread class 
 * which is an extension of the WorkerThread.
 * Given the small ammount of code in this class at some point we should take
 * a harder look and see if this can be implemented with minor modifications to 
 * the TimeoutNotificationThread
 * Complete the class to run tests.
 *
 * @author Brian M. Lima
 */
public abstract class AlertTestExecutor extends TimeoutNotificationThread {

  /**
   * The AlertHandler this Thread will use to implement an alert.
   */
  private AlertHandler theAlertHandler = null;

  /**
   * 
   * @param tg
   * @param string
   * @param theTimeUnit
   * @param unitCount
   * @param theAlertHandler 
   */
  public AlertTestExecutor(ThreadGroup tg, String string,TimeUnit theTimeUnit, long unitCount,AlertHandler theAlertHandler) {
    super(tg, string,theTimeUnit, unitCount);
    this.theAlertHandler=theAlertHandler;
  }
  
  /**
   * 
   * @param theTimeUnit
   * @param unitCount
   * @param theAlertHandler 
   */
  public AlertTestExecutor(TimeUnit theTimeUnit, long unitCount,AlertHandler theAlertHandler) {
    super(theTimeUnit, unitCount);
    this.theAlertHandler=theAlertHandler;
  }
  /**
   * Override WorkerThread's method and add test / sleep interval
   */
  @Override
  public void doIt() {
    if (this.getShouldRun()) {
      try {
        test();
        sleep(super.getTimeoutInMills());
      } catch (InterruptedException ex) {
        Logger.getLogger(AlertTestExecutor.class.getName()).log(Level.SEVERE, null, ex);
        this.setShouldRun(false);
      } catch (Exception e){
        
      }
    }
  }
  /**This is the real meat of the class
   * @return boolean true on test success, false on fail and null if the AlertHandler is
   * disabled or a sub component is unavailable.
   */
  public abstract Boolean test();

  /**
   * @return the theAlertHandler
   */
  public AlertHandler getTheAlertHandler() {
    return theAlertHandler;
  }

  /**
   * @param theAlertHandler the theAlertHandler to set
   */
  public void setTheAlertHandler(AlertHandler theAlertHandler) {
    this.theAlertHandler = theAlertHandler;
  }
}