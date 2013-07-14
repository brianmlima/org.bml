
package org.bml.util.ratelimitor;

/**
 *
 * @author brianmlima
 */
public class OverRateException extends Exception {

    /**
     * Creates a new instance of
     * <code>OverRateException</code> without detail message.
     */
    public OverRateException() {
    }

    /**
     * Constructs an instance of
     * <code>OverRateException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public OverRateException(String msg) {
        super(msg);
    }
}
