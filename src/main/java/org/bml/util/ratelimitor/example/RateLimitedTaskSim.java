package org.bml.util.ratelimitor.example;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2008 - 2014 Brian M. Lima
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

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.exception.DisabledException;
import org.bml.util.exception.UnavailableException;
import org.bml.util.ratelimitor.RateExceededException;
import org.bml.util.ratelimitor.RateLimitedTask;
import org.bml.util.threads.WorkerThread;

/**
 * A simulated {@link RateLimitedTask}
 *
 * @author Brian M. Lima
 */
public class RateLimitedTaskSim extends WorkerThread implements RateLimitedTask<Integer, Date> {

    private final Log LOG;
    private final int cap;
    private boolean isAvailable;
    private boolean isDisabled;
    private final Object LOCK = new Object();
    private final int maxSleep = 120;
    public final AtomicInteger counter = new AtomicInteger(0);
    private int second = 0;
    private final Random rand;

    public RateLimitedTaskSim(ThreadGroup theThreadGroup, String theThreadName) {
        super(theThreadGroup, theThreadName);
        this.rand = new Random();
        this.isDisabled = false;
        this.isAvailable = true;
        this.LOG = LogFactory.getLog(RateLimitedTaskSim.class);
        this.setShouldRun(true);
        cap = 200;
    }

    /**
     *
     * @param callConfiguration a Date to calculate the number of days since
     * 1970
     * @param timeout the number of milliseconds to process before timing out
     * @return The number of days since 1970
     * @throws RateExceededException if rate of 1000 per second is exceeded
     * @throws DisabledException Simulated service unavailable exception
     * @throws UnavailableException
     * @throws InterruptedException
     */
    public Integer attemptCall(Date callConfiguration, long timeout) throws RateExceededException, DisabledException, UnavailableException, InterruptedException {
        if (this.isDisabled) {//simulate Disabled
            throw new DisabledException(this.getClass().getName() + " service disabled.");
        }
        if (!this.isAvailable) {//simulate unavailable
            throw new UnavailableException(this.getClass().getName() + " service unavailable.");
        }

        int currentSecond = (int) ((System.currentTimeMillis() / 1000L) % 60L);
        if (currentSecond != second) {
            synchronized (LOCK) { //Sync on second change
                if (currentSecond != second) {//check if different still after lock obtained
                    counter.set(0);
                    second = currentSecond;
                }
            }
        }
        synchronized (LOCK) { //Sync on second change
            if (counter.get() >= cap) {
                throw new RateExceededException("Rate of " + cap + " exceeded", 1000);
            }
            counter.incrementAndGet();
        }
        int days = (int) TimeUnit.MILLISECONDS.toDays(callConfiguration.getTime());
        long opTime = rand.nextInt(this.maxSleep);
        if (opTime > timeout) {//simulate time out
            sleep(timeout);
            throw new InterruptedException("AttemptCall took longer than timeout=" + timeout);
        } else {
            if (rand.nextBoolean()) {//random wait
                sleep(opTime);//simulate operation time.
            }
            return days;//incur boxing
        }
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    protected void doIt() {
        while (this.getShouldRun()) {
            if (rand.nextInt(10) == 0) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Simulating service Unavailable");
                }
                this.isAvailable = false;
                try {
                    sleep(5000);
                } catch (InterruptedException ex) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("InterruptedException caught while simulating Unavailable service state.", ex);
                    }
                }
                if (LOG.isInfoEnabled()) {
                    LOG.info("Simulating service Available");
                }
                this.isAvailable = true;
                continue;
            }
            if (rand.nextInt(5) == 0) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Simulating service ");
                }
                this.isDisabled = true;
                try {
                    sleep(5000);
                } catch (InterruptedException ex) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("InterruptedException caught while simulating Disabled service state.", ex);
                    }
                }
                if (LOG.isInfoEnabled()) {
                    LOG.info("Simulating service Enabled");
                }
                this.isDisabled = false;
                continue;
            }

        }
    }
}
