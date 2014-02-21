package org.bml.util.exception;

/**Thrown when a service is overloaded. There is a distinct difference between
 * RateExceeded which is when a third party imposed limit is reached and this 
 * exception which is used to describe a condition where the service being 
 * called can not keep up with the demand and is backing up, causing RT 
 * operations to time out. This exception should usually be followed by a 
 * refactoring of the service to batch.
 *
 * @author Brian M. Lima
 */
public class OverloadedException extends Exception {

  /**
   * Creates a new instance of
   * <code>OverloadedException</code> without detail message.
   */
  public OverloadedException() {
  }

  /**
   * Constructs an instance of
   * <code>OverloadedException</code> with the specified detail message.
   *
   * @param msg the detail message.
   */
  public OverloadedException(String msg) {
    super(msg);
  }
}
