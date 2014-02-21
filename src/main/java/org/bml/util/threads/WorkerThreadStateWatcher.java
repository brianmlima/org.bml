
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
            long millsSinceLastStateChange = TimeUnit.MILLISECONDS.convert(thread.getSecondsSinceLastStateChange(),TimeUnit.SECONDS);
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
