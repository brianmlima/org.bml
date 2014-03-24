
package org.bml.util.elasticmessager;

import org.bml.util.threads.WorkerThread;

/**
 *
 * @author Brian M. Lima
 */
public class MessageStoreTestThread extends WorkerThread {
    
    private int testInterval=1000;

    @Override
    protected void doIt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the testInterval
     */
    public int getTestInterval() {
        return testInterval;
    }

    /**
     * @param testInterval the testInterval to set
     */
    public void setTestInterval(int testInterval) {
        this.testInterval = testInterval;
    }
    
}
