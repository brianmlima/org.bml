
package org.bml.util.threads;

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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An Extension of Worker Thread for encapsulating thread infrastructure
 * designed to specifically feed off of a BlockingQueue<T>.
 *
 * @author Brian M. Lima
 */
public abstract class BlockingQueueWorkerThread<T> extends WorkerThread {

  /**
   * Standard Logging. All logging should be funneled through this log.
   */
  private static final Log LOG = LogFactory.getLog(BlockingQueueWorkerThread.class);
  /**
   * Use to avoid calling .class.getName() in high throughput situations
   */
  private static final String CLASS_NAME = BlockingQueueWorkerThread.class.getName().intern();
  /**
   * Use to avoid calling .class.getSimpleName() in high throughput situations
   */
  private static final String SIMPLE_CLASS_NAME = BlockingQueueWorkerThread.class.getSimpleName().intern();
  /**
   * The Queue this implementation watches
   */
  private BlockingQueue<T> queueIn = null;
  /**
   */
  private long timeout = 1;
  /**
   */
  private TimeUnit unit = TimeUnit.SECONDS;
  /**
   */
  long waitOnEmptyQueueInMills = 1000;

  /**
   * Creates a new BlockingQueueWorkerThread.
   *
   * @param queueIn The BlockingQueue<T> for worker threads to poll.
   * @param timeout The worker threads poll timeout.
   * @param unit The worker threads poll timeout TimeUnit.
   * @param waitOnEmptyQueueInMills The worker threads sleep time on an empty
   * queue.
   */
  public BlockingQueueWorkerThread(BlockingQueue<T> queueIn, long timeout, TimeUnit unit, long waitOnEmptyQueueInMills) {
    this.queueIn = queueIn;
    this.timeout = timeout;
    this.unit = unit;
    this.waitOnEmptyQueueInMills = waitOnEmptyQueueInMills;
  }

  /**
   * The main run method substitute from WorkerThread.
   *
   * This method handles the pulling of data off the queue and passes it to
   * <code>protected abstract void doIt(T obj);</code>
   *
   */
  @Override
  protected void doIt() {
    //Check queue for nulls
    if(queueIn==null){
      if(LOG.isErrorEnabled()){
        LOG.fatal("Found null queueIn. Setting ShouldRun to false and exiting. It is recomended you check the configuration of any implementation class.");
      }
      this.setShouldRun(false);
      return;
    }

    T data = null;
    try {
      data = queueIn.poll(timeout, unit);
      if (data == null) {
        sleep(waitOnEmptyQueueInMills);
      } else {
        doIt(data);
      }
    } catch (InterruptedException ex) {
      if(LOG.isWarnEnabled()){
        LOG.warn("InterruptedException caught. Shutting down gracefully",ex);
      }
      this.setShouldRun(false);
      return;
    } catch (NullPointerException npe){
      if(LOG.isWarnEnabled()){
        LOG.warn("NullPointerException caught. This can only be caused by a null queueIn or a bubble up from doIt(data). Shutting down gracefully",npe);
      }
      this.setShouldRun(false);
      return;
      
    }catch (OutOfMemoryError oome){
      if(LOG.isFatalEnabled()){
        LOG.fatal("OutOfMemoryError caught. Generally unrecoverable. Shutting down gracefully",oome);
      }
      this.setShouldRun(false);
      return;      
    } catch (Exception ex) {
      if(LOG.isErrorEnabled()){
        LOG.fatal("Exception caught. It must have bubbled up from the doIt(data) implementation. Generally unrecoverable. Shutting down gracefully",ex);
      }
      this.setShouldRun(false);
      return;      
    }
  }

  /**
   *
   * @param obj T object to be acted upon.
   */
  protected abstract void doIt(T obj);
}
