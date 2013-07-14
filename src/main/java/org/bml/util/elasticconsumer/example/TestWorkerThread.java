
package org.bml.util.elasticconsumer.example;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bml.util.threads.BlockingQueueWorkerThread;

/**A test implementation of BlockingQueueWorkerThread<ProcData>.
 * NOTE: takes using the super class and waits 1 second before setting a boolean to true
 * and moving on.
 * @author Brian M. Lima
 */
public class TestWorkerThread extends BlockingQueueWorkerThread<ProcData> {

    /**Creates a new instance of TestWorkerThread.
     * @param queueIn The BlockingQueue<ProcData> for worker threads to poll.
     * @param timeout The worker threads poll timeout.
     * @param unit The worker threads poll timeout TimeUnit.
     * @param waitOnEmptyQueueInMills The worker threads sleep time on an empty queue.
     */
    public TestWorkerThread(BlockingQueue<ProcData> queueIn, long timeout, TimeUnit unit, long waitOnEmptyQueueInMills) {
        super(queueIn, timeout, unit, waitOnEmptyQueueInMills);
    }

    /**This is where the action is.
     * @param data
     */
    @Override
    protected void doIt(ProcData data) {
        try {
            sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ElasticConsumerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        data.wasProcessed = true;
    }
}
