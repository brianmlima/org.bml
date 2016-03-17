/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import java.io.File;
import java.io.IOException;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.bml.util.exception.OverloadedException;
import org.bml.util.exception.UnavailableException;

/**
 *
 * @author Brian M. Lima
 */
public class StringToFileRTTelemetrySinkTest extends TestCase {

    public StringToFileRTTelemetrySinkTest(String testName) {
        super(testName);
    }

    final File outputToFile = new File("./StringToFileRTTelemetrySink_JUnitTest_BASIC.txt");
    final ThreadGroup theThreadGroup = new ThreadGroup("StringToFileRTTelemetrySink_JUnitTest_ThreadGroup");
    final String theThreadName = "StringToFileRTTelemetrySink_JUnitTest";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        outputToFile.deleteOnExit();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

    }

    public void testSimpleWrite() {
        System.out.println("TEST testSimpleWrite");

        StringToFileRTTelemetrySink instance = null;

        final Double maxOffersPerSecond = 100000d;
        final int bufferCapacity = 100000;

        final int offerCount = 10000;

        try {
            instance = new StringToFileRTTelemetrySink(
                    outputToFile,
                    theThreadGroup,
                    theThreadName,
                    maxOffersPerSecond,
                    bufferCapacity);
            instance.setShouldRun(true);
            instance.start();
            instance.enable();

            long permitWait = 100;
            long offerWait = 100;

            System.out.println("Tesing Write");
            for (int c = 0; c < offerCount; c++) {
                instance.offerTelemetry("Hello " + c, permitWait, offerWait, false);
            }
            System.out.println("Offered 10K telemetry strings. Checking write.");

            int lineCount = 0;

            LineIterator iter = FileUtils.lineIterator(outputToFile);
            while (iter.hasNext()) {
                iter.next();
                lineCount++;
            }
            iter.close();
            Assert.assertEquals("Number of lines written does not match", offerCount, lineCount);
            if (offerCount != lineCount) {
                fail("Lines written to file by the sink do not match the number of strings offered.");
            }
        } catch (IOException e) {
            System.out.println("IOException caught");
            fail(e.getMessage());
        } catch (OverloadedException e) {
            System.out.println("OverloadedException caught");
            fail(e.getMessage());
        } catch (UnavailableException e) {
            System.out.println("UnavailableException caught");
            fail(e.getMessage());
        } finally {
            if (instance != null) {
                instance.shutDown();
            }
            outputToFile.delete();
        }
    }

    public void testDisabled() {
        System.out.println("TEST testDisabled");

        StringToFileRTTelemetrySink instance = null;

        final Double maxOffersPerSecond = 100000d;
        final int bufferCapacity = 100000;
        final int offerCount = 10000;
        final long permitWait = 100;
        final long offerWait = 100;

        try {
            instance = new StringToFileRTTelemetrySink(
                    outputToFile,
                    theThreadGroup,
                    theThreadName,
                    maxOffersPerSecond,
                    bufferCapacity);
            instance.setShouldRun(true);
            instance.start();

            System.out.println("Tesing Disable");
            try {
                instance.offerTelemetry("Hello Disabled", permitWait, offerWait, false);
            } catch (UnavailableException e) {
                System.out.println("Expected UnavailableException caught");
                System.out.println("Passed disable");
            }

        } catch (IOException e) {
            System.out.println("IOException caught");
            fail(e.getMessage());
        } catch (OverloadedException e) {
            System.out.println("OverloadedException caught");
            fail(e.getMessage());
        } finally {
            if (instance != null) {
                instance.shutDown();
            }
            outputToFile.delete();
        }
    }

    public void testEnabledDisabledEnabled() {
        System.out.println("TEST testEnabledDisabledEnabled");

        StringToFileRTTelemetrySink instance = null;

        final Double maxOffersPerSecond = 100000d;
        final int bufferCapacity = 100000;
        final int offerCount = 10000;
        final long permitWait = 100;
        final long offerWait = 100;

        try {
            instance = new StringToFileRTTelemetrySink(
                    outputToFile,
                    theThreadGroup,
                    theThreadName,
                    maxOffersPerSecond,
                    bufferCapacity);
            instance.setShouldRun(true);
            instance.start();
            instance.enable();

            System.out.println("Tesing Enabled");
            try {
                instance.offerTelemetry("Hello enabled", permitWait, offerWait, false);
                System.out.println("Passed re-enable");
            } catch (UnavailableException e) {
                System.out.println("Unexpected UnavailableException caught");
                fail("Unexpected UnavailableException caught after disabeling and re-enable");
            }

            System.out.println("Tesing Disable");

            instance.disable();
            try {
                instance.offerTelemetry("Hello Disabled", permitWait, offerWait, false);
            } catch (UnavailableException e) {
                System.out.println("Expected UnavailableException caught");
                System.out.println("Passed disable");
            }
            System.out.println("Tesing re-enable");
            instance.enable();
            try {
                instance.offerTelemetry("Hello enabled", permitWait, offerWait, false);
                System.out.println("Passed re-enable");
            } catch (UnavailableException e) {
                System.out.println("Unexpected UnavailableException caught");
                fail("Unexpected UnavailableException caught after disabeling and re-enable");
            }

        } catch (IOException e) {
            System.out.println("IOException caught");
            fail(e.getMessage());
        } catch (OverloadedException e) {
            System.out.println("OverloadedException caught");
            fail(e.getMessage());
        } finally {
            if (instance != null) {
                instance.shutDown();
            }
            outputToFile.delete();
        }

    }

    public void testRateLimitation() {
        System.out.println("TEST testRateLimitation");

        StringToFileRTTelemetrySink instance = null;

        final Double maxOffersPerSecond = 100d;
        final int bufferCapacity = 200;
        final long permitWait = 100;
        final long offerWait = 100;

        try {
            instance = new StringToFileRTTelemetrySink(
                    outputToFile,
                    theThreadGroup,
                    theThreadName,
                    maxOffersPerSecond,
                    bufferCapacity);
            instance.setShouldRun(true);
            instance.start();
            instance.enable();

            System.out.println("Testing rate limitation.");
            //INIT first telemetry
            instance.offerTelemetry("Hello Initialize", permitWait, offerWait, false);
            //Run timed telemetry
            long sTime = System.currentTimeMillis();
            for (int c = 0; c < 201; c++) {
                instance.offerTelemetry("Hello " + c, permitWait, offerWait, false);
            }
            long eTime = System.currentTimeMillis();
            //check timing
            long totalTime = eTime - sTime;
            if (2000 > totalTime) {
                System.out.println("Rate limitation is not being properly enforced. total mills elapsed = " + totalTime);
                fail("Rate limitation is not being properly enforced");
            }
        } catch (IOException e) {
            System.out.println("Unexpected IOException caught");
            fail(e.getMessage());
        } catch (OverloadedException e) {
            System.out.println("Unexpected OverloadedException caught");
            fail(e.getMessage());
        } catch (UnavailableException e) {
            System.out.println("Unexpected UnavailableException caught");
            fail(e.getMessage());
        } finally {
            if (instance != null) {
                instance.shutDown();
            }
            outputToFile.delete();
        }

    }

    public void testFileDeleteFailureMode() {
        System.out.println("TEST testFileDeleteFailureMode");
        StringToFileRTTelemetrySink instance = null;

        final Double maxOffersPerSecond = 100d;
        final int bufferCapacity = 200;
        final long permitWait = 100;
        final long offerWait = 100;

        try {
            instance = new StringToFileRTTelemetrySink(
                    outputToFile,
                    theThreadGroup,
                    theThreadName,
                    maxOffersPerSecond,
                    bufferCapacity);
            instance.setShouldRun(true);
            instance.start();
            instance.enable();

            System.out.println("Offering init telemetry.");

            instance.offerTelemetry("Hello Initialize", permitWait, offerWait, false);

            System.out.println("Sleeping to allow for write.");
            //allow plenty of time for write to occur
            Thread.sleep(200);
            System.out.println("Removing output file.");
            //simulate output file deleted accidently.
            /**
            boolean delete = outputToFile.delete();
            System.out.println("FILE DELETED="+delete+" EXISTS = "+outputToFile.exists());
            System.out.println("Attempting offer that should fail");
            instance.offerTelemetry("Hello Initialize", permitWait, offerWait, false);
            if (outputToFile.exists()) {
                fail();
            }
            */

        } catch (IOException e) {
            System.out.println("Unexpected IOException caught");
            fail(e.getMessage());
        } catch (OverloadedException e) {
            System.out.println("Unexpected OverloadedException caught");
            fail(e.getMessage());
        } catch (UnavailableException e) {
            System.out.println("Unexpected UnavailableException caught");
            fail(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Unexpected InterruptedException caught");
            fail(e.getMessage());
        } finally {
            if (instance != null) {
                instance.shutDown();
            }
            outputToFile.delete();
        }

    }

}
