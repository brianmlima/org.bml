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


package org.bml.util.threads;

import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.bml.util.StopWatch;

/**
 * <p>A Thread with graceful shutdown extensions and a runtime boolean check of
 * shouldRun to ensure proper initialization. These two extensions are
 * especially handy when implementing pools of threads that may or may not have
 * been initialized correctly by an ObjectFactory.</p>
 *
 * <p>Commons math
 * <code>DescriptiveStatistics</code> based cycle telemetry and extensions
 * allowing thread state telemetry using the
 * <code>WORKER_STATE</code> Enum. NOTE: Implementations of this class must use
 * <code>WORKER_STATE</code> and the tracking methods at runtime for state to
 * change.
 * </p>
 *
 * <p><b>Note:</b> Be aware extensions of this class will not 'run' unless<br/>
 * <code>getShouldRun() == true</code> </p>
 *
 * @author Brian M. Lima
 */
public abstract class WorkerThread extends Thread {

  /**
   * Empty constructor. This constructor may be depreciated in the next release
   * in order to force implementations to use the
   * <code>WorkerThread(ThreadGroup theThreadGroup, String theThreadName)</code>
   * constructor.
   */
  public WorkerThread() {
    super();
  }

  /**
   * The recommended constructor to use when creating an implementation. By
   * giving the thread a group and a name you make lookup and use by other
   * components possible without a direct reference.
   *
   * @param theThreadGroup The thread group to add this thread to.
   * @param theThreadName the name of the thread.
   */
  public WorkerThread(ThreadGroup theThreadGroup, String theThreadName) {
    super(theThreadGroup, theThreadName);
  }
  /**
   * Most classes use a static Log. Because this is a base class its log is only
   * added here for bare implementations. Leaf classes should have a standard
   * private static Log LOG.
   */
  private Log log = LogFactory.getLog(WorkerThread.class);
  /**
   * Controls if a thread is capable of starting or if it should keep running.
   */
  private boolean shouldRun = false;
  /**
   * If true the thread will attempt to track the elapsed time for each run
   * cycle using the DescriptiveStatistics object if it is not null;
   */
  private boolean trackInstanceCycles = false;
  /**
   * Commons Math based stats object for cycle telemetry tracking.
   */
  private DescriptiveStatistics theDescriptiveStatistics = null;
  /**
   * Helps track application behavior and provide a way to avoid all workers
   * entering into the DB at once. IE: starving the incoming queue. Also enables
   * fine grained telemetry monitoring. Eventually this should be moved out to a
   * new class called StateTracker and StateMonitor
   */
  private WORKER_STATE state = WORKER_STATE.STOPPED;
  /**
   * The last time this threads state changed
   */
  private Date lastStateChange = new Date();
  /**
   * The StopWatch used to track state telemetry over time.
   */
  private StopWatch lastStateWatch = new StopWatch();

  /**
   * @return WORKER_STATE
   */
  public WORKER_STATE getWorkerState() {
    return this.state;
  }

  /**
   * @return Date
   */
  public Date getLastStateChangeDate() {
    return this.lastStateChange;
  }

  /**
   * @return long
   */
  public long getSecondsSinceLastStateChange() {
    return this.lastStateWatch.getElapsedTimeSecs();
  }

  /**
   *
   * @param state
   */
  public void setWorkerState(WORKER_STATE state) {
    this.state = state;
    this.lastStateWatch.stop();
    this.lastStateWatch.start();
    this.lastStateChange = new Date();
  }

  /**
   * Defines possible worker states. TODO: Pull this Enum out of the base class.
   */
  public static enum WORKER_STATE {

    /**
     * 
     */
    STARTING,
    /**
     *
     */
    STOPPING,
    /**
     *
     */
    STOPPED,
    /**
     *
     */
    AQUIRINGCONNECTION,
    /**
     *
     */
    PULLING,
    /**
     *
     */
    WAITING,
    /**
     *
     */
    PUSHING,
    /**
     *
     */
    ENTERING,
    /**
     *
     */
    EXECUTING_BATCH,
    /**
     *
     */
    COMMITTING,
    /**
     *
     */
    CHECKING_COMMIT,
    /**
     *
     */
    OFFERINGERROR,
    /**
     *
     */
    FAILING,
    /**
     *
     */
    FLUSHING,
    /**
     *
     */
    BATCHING
  }

  /**
   * Gets the shouldRun value.
   *
   * @return shouldRun True if <code>setShouldRun(true); </code> has been
   * called. False otherwise.
   */
  public boolean getShouldRun() {
    return shouldRun;
  }

  /**
   * Overrides
   * <code>Thread.run()</code> to implement
   * <code>getShouldRun()</code> and
   * <code>doShutdown()</code> WorkerThread extensions.
   */
  @Override
  public void run() {
    StopWatch watch = new StopWatch();
    /**
     * check shoudlRun. We commit to a full run so overrides of doIt are
     * expected to handle as an atomic transaction
     */
    while (shouldRun) {
      //TRACK INSTANCE TELEMETRY
      if (trackInstanceCycles) {
        watch.start();
      }
      //EXECUTE
      doIt();
      //TRACK INSTANCE TELEMETRY
      if (trackInstanceCycles) {
        watch.stop();
        //Be definsive. NOTE: This should probubly be encapsulated or
        //initialization should be revisited to ensure these checks are 
        //not necessary.
        if (theDescriptiveStatistics == null) {
          this.trackInstanceCycles = Boolean.FALSE;
          String tmpWarning = this.getLogPrefix() + "WorkerThread object Cycle Telemtry Tracking sub-system is misconfigured. I am taking action and halting Cycle Telemtry Tracking for THREAD #";
          if (log != null) {
            log.warn(tmpWarning);
          } else {
            System.out.println(tmpWarning);
          }
          continue;
        }
        theDescriptiveStatistics.addValue(watch.getElapsedTime());
      }
    }
    //call shutdown handler.
    doShutdown();
  }

  /**
   * The actual run operation. this is called continuously by the
   * WorkerThread.run method until shouldRun is false.
   */
  protected abstract void doIt();

  /**
   * Override me for special shutdown
   */
  protected void doShutdown() {
    System.out.println("WorkerThread " + this.getId() + " shutting down");
    setShouldRun(false);
  }

  /**
   * Sets the shouldRun control for the WorkerThread.
   *
   * @param shouldRun A boolean to set shouldRun to.
   */
  public void setShouldRun(boolean shouldRun) {
    this.shouldRun = shouldRun;
  }

  /**
   * Override for workers that use storage if you want to force a flush to
   * storage. This is provisioned here so monitors and handlers can use the
   * WorkerThead in pipelines where the operations are not specific to the
   * implementation class.
   *
   * @return the number of objects that were flushed. May throw an
   * UnsupportedOperationException if the implementation class does not override
   * correctly.
   */
  public synchronized int flush() {
    throw new UnsupportedOperationException("Base WorkerThread does not implement flush");
  }

  /**
   * Get the Boolean used to by this class to turn instance cycle telemetry on
   * and off.
   *
   * @return the trackCycles
   */
  public boolean getTrackInstanceCycles() {
    return trackInstanceCycles;
  }

  /**Synched to avoid null pointer exceptions from run when under very high 
   * load.
   * @param trackInstanceCycles the trackInstanceCycles to set. 
   */
  public synchronized void setTrackInstanceCycles(boolean trackInstanceCycles) {
    if (trackInstanceCycles == true) {
      if (this.trackInstanceCycles == false) {
        this.setTheDescriptiveStatistics(new DescriptiveStatistics());
      }
    } else {
      this.setTheDescriptiveStatistics(null);
      this.trackInstanceCycles = Boolean.FALSE;
    }
  }

  /**
   * @return the theStatisticalSummary
   */
  public DescriptiveStatistics getTheDescriptiveStatistics() {
    return theDescriptiveStatistics;
  }

  /**
   * @param theDescriptiveStatistics
   */
  public void setTheDescriptiveStatistics(DescriptiveStatistics theDescriptiveStatistics) {
    if (theDescriptiveStatistics == null) {
      this.trackInstanceCycles = Boolean.FALSE;
      this.theDescriptiveStatistics = null;
      return;
    }
    this.theDescriptiveStatistics = theDescriptiveStatistics;
    trackInstanceCycles = Boolean.TRUE;
  }
  
  /**
   */
  private String logPrefix = null, logName = null;

  /**
   * @return
   */
  public String getLogPrefix() {
    if (logPrefix == null) {
      logPrefix = new StringBuilder().append("LOG_NAME=").append(logName).append(" THREAD_ID=").append(this.getId()).toString();
    }
    return logPrefix;
  }

  /**
   * @return the logName
   */
  public String getLogName() {
    return logName;
  }

  /**
   * @param logName the logName to set
   */
  public void setLogName(String logName) {
    this.logName = logName;
  }
}
