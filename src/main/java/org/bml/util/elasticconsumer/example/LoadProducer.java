
package org.bml.util.elasticconsumer.example;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bml.util.threads.WorkerThread;
import org.bml.util.elasticconsumer.ElasticConsumer;

/**Load producer for testing ElasticConsumer<ProcData> behavior
 * @author Brian M. Lima
 */
public class LoadProducer extends WorkerThread {

    /*The ElasticConsumer to place a load on*/
    private ElasticConsumer<ProcData> anElasticConsumer = null;
    /*The total succesfull offers to make*/
    private int doTotal = 1000000;
    /*The total succesfull offers made*/
    private int doneTotal = 0;

    /**Creates a new LoadProducer for ElasticConsumer<ProcData>  testing.
     * @param aElasticConsumer a ElasticConsumer<ProcData> to place a load on.
     */
    public LoadProducer(ElasticConsumer<ProcData> aClientLimitor) {
        super();
        this.anElasticConsumer = aClientLimitor;
    }

    /**Override of Thread.start();
     * Checks aElasticConsumer.isAlive() and sets shoudlRun appropriately
     */
    @Override
    public synchronized void start() {
        if (anElasticConsumer.isAlive()) {
            setShouldRun(true);
            super.start();
        } else {
            setShouldRun(false);
        }
    }

    /**Overrides WorkerThread.doIt();
     * This is the guts of the thread;
     */
    @Override
    protected void doIt() {
        //check if aElasticConsumer is running.
        while (anElasticConsumer.isAlive()) {
            try {
                //Attempt an offer. aElasticConsumer should log apropriately for testing
                if (anElasticConsumer.offer(new ProcData(), 10, TimeUnit.SECONDS)) {
                    doneTotal++;
                }
            } catch (InterruptedException ex) {
                //Log and handle interruption with apropriate setShouldRun(false);
                Logger.getLogger(ElasticConsumerTest.class.getName()).log(Level.SEVERE, null, ex);
                setShouldRun(false);
            }
            //check aElasticConsumer.isAlive() again just in case it shutdown while I was working.
            if (!anElasticConsumer.isAlive()) {
                this.setShouldRun(false);
            }
            //Check and handle shutdown after test has completed
            if (this.doTotal == this.doneTotal) {
                this.setShouldRun(false);
            }
        }

    }

    /**Overrides WorkerThread.doShutdown() to add some logging for test clarity.*/
    @Override
    protected void doShutdown() {
        System.out.println("LoaderProducer: " + this.getId() + " Shutting Down");
    }
}
