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


package org.bml.util.errorconsumer;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.device.DeviceClass;
import org.bml.device.DeviceType;
import org.bml.util.DataContainer;
import org.bml.util.StopWatch;
import org.bml.util.sql.DBUtil;
import org.bml.util.threads.BlockingQueueWorkerThread;

/**
 * The core worker thread for use with ElasticConsumer Classes
 *
 * @author Brian M. Lima
 */
public class ParseErrorWorkerThread extends BlockingQueueWorkerThread<ParseError> {

    /**
     *
     */
    private static Log LOG = LogFactory.getLog(ParseErrorWorkerThread.class);
    /**
     *
     */
    private StopWatch timer = null;
    /**
     *
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
    public ParseErrorWorkerThread(BlockingQueue<ParseError> queueIn, long timeout, TimeUnit unit, long waitOnEmptyQueueInMills) {
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

        if (timer.getElapsedTimeSecs() > 30 || errorQueue.size() > 200) {
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
        
        
        setWorkerState(WORKER_STATE.AQUIRINGCONNECTION);

        Connection myPageViewConnection = null;

        PreparedStatement myPageViewPreparedStatement = null;

        int batchUpdateCounts[] = null, batchExecutionResults[] = null, counter = 0;

        List<DataContainer> theBatchTrackingList = null ;

        DataContainer dataContainer = null, pvData = null ;

        DeviceType aDeviceType = null;
        DeviceClass aDeviceClass = null;

        //Change to reusable map
        Map<String, String> tmpMap = null;

        //Change to StringBuilder 
        String tmpString = null;

        //theBatchTrackingList = new ArrayList<PageViewData>(dataQueue.size());

        boolean dbErrror = false;
        
        try {
            ParseError aParseError = null;
            try {
                aParseError = errorQueue.remove();
            } catch (NoSuchElementException e) {
                LOG.info("There are no ParseError Objects to push into the DB");
                return;
            }
            StopWatch connectionAge = new StopWatch();
            connectionAge.start();

            myConnection = DBUtil.getDefaultDataSource().getConnection();
            myConnection.clearWarnings();
            myConnection.setAutoCommit(false);
            myPreparedStatement = myConnection.prepareStatement(ParseErrorTable.PREPARED_INSERT_SQL);
            setWorkerState(WORKER_STATE.BATCHING);

            while (connectionAge.getElapsedTimeSecs() <= 20) {
                ParseErrorTable.populatePreparedStatement(myPreparedStatement, aParseError.toParamMap(), Boolean.FALSE);
                myPreparedStatement.addBatch();
                try {
                    aParseError = errorQueue.remove();
                } catch (NoSuchElementException e) {
                    break;
                }
            }
            myPreparedStatement.executeBatch();
            myConnection.commit();
        } catch (SQLException mySQLException) {
            //System.out.println(this.getClass().getName());
            mySQLException.printStackTrace();
        } catch (Exception myException) {
            //System.out.println(this.getClass().getName());
            myException.printStackTrace();
        } finally {
            DbUtils.closeQuietly(myPreparedStatement);
            DbUtils.closeQuietly(myConnection);
        }
    }

    @Override
    public synchronized int flush() {
        if (LOG.isInfoEnabled()) {
            LOG.info("CALLING FLUSH");
        }
        handleDBEntry();
        timer.stop();
        timer.start();
        return 1;
    }
}
