package org.bml.util.ratelimitor;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
import org.bml.util.exception.DisabledException;
import org.bml.util.exception.UnavailableException;

/**
 * {@link RateControler} handles tasks associated with rate limiting. NOTE: The
 * {@link RateControler} is optomised to run on a second based TimeUnit all
 * metrics are reduced to per-second.
 *
 * @author Brian M. Lima
 */
public class RateControler<R, C> implements RateLimitedTask<R, C> {

    /**
     * Standard logging.
     */
    private static final Log LOG = LogFactory.getLog(RateControler.class);

    private final RateLimitedTask<R, C> theRateLimitedTask;
    private final RateConfiguration config;
    private final AtomicInteger theLastSecond = new AtomicInteger(-1);
    private final RateControlerData data;

    private Object SECOND_UPDATE_LOCK = new Object();
    private Object CALL_UPDATE_LOCK = new Object();

    public RateControler(final RateConfiguration theRateConfiguration, final RateLimitedTask<R, C> theRateLimitedTask) {
        this.theRateLimitedTask = theRateLimitedTask;
        this.config = theRateConfiguration;
        this.data = new RateControlerData(theRateConfiguration);
    }

    private String getLogPrefixMsg(final int currentSecond) {
        return "THREAD_GROUP=" + Thread.currentThread().getThreadGroup().getName() + " THREAD_ID=" + Thread.currentThread().getId() + " CURRENT_SECOND=" + currentSecond;
    }

    /**
     *
     * @param callConfiguration C an object containing all the necessary
     * information to call the theRateLimitedTask's attempt call method.
     * @param timeout the task timeout passed to theRateLimitedTask
     * @return R the result of theRateLimitedTask
     * @throws RateExceededException if call rate per second has been exceeded.
     * @throws DisabledException The implementation of theRateLimitedTask throws
     * it.
     * @throws UnavailableException The implementation of theRateLimitedTask
     * throws it.
     * @throws InterruptedException The implementation of theRateLimitedTask
     * throws it.
     */
    public R attemptCall(C callConfiguration, long timeout) throws RateExceededException, DisabledException, UnavailableException, InterruptedException {
        
        
        
        final long startTime=System.currentTimeMillis()/1000L;
        
        //final long startTime = Math.floor(((double)/1000d));

        int currentSecond = (int) (startTime % 60L);
        int lastSecond = (currentSecond == 0) ? 60 : currentSecond;

        //Check for firstRun
        //if (!theLastSecond.compareAndSet(-1, currentSecond)) {//check if not first run
          //  if (LOG.isTraceEnabled()) {
          //      LOG.trace(getLogPrefixMsg(currentSecond) + " First attempt for this RateControler");
            //}
            if (theLastSecond.compareAndSet(((currentSecond==0) ? 60 : currentSecond - 1), currentSecond)) { //if second has changed
                if (LOG.isTraceEnabled()) {
                    LOG.trace(getLogPrefixMsg(currentSecond) + " The Second Has Changed. GETTING SECOND_UPDATE_LOCK.");
                }
                synchronized (SECOND_UPDATE_LOCK) {//grab second update lock
                    if (LOG.isTraceEnabled()) {
                        LOG.trace(getLogPrefixMsg(currentSecond) + " HAVE SECOND_UPDATE_LOCK.");
                    }
                    //check to see if second changed while grabbing lock
                    if (theLastSecond.compareAndSet(((currentSecond==0) ? 60 : currentSecond - 1), currentSecond)) { //if second has changed
                        if (LOG.isTraceEnabled()) {
                            LOG.trace(getLogPrefixMsg(currentSecond) + " Second has changed. HAVE SECOND_UPDATE_LOCK. GETTING CALL_UPDATE_LOCK");
                        }
                        //grab call update lock and reset to 0
                        synchronized (CALL_UPDATE_LOCK) {
                            if (LOG.isTraceEnabled()) {
                                LOG.trace(getLogPrefixMsg(currentSecond) + " Reseting Second Tracking. HAVE SECOND_UPDATE_LOCK,CALL_UPDATE_LOCK.");
                            }
                            data.callCapStats[currentSecond].set(0); //reset call count tracking
                            data.mathStats[currentSecond].clear(); //clear stats tracking
                            if (LOG.isTraceEnabled()) {
                                LOG.trace(getLogPrefixMsg(currentSecond) + " RELEASING CALL_UPDATE_LOCK.");
                            }
                        }
                    }
                    if (LOG.isTraceEnabled()) {
                        LOG.trace(getLogPrefixMsg(currentSecond) + " RELEASING SECOND_UPDATE_LOCK.");
                    }

                }
            }
        //}
        if (LOG.isTraceEnabled()) {
            LOG.trace(getLogPrefixMsg(currentSecond) + " GETTING CALL_UPDATE_LOCK.");
        }
        //Grab lock and check / update call count
        synchronized (CALL_UPDATE_LOCK) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(getLogPrefixMsg(currentSecond) + " HAVE CALL_UPDATE_LOCK.");
            }
            if (data.callCapStats[currentSecond].get() >= config.callsPerSecond.get()) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace(getLogPrefixMsg(currentSecond) + "Rate " + config.callsPerSecond.intValue() + " Exceeded at " + data.callCapStats[currentSecond].get() + ". Throwing RateExceededException. HAVE CALL_UPDATE_LOCK.");
                }
                throw new RateExceededException("Rate limit of " + config.callsPerSecond.intValue() + " exceeded.", -1);
            } else {
                if (LOG.isTraceEnabled()) {
                    LOG.trace(getLogPrefixMsg(currentSecond) + "Incrementing call cap. HAVE CALL_UPDATE_LOCK.");
                }
                data.callCapStats[currentSecond].incrementAndGet();
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace(getLogPrefixMsg(currentSecond) + "Releasing CALL_UPDATE_LOCK.");
            }
        }
        //try call
        try {
            return this.theRateLimitedTask.attemptCall(callConfiguration, timeout);
        } finally {
            //Set call time in mills
            data.mathStats[currentSecond].addValue((System.currentTimeMillis()/1000L) - startTime);
        }
    }

    public boolean isAvailable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

/**
 *
 */
class RateControlerData {

    private final RateConfiguration theRateConfiguration;
    //Used when in bare tracking mode
    final AtomicInteger[] callCapStats;
    //Used when in stats tracking mode.
    final SummaryStatistics mathStats[];

    RateControlerData(final RateConfiguration theRateConfiguration) {
        this.theRateConfiguration = theRateConfiguration;
        this.callCapStats = new AtomicInteger[60];
        for (int c = 0; c < 60; c++) {
            callCapStats[c] = new AtomicInteger(0);
        }

        this.mathStats = new SynchronizedSummaryStatistics[60];

        for (int c = 0; c < 60; c++) {
            mathStats[c] = new SynchronizedSummaryStatistics();
        }

    }

}
