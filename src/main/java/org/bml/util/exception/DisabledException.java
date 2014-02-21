
package org.bml.util.exception;

/**Exception to be thrown when a service internal or third party is disabled
 *
 * @author Brian M. Lima
 */
public class DisabledException extends Exception {

    /**
     * Creates a new instance of
     * <code>DisabledException</code> without detail message.
     */
    public DisabledException() {
    }

    /**
     * Constructs an instance of
     * <code>DisabledException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DisabledException(String msg) {
        super(msg);
    }
}
