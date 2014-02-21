package org.bml.util.search;

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
