
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
     * An extension of the standard binary search that returns the next lowest
     * Integer in an array if the passed key does not exist in the array. If the
     * key does exist in the array it is returned.
     *
     * @param theSearchBase An array of sorted integers from lowest
     * <code>theSearchBase[0]</code> to highest
     * <code>theSearchBase[theSearchBase.length]</code>.
     * @param theLowerBound The low index bound of theSearchBase to search in.
     * This allows the search to be focused to a predefined part of
     * theSearchBase array.
     * @param theUpperBound The high index bound of theSearchBase to search in.
     * This allows the search to be focused to a predefined part of
     * theSearchBase array.
     * @param theKey The integer to search for.
     * @return
     */
    public static int binarySearchLower(int[] theSearchBase, int theLowerBound, int theUpperBound, int theKey) {
        //Argument sanity checking.
        if (theSearchBase == null) {
            throw new IllegalArgumentException("Can not operate on a null search base.");
        } else if (theSearchBase.length < 2) {
            throw new IllegalArgumentException("Can not operate on a search base with a length of less than 2.");
        } else if (theLowerBound > theUpperBound) {
            throw new IllegalArgumentException("Can not operate when the lower bound is greater than the lower bound.");
        } else if (theLowerBound == theUpperBound) {
            throw new IllegalArgumentException("Can not operate when the lower bound is equal to the lower bound.");
        }

        int comparisonCount = 1, // counting the number of comparisons (optional)
                lowerbound = theLowerBound,
                upperbound = theUpperBound,
                position = (lowerbound + upperbound) / 2; // find the subscript of the middle position.

        while ((theSearchBase[position] != theKey) && (lowerbound <= upperbound)) {
            comparisonCount++;
            if (theSearchBase[position] > theKey) { // If the number is > key, ..
                upperbound = position - 1; // decrease position by one.
            } else {
                lowerbound = position + 1; // Else, increase position by one.
            }
            position = (lowerbound + upperbound) / 2;
        }
        return theSearchBase[lowerbound - 1];
    }

    /**
     * An extension of the standard binary search that returns the next lowest
     * Integer in an array if the passed key does not exist in the array. If the
     * key does exist in the array it is returned.
     *
     * @param theSearchBase An array of sorted integers from lowest
     * <code>theSearchBase[0]</code> to highest
     * <code>theSearchBase[theSearchBase.length]</code>.
     * @param theLowerBound The low index bound of theSearchBase to search in.
     * This allows the search to be focused to a predefined part of
     * theSearchBase array.
     * @param theUpperBound The high index bound of theSearchBase to search in.
     * This allows the search to be focused to a predefined part of
     * theSearchBase array.
     * @param theKey The integer to search for.
     * @return
     */
    public static Integer binarySearchLower(final Integer[] theSearchBase, final int theLowerBound, final int theUpperBound, final int theKey) {
        //Argument sanity checking.
        if (theSearchBase == null) {
            throw new IllegalArgumentException("Can not operate on a null search base.");
        } else if (theSearchBase.length < 2) {
            throw new IllegalArgumentException("Can not operate on a search base with a length of less than 2.");
        } else if (theLowerBound > theUpperBound) {
            throw new IllegalArgumentException("Can not operate when the lower bound is greater than the lower bound.");
        } else if (theLowerBound == theUpperBound) {
            throw new IllegalArgumentException("Can not operate when the lower bound is equal to the lower bound.");
        }

        int comparisonCount = 1, // counting the number of comparisons (optional)
                lowerbound = theLowerBound,
                upperbound = theUpperBound,
                position = (lowerbound + upperbound) / 2; // find the subscript of the middle position.

        while ((theSearchBase[position] != theKey) && (lowerbound <= upperbound)) {
            comparisonCount++;
            if (theSearchBase[position] > theKey) { // If the number is > key, ..
                upperbound = position - 1; // decrease position by one.
            } else {
                lowerbound = position + 1; // Else, increase position by one.
            }
            position = (lowerbound + upperbound) / 2;
        }
        return theSearchBase[lowerbound - 1];
    }
}
