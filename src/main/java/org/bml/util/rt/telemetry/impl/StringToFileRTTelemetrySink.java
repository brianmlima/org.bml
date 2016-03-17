
package org.bml.util.rt.telemetry.impl;

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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.exception.UnavailableException;
import org.bml.util.rt.telemetry.RTTelemetrySink;

/**
 *
 * @author Brian M. Lima
 * 
 * 
 * @todo This class can block on offer indefinitely if the out file is removed 
 * during operation. For some reason pushing the write operation into a callable
 * and using the executor service still blocks the Future object. 
 * 
 */
public class StringToFileRTTelemetrySink extends RTTelemetrySink<String> {

    private static final String NL = System.getProperty("line.separator");

    /**
     *
     */
    private final Log LOG = LogFactory.getLog(StringToFileRTTelemetrySink.class);

    final File outFile;
    final BufferedWriter outBufferedWriter;

    public StringToFileRTTelemetrySink(
            final File outputToFile,
            final ThreadGroup theThreadGroup,
            final String theThreadName,
            final Double maxOffersPerSecond,
            final int bufferCapacity
    ) throws FileNotFoundException, IOException {
        super(
                theThreadGroup,
                theThreadName,
                maxOffersPerSecond,
                bufferCapacity,
                false
        );
        this.outFile = outputToFile;
        this.outBufferedWriter = new BufferedWriter(new FileWriter(this.outFile));
        super.setLogName("StringToFileRTTelemetrySink " + theThreadName);
    }

    private boolean thouroughFileExists() {
        FileInputStream in = null;
        try {
            in = new FileInputStream(this.outFile);
            in.read();
            return true;

        } catch (Exception e) {
            return false;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    protected void flushData() {
        if (!thouroughFileExists()) {
            this.shutDown("The ouptput file at " + outFile.getAbsolutePath() + " no longer exists. Shutting down to avoid never ending blocking io.");
            return;
        }

        if (!telemetryBuffer.isEmpty()) {

            //Executors.newCachedThreadPool();
            ExecutorService executor = Executors.newFixedThreadPool(1);

            Callable<Boolean> task = new Callable<Boolean>() {
                @Override
                public Boolean call() throws UnavailableException {
                    final List<String> list = new LinkedList<String>();
                    telemetryBuffer.drainTo(list);
                    int written = 0;

                    System.out.println("Attempting to write to output buffer");

                    for (String s : list) {
                        if(!outFile.exists()){
                            return false;
                        }
                        try {
                            outBufferedWriter.append(s);
                            outBufferedWriter.append(NL);
                            written++;
                        } catch (IOException ex) {
                            if (LOG.isErrorEnabled()) {
                                LOG.error("IOException caught while writing to File " + outFile.getAbsolutePath(), ex);
                            }
                        }
                    }
                    System.out.println("Attempting to flush output buffer");
                    try {
                        if(!outFile.exists()){
                            return false;
                        }
                        outBufferedWriter.flush();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        if (LOG.isErrorEnabled()) {
                            LOG.error("IOException caught while flushing to File " + outFile.getAbsolutePath(), ex);
                        }
                    }
                    //Check if all telemetry was written and act accordingly.
                    if (written != list.size()) {
                        //handle fail
                        return false;
                    } else {
                        return true;
                    }

                }
            };

            Future<Boolean> future = executor.submit(task);
            try {
                System.out.println("executing get");
                Boolean result = future.get(5, TimeUnit.SECONDS);
                System.out.println("executing get finished result==" + result);
                if(result==false){
                    this.shutDown("An attemted write failed.");
                }
                
                LOG.info(result);
            } catch (TimeoutException e) {
                LOG.fatal("TimeoutException caught while calling flushData with timeout", e);
            } catch (InterruptedException e) {
                LOG.fatal("ExecutionException caught while calling flushData with timeout", e);
            } catch (ExecutionException e) {
                LOG.fatal("ExecutionException caught while calling flushData with timeout", e);
            } finally {
                future.cancel(true);
            }
            System.out.println("Still going!");
        }
    }
}
