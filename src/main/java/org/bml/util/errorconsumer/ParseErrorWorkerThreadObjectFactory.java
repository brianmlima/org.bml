
package org.bml.util.errorconsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.bml.util.ObjectFactory;

/** An implementation of an ObjectFactory<ParseErrorWorkerThread> for the ParseError
 * handler.
 * @author BML
 */
public class ParseErrorWorkerThreadObjectFactory implements ObjectFactory<ParseErrorWorkerThread> {

    /**This input queue. this is only used in the factory for TestWorkerThread initialization*/
    private BlockingQueue<ParseError> queueIn = null;
    /*Used for TestWorkerThread initialization*/
    private long timeout = 1,  waitOnEmptyQueueInMills = 1000;
    /*TimeUnit for polling timeout. Used for TestWorkerThread initialization*/
    private TimeUnit unit = TimeUnit.SECONDS;

    /**Creates a new instance of .
     * @param queueIn The BlockingQueue<ProcData> for worker threads to poll.
     * @param timeout The worker threads poll timeout.
     * @param unit The worker threads poll timeout TimeUnit.
     * @param waitOnEmptyQueueInMills The worker threads sleep time on an empty queue;
     */
    public ParseErrorWorkerThreadObjectFactory(BlockingQueue<ParseError> queueIn, long timeout, TimeUnit unit, long waitOnEmptyQueueInMills) {
        this.queueIn = queueIn;
        this.timeout = timeout;
        this.unit = unit;
        this.waitOnEmptyQueueInMills = waitOnEmptyQueueInMills;
    }

    /**Makes a new TestWorkerThread.
     * @return a TestWorkerThread ready to be started.
     */
    @Override
    public ParseErrorWorkerThread makeObject() {
        return new ParseErrorWorkerThread(queueIn, timeout, unit, waitOnEmptyQueueInMills);
    }

    /**Destruction method. Not necessary in the test case but handy in cleanup operations.
     * @param obj TestWorkerThread to be destroyed.
     * @return always true;
     */
    @Override
    public boolean destroyObject(ParseErrorWorkerThread obj) {
        obj.setShouldRun(false);
        obj.interrupt();
        obj.handleDBEntry();
        return true;
    }
}
