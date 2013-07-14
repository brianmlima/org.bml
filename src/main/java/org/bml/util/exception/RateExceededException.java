
package org.bml.util.exception;

/**
 *
 * @author brianmlima
 */
public class RateExceededException extends Exception {

    /**
     * Creates a new instance of
     * <code>RateExceededException</code> without detail message.
     */
    public RateExceededException() {
    }

    /**
     * Constructs an instance of
     * <code>RateExceededException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public RateExceededException(String msg) {
        super(msg);
    }
}
