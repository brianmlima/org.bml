package org.bml.util.ratelimitor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/*
 * #%L
 * orgbml
 * %%
 * Copyright (C) 2008 - 2013 Brian M. Lima
 * %%
 * This file is part of org.bml.
 * 
 * org.bml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.bml is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with org.bml.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
