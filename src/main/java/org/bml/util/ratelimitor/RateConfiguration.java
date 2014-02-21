package org.bml.util.ratelimitor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 *{@link RateConfiguration} used by RateController objects to manage and enforce
 * time based call rate limits.
 * 
 * @author Brian M. Lima
 */
public class RateConfiguration {
  
  public final AtomicLong callsPerSecond;
  
  //Togles to make the call anyway on rate exceeded. This functionality is 
  //handy for testing and for situations where you want to use the Rate reporting 
  //functionality but do not want to enforce the rate Cap.
  private AtomicBoolean enforceRateCap=new AtomicBoolean(true);
  
  /**
   * Toggles the tracking of call times using {@link org.apache.commons.math3.stat.descriptive.SummaryStatistics}
   */
  private AtomicBoolean trackStats=new AtomicBoolean(true);
  
  public RateConfiguration(final long callsPerSecond){
      //Set up calls per second
      this.callsPerSecond=new AtomicLong(callsPerSecond);
      
  }
  
}
