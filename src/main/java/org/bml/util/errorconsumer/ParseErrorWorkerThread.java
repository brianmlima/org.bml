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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.sql.DBUtil;
import org.bml.util.threads.BlockingQueueWorkerThread;

/**
 * An extension of {@link BlockingQueueWorkerThread} charged with 
 * pulling {@link ParseError} objects from a {@link BlockingQueue} and storing them in 
 * 
 * 
 * The core worker thread for use with ElasticConsumer Classes
 *
 *
 * 
 * TODO: Dump to /tmp/error_consumer/ on SQLException and recover
 * 
 * @see ParseError
 * @see BlockingQueueWorkerThread
 * @author Brian M. Lima
 */
public class ParseErrorWorkerThread extends BlockingQueueWorkerThread<ParseError> {

    /**
     * Standard Commons {@link Log}
     */
    private static Log LOG = LogFactory.getLog(ParseErrorWorkerThread.class);

    /**
     * A {@link StopWatch} utility for handling timing
     */
    private StopWatch timer = null;
    
    /**
     * The {@link Queue} of {@link ParseError} objects to be stored
     */
    private Queue<ParseError> errorQueue = new LinkedList<ParseError>();


    
    
    
    
    
    /**
     * Creates a new BlockingQueueWorkerThread.
     *
     * @param queueIn The BlockingQueue<T> for worker threads to poll.
     * @param timeout The worker threads poll timeout.
     * @param unit The worker threads poll timeout TimeUnit.
     * @param waitOnEmptyQueueInMills The worker threads sleep time on an empty
     * queue.
     */
    public ParseErrorWorkerThread(final BlockingQueue<ParseError> queueIn, final long timeout, final TimeUnit unit, final long waitOnEmptyQueueInMills) {
        super(queueIn, timeout, unit, waitOnEmptyQueueInMills);
        timer = new StopWatch();
        timer.start();
    }

    @Override
    public void run() {
        timer.start();
        super.run();
        handleDBEntry();
    }

    @Override
    protected void doIt(ParseError obj) {

        boolean result = this.errorQueue.add(obj);

        if (!result && LOG.isWarnEnabled()) {
            LOG.warn("UNABLE TO ADD ParseError to internal errorQueue");
        }

        if ((timer.getTime() / 1000) > 30 || errorQueue.size() > 200) {
            handleDBEntry();
            timer.stop();
            timer.start();
        }
    }

    /**
     * TOOD: Add a temp ordered list to store ParseError objects as they are
     * taken from the queue and log if any rows are rejected by the DB server
     * TODO: abstract out the handleDBEntry base logic and use <T> for entry and
     * a static method for marshaling into a Prepared Statement (Consider adding
     * the marshal method to a TABLE definition object).
     */
    public void handleDBEntry() {

        Connection myConnection = null;
        PreparedStatement myPreparedStatement = null;

        Connection myPageViewConnection = null;

        int batchExecutionResults[] = null;

        List<ParseError> theBatchTrackingList = new LinkedList<ParseError>();

        //DeviceType aDeviceType = null;
        //DeviceClass aDeviceClass = null;

        //Change to reusable map
        Map<String, String> tmpMap = null;

        //Change to StringBuilder 
        //String tmpString = null; 

        //theBatchTrackingList = new ArrayList<PageViewData>(dataQueue.size());
        boolean dbErrror = false;

        try {
            ParseError aParseError = null;
            try {
                aParseError = errorQueue.remove();
                theBatchTrackingList.add(aParseError);
            } catch (NoSuchElementException e) {
                LOG.info("There are no ParseError Objects to push into the DB");
                return;
            }
            StopWatch connectionAge = new StopWatch();
            connectionAge.start();
            setWorkerState(WORKER_STATE.AQUIRING_CONNECTION);
            myConnection = DBUtil.getDefaultDataSource().getConnection();
            setWorkerState(WORKER_STATE.CONFIGURING_CONNECTION);
            myConnection.clearWarnings();
            myConnection.setAutoCommit(false);
            setWorkerState(WORKER_STATE.PREPARING_SQL);
            myPreparedStatement = myConnection.prepareStatement(ParseErrorTable.PREPARED_INSERT_SQL);
            setWorkerState(WORKER_STATE.BATCHING);

            while ((connectionAge.getTime() / 1000) <= 20) {
                ParseErrorTable.populatePreparedStatement(myPreparedStatement, aParseError.toParamMap(), Boolean.FALSE);
                myPreparedStatement.addBatch();
                try {
                    aParseError = errorQueue.remove();
                    theBatchTrackingList.add(aParseError);
                } catch (NoSuchElementException e) {
                    break;
                }
            }

            this.setWorkerState(WORKER_STATE.EXECUTING_BATCH);
            batchExecutionResults=myPreparedStatement.executeBatch();
            
            myConnection.commit();
            
            this.setWorkerState(WORKER_STATE.VERIFYING_BATCH);
            if(batchExecutionResults.length!=theBatchTrackingList.size()){
                
            }

            
        } catch (SQLException sqle) {
            if (LOG.isFatalEnabled()) {
                LOG.fatal("SQLException caught. The ErrorConsumer is unable to push data to a database. ParseErrors will be dumped to /tmp/error_consumer/", sqle);
            }
        } catch (Exception e) {
            if (LOG.isFatalEnabled()) {
                LOG.fatal("Exception caught. The ErrorConsumer is unable to push data to a database. Errors will be dumped to /tmp/error_consumer/", e);
            }
            
        } finally {
            DbUtils.closeQuietly(myPreparedStatement);
            DbUtils.closeQuietly(myConnection);
        }
    }

    @Override
    public synchronized int flush() {
        this.setWorkerState(WORKER_STATE.FLUSHING);
        if (LOG.isInfoEnabled()) {
            LOG.info("CALLING FLUSH");
        }
        handleDBEntry();
        timer.stop();
        timer.start();
        return 1;
    }
}
