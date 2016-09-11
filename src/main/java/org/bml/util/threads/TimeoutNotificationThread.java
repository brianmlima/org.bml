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
/**
 * Used like an alarm clock or timer task but is more portable and has the
 * advantage of being an extension of the boiler plate WorkerThread.
 *
 * @author Brian M. Lima
 */
public abstract class TimeoutNotificationThread extends WorkerThread {

    /**
     * The number of milliseconds to tolerate. IE: a notification will occur after.
     */
    private long theTimeoutInMills;

    /**
     * Creates a new instance of {@link TimeoutNotificationThread}.
     *
     * @param theThreadGroup The ThreadGroup this thread should join.
     * @param theThreadName The name this thread should take on.
     * @param theTimeoutInMills The number of milliseconds to sleep between calls to the worker threads doIt() method.
     */
    public TimeoutNotificationThread(final ThreadGroup theThreadGroup, final String theThreadName, final long theTimeoutInMills) {
        super(theThreadGroup, theThreadName);
        this.theTimeoutInMills = theTimeoutInMills;
    }

    /**
     * Creates a new instance of {@link TimeoutNotificationThread}.
     *
     * @param theTimeoutInMills The number of milliseconds to sleep between calls to the worker threads doIt() method.
     */
    public TimeoutNotificationThread(final long theTimeoutInMills) {
        super();
        this.theTimeoutInMills = theTimeoutInMills;
    }

    /**
     * getter for theTimeoutInMills.
     *
     * @return the theTimeoutInMills
     */
    public long getTimeoutInMills() {
        return theTimeoutInMills;
    }

    /**
     * getter for theTimeoutInMills.
     *
     * @param timeoutInMills the theTimeoutInMills to set
     */
    public void setTimeoutInMills(final long timeoutInMills) {
        this.theTimeoutInMills = timeoutInMills;
    }

  //Unnecessary as the doIt Method serves this purpose from WorkerThread
    //public abstract runOnTimeOut(WORKER_THREAD threadSa)
}
