
package org.bml.util.alert;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.threads.WorkerThread;

/**
 * The Alert Control is a static interface and initializer for AlertHandler and
 * AlertTestExecutors
 *
 * @author Brian M. Lima
 */
public class AlertControl extends WorkerThread {

  /**
   * Standard Logging. All logging should be funneled through this log.
   */
  private static final Log LOG = LogFactory.getLog(AlertControl.class);
  /**
   * Use to avoid calling .class.getName() in high throughput situations
   */
  private static final String CLASS_NAME = AlertControl.class.getName().intern();
  /**
   * Use to avoid calling .class.getSimpleName() in high throughput situations
   */
  private static final String SIMPLE_CLASS_NAME = AlertControl.class.getSimpleName().intern();

  public AlertControl() {
    super(new ThreadGroup("AlertControl"), CLASS_NAME);
  }

  @Override
  protected void doIt() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
