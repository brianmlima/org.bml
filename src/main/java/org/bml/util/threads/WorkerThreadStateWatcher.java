package org.bml.util.threads;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2014 Brian M. Lima
 * %%
 * This file is part of ORG.BML.
 *     ORG.BML is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *     ORG.BML is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with ORG.BML.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This theWorkerThread is meant to monitor generic worker threads in case they are stuck
 * in a certain state. There is also a provision for a take action method to deal
 * with threads stuck in a state.
 *
 * @author Brian M. Lima
 */
public abstract class WorkerThreadStateWatcher extends TimeoutNotificationThread {

    /**
     */
    private static final Logger LOG = LoggerFactory.getLogger(WorkerThreadStateWatcher.class);
    /**
     * The theWorkerThread to monitor state.
     */
    private final WorkerThread theWorkerThread;

    /**
     * @param theWorkerThread a WorkerThreadInstance
     * @param theTimeoutInMills The number of milliseconds a theWorkerThread is allowed to hold the same state. .
     */
    public WorkerThreadStateWatcher(final WorkerThread theWorkerThread, final long theTimeoutInMills) {
        super(theTimeoutInMills);
        this.setLogName("WorkerThreadStateWatcher for Thread" + theWorkerThread.getId());
        this.theWorkerThread = theWorkerThread;
    }

    @Override
    protected void doIt() {
        long millsSinceLastStateChange;
        WORKER_STATE theWorkerState;
        while (this.getShouldRun()) {
            millsSinceLastStateChange = TimeUnit.MILLISECONDS.convert(theWorkerThread.getSecondsSinceLastStateChange(), TimeUnit.SECONDS);
            theWorkerState = theWorkerThread.getWorkerState();
            if (millsSinceLastStateChange > getTimeoutInMills()) {
                LOG.info(this.getInfoMessage(theWorkerState, millsSinceLastStateChange));
                takeAction();
            }
            try {
                sleep(super.getTimeoutInMills());
            } catch (InterruptedException ex) {
                LOG.error("InterruptedException caught while sleeping.", ex);
            }
            if (!theWorkerThread.isAlive()) {
                this.setShouldRun(false);
                break;
            }
        }
    }

    /**
     * Helper method for constructing a readable info message regarding the passes state.
     *
     * @param theWorkerState The state the message is about.
     * @param millsSinceLastStateChange The amount of time the state has been active.
     * @return A String message containing the workers current state info.
     */
    public String getInfoMessage(final WORKER_STATE theWorkerState, final long millsSinceLastStateChange) {
        StringBuilder buff = new StringBuilder();
        buff.append(theWorkerThread.getLogPrefix());
        buff.append(" STALLED IN STATE ");
        buff.append(theWorkerState);
        buff.append(" FOR ");
        buff.append(millsSinceLastStateChange);
        buff.append("MILLISECONDS.");
        return buff.toString();
    }

    /**
     * Returns the {@link WorkerThread} this watcher is watching.
     *
     * @return the theWorkerThread
     */
    public WorkerThread getThread() {
        return theWorkerThread;
    }

    /**
     * This is the method that is called when a {@link WorkerThread} has not
     * changed state and a notification is made.
     * Consider this method the notification.
     */
    public abstract void takeAction();
}
