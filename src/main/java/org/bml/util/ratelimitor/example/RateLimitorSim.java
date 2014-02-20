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
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.bml.util.exception.DisabledException;
import org.bml.util.exception.UnavailableException;
import org.bml.util.ratelimitor.RateConfiguration;
import org.bml.util.ratelimitor.RateControler;
import org.bml.util.ratelimitor.RateExceededException;
import org.bml.util.threads.WorkerThread;

/**
 *
 * @author Brian M. Lima
 */
public class RateLimitorSim extends WorkerThread {

    private static final Log LOG = LogFactory.getLog(RateLimitorSim.class);

    public static void main(String args[]) {

// help log4j        
        String logPropPath = "/opt/projects/orgbml/src/main/resources/conf/log4j.xml";
        System.setProperty("log4j.configuration", logPropPath);
        DOMConfigurator.configureAndWatch(System.getProperty("log4j.configuration"));   
        ThreadGroup aThreadGroup = new ThreadGroup("Simulation-RateLimitor-Group");
        RateLimitorSim sim = new RateLimitorSim(aThreadGroup, "Simulated-RateLimitor");
        LOG.info("Starting Rate Limitor Simulation");
        sim.start();
    }

    private final RateLimitedTaskSim task;
    private final RateConfiguration config;
    private final RateControler<Integer, Date> controler;
    private final Set<LoadSimulator> loadProducers;

    public RateLimitorSim(ThreadGroup theThreadGroup, String theThreadName) {
        super(theThreadGroup, theThreadName);
        this.setShouldRun(true);
        this.task = new RateLimitedTaskSim(theThreadGroup, "Simulated-RateLimitedTask");
        this.config = new RateConfiguration(300);
        this.controler = new RateControler<Integer, Date>(config, task);
        this.loadProducers = new HashSet<LoadSimulator>();

        ThreadGroup loadProducerThreadGroup = new ThreadGroup("LoadProducer-Group");

        for (int c = 0; c < 10; c++) {
            this.loadProducers.add(new LoadSimulator(loadProducerThreadGroup, "LoadProducer-" + c, this.controler));
        }
    }

    @Override
    public synchronized void start() {
        super.start();
        for (LoadSimulator worker : this.loadProducers) {
            worker.start();
        }
    }

    @Override
    protected void doIt() {
        int total = 0;
        int rate = 0;
        int disabled = 0;
        int unavailable = 0;
        int interupted = 0;
        boolean stopSim = true;
        while (this.getShouldRun()) {
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            total = 0;
            rate = 0;
            disabled = 0;
            unavailable = 0;
            interupted = 0;
            for (LoadSimulator worker : this.loadProducers) {
                total += worker.total;
                rate += worker.rate;
                disabled += worker.disabled;
                unavailable += worker.unavailable;
                interupted += worker.interupted;
            }
            LOG.info("TOTAL=" + total + " PER_SEC=" + this.task.counter.get() + " Exceptions Caught RATE=" + rate + " DISABLED=" + disabled + " UNAVAILABLE=" + unavailable + " INTERRUPTED=" + interupted);

            stopSim = true;
            for (LoadSimulator worker : this.loadProducers) {
                if (worker.isAlive()) {
                    stopSim = false;
                }
            }
            if (stopSim) {
                this.setShouldRun(false);
                LOG.info("FINAL TOTAL=" + total + " PER_SEC=" + this.task.counter.get() + " Exceptions Caught RATE=" + rate + " DISABLED=" + disabled + " UNAVAILABLE=" + unavailable + " INTERRUPTED=" + interupted);
            }
        }

        LOG.info("Calling Shutdown.");
        for (LoadSimulator worker : this.loadProducers) {
            worker.setShouldRun(false);
        }
        ((WorkerThread) this.task).setShouldRun(false);
        this.setShouldRun(false);

    }

}

class LoadSimulator extends WorkerThread {

    private Log LOG = LogFactory.getLog(LoadSimulator.class);

    private RateControler<Integer, Date> control;
    int total = 0;
    int rate = 0;
    int disabled = 0;
    int unavailable = 0;
    int interupted = 0;

    public LoadSimulator(ThreadGroup theThreadGroup, String theThreadName, RateControler<Integer, Date> control) {
        super(theThreadGroup, theThreadName);
        this.control = control;
        this.setShouldRun(true);
    }

    @Override
    protected void doIt() {
        LOG.info("Running Tests");
        for (int c = 0; c < 10000; c++) {
            total++;
            try {
                control.attemptCall(new Date(), 1000);
            } catch (RateExceededException ex) {
                rate++;
            } catch (DisabledException ex) {
                disabled++;
            } catch (UnavailableException ex) {
                unavailable++;
            } catch (InterruptedException ex) {
                interupted++;
            }
        }
        LOG.info("Shutting down");
        this.setShouldRun(false);
    }
}
