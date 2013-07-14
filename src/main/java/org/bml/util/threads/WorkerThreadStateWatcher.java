
package org.bml.util.threads;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.TimeUtils;

/**
 * This thread is meant to monitor generic worker threads in case they are stuck
 * in a certain state. There is also a provision for a take action method to deal
 * with threads stuck in a state.
 *
 * @author Brian M. Lima
 */
public abstract class WorkerThreadStateWatcher extends TimeoutNotificationThread {

    /**
     */
    private static Log LOG = LogFactory.getLog(WorkerThreadStateWatcher.class);
    /**
     * The thread to monitor state.
     */
    private WorkerThread thread = null;

    public WorkerThreadStateWatcher(TimeUnit theTimeUnit, long unitCount, WorkerThread thread) {
        super(theTimeUnit, unitCount);
        this.setLogName("WorkerThreadStateWatcher for Thread" + thread.getId());
        this.thread = thread;
    }

    @Override
    protected void doIt() {
        while (this.getShouldRun()) {
            long millsSinceLastStateChange = TimeUtils.timeUnitsToMilliseconds(TimeUnit.SECONDS, thread.getSecondsSinceLastStateChange());
            WORKER_STATE state = thread.getWorkerState();
            if (millsSinceLastStateChange > getTimeoutInMills()) {
                LOG.info(this.getInfoMessage(thread.getLogPrefix(), state, millsSinceLastStateChange));
                takeAction();
            }
            try {
                sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WorkerThreadStateWatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!thread.isAlive()) {
                this.setShouldRun(false);
                break;
            }
        }
    }

    public String getInfoMessage(String logPrefix, WORKER_STATE state, Long millsSinceLastStateChange) {
        return thread.getLogPrefix() + " STALLED IN STATE " + state + " FOR " + millsSinceLastStateChange + " mills.";
    }

    /**
     * @return the thread
     */
    public WorkerThread getThread() {
        return thread;
    }

    /**
     * @param thread the thread to set
     */
    public void setThread(WorkerThread thread) {
        this.thread = thread;
    }
    public abstract void takeAction();
}
