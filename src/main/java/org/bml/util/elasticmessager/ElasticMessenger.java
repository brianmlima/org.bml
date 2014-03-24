
package org.bml.util.elasticmessager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * The ElasticMessenger Allows multiple threads to write messages. Multiple 
 * threads read from the queue batch up messages if possible and writes to a
 * MessageStore implementation.
 * 
 * 
 * @author Brian M. Lima
 * @param <T>
 */
public class ElasticMessenger<T> {
   private final BlockingQueue<T> queueIn = new SynchronousQueue<T>();
    
    
    
    
}
