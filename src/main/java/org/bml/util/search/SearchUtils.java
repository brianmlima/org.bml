
package org.bml.util.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian M. Lima
 */
public class SearchUtils {
    private static final Log LOG = LogFactory.getLog(SearchUtils.class);
    private static final String SIMPLE_NAME = SearchUtils.class.getSimpleName();

    /**
     * @param array
     * @param lowerbound
     * @param upperbound
     * @param key
     * @return 
     */
    public static int binarySearchLower(int[] array, int lowerbound, int upperbound, int key) {
        int position;
        int comparisonCount = 1;    // counting the number of comparisons (optional)
        // To start, find the subscript of the middle position.
        position = (lowerbound + upperbound) / 2;
        while ((array[position] != key) && (lowerbound <= upperbound)) {
            comparisonCount++;
            if (array[position] > key) { // If the number is > key, ..
                upperbound = position - 1; // decrease position by one.
            } else {
                lowerbound = position + 1; // Else, increase position by one.
            }
            position = (lowerbound + upperbound) / 2;
        }
        return array[lowerbound-1] ;
    }
        
     /**
     * @param array
     * @param lowerbound
     * @param upperbound
     * @param key
     * @return 
     */
    public static Integer binarySearchLower(Integer[] array, int lowerbound, int upperbound, int key) {
        int position;
        int comparisonCount = 1;    // counting the number of comparisons (optional)
        // To start, find the subscript of the middle position.
        position = (lowerbound + upperbound) / 2;
        while ((array[position] != key) && (lowerbound <= upperbound)) {
            comparisonCount++;
            if (array[position] > key) { // If the number is > key, ..
                upperbound = position - 1; // decrease position by one.
            } else {
                lowerbound = position + 1; // Else, increase position by one.
            }
            position = (lowerbound + upperbound) / 2;
        }
        return array[lowerbound-1] ;
    }
}
