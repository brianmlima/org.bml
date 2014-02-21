package org.bml.util.search;

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

import junit.framework.TestCase;

/**A Test for the SearchUtils.class.
 * @author Brian M. Lima
 */
public class SearchUtilsTest extends TestCase {

    public SearchUtilsTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of binarySearchLower method, of class SearchUtils.
     */
    public void testBinarySearchLower_4args_1() {
        String methodName="binarySearchLower";
        System.out.println("TESTING "+methodName);
        int[] array = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18};
        int lowerbound = 0;
        int upperbound = array.length;
        int key = 11;
        int expResult = 10;
        int result = SearchUtils.binarySearchLower(array, lowerbound, upperbound, key);
        assertEquals(expResult, result);

        if (expResult != result) {
            fail(methodName+" result="+result+" expected="+expResult);
        }else{
            System.out.println("TESTED "+methodName+" SUCCESS.");
        }
    }

    /**
     * Test of binarySearchLower method, of class SearchUtils.
     */
    public void testBinarySearchLower_4args_2() {
        String methodName="binarySearchLower";
        System.out.println("TESTING "+methodName);
        Integer[] theSearchBase = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18};
        int theLowerBound = 0;
        int theUpperBound = theSearchBase.length;
        int theKey = 11;
        Integer expResult = 10;
        Integer result = SearchUtils.binarySearchLower(theSearchBase, theLowerBound, theUpperBound, theKey);
        assertEquals(expResult, result);
        if (expResult != result) {
            fail(methodName+" result="+result+" expected="+expResult);
        }else{
            System.out.println("TESTED "+methodName+" SUCCESS.");
        }
    }

}
