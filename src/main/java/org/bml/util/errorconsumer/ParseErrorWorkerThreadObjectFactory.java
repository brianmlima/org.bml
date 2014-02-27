
package org.bml.util.errorconsumer;

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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.pool.PoolableObjectFactory;

/** An implementation of an ObjectFactory<ParseErrorWorkerThread> for the ParseError
 * handler.
 * @author BML
 */
public class ParseErrorWorkerThreadObjectFactory implements PoolableObjectFactory<ParseErrorWorkerThread> {

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
     * @throws java.lang.Exception This implementation should not throw Exception however it is necessary to complete the contract of {@link PoolableObjectFactory}
     */
    @Override
    public void destroyObject(ParseErrorWorkerThread obj) throws Exception {
        
        obj.setShouldRun(false);
        obj.interrupt();
        obj.handleDBEntry();
    }

    public boolean validateObject(ParseErrorWorkerThread obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void activateObject(ParseErrorWorkerThread obj) throws Exception {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void passivateObject(ParseErrorWorkerThread obj) throws Exception {
        throw new UnsupportedOperationException("Not supported."); 
    }
}
