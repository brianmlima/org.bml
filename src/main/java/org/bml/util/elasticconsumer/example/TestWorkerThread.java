
package org.bml.util.elasticconsumer.example;

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
