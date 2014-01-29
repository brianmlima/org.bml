/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bml.util.rt.telemetry;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2008 - 2013 Brian M. Lima
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
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author brianmlima
 */
public class SecondBasedCounterTest extends TestCase {

  private SecondBasedCounter counter;

  public SecondBasedCounterTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    counter = new SecondBasedCounter(60, "SecondBasedCounter");
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    counter = null;
  }

  /**
   * Test of increment method, of class SecondBasedCounter.
   */
  public void testIncrement() {
    System.out.println("increment");
    SecondBasedCounter instance = null;
    int limit = 10000000;
    for (int c = 0; c < limit; c++) {
      counter.increment();
    }
    System.out.println("Total useage is " + counter.getNumOperations());
    System.out.println("Totoal Expected usage is " + limit);
    assertEquals(counter.getNumOperations(), limit);
  }

  /**
   * Test of getLastMinutesTelemetry method, of class SecondBasedCounter.
   */
  public void testGetLastMinutesTelemetry() {
    System.out.println("getLastMinutesTelemetry");
    DescriptiveStatistics stats = counter.getLastMinutesTelemetry();


    long max = stats.getN();

    AtomicInteger[] counterArray=counter.getCounterArray();

    for (int index = 0; index < max; index++) {
      System.out.println(stats.getElement(index)+" -- "+counterArray[index].get());

    }




  }

  /**
   * Test of getCurrentSecondID method, of class SecondBasedCounter.
   */
  public void testGetCurrentSecondID() {
    System.out.println("getCurrentSecondID");
    //int expResult = 0;
    //int result = SecondBasedCounter.getCurrentSecondID();
    //assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    //fail("The test case is a prototype.");
  }
}
