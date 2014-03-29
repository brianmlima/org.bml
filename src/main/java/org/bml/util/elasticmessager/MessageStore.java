
package org.bml.util.elasticmessager;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2014 Brian M. Lima
 * %%
 * This file is part of ORG.BML.
 * 
 *     ORG.BML is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     ORG.BML is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with ORG.BML.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bml.util.threads.WorkerThread;

/**
 * <p>
 *A MessageStore is an abstract class that models a message storage system that
 * can be written to by concurrently. This model focuses on speed and the idea 
 * that it is ok to drop messages on the floor when not doing so could result in
 * use of all available memory and or disk space. The class was inspired by an 
 * implementation for a real time bidder logging system. In this case system 
 * criticle logging (IE: billing) was handled by a different system and 
 * application logging for data mining purposes is implemented as best effort 
 * but non-criticle.
 * </p>
 * <p>
 * This results in choices for speed above all, even when it could result in 
 * data loss if a part of the system fails.
 * </p>
 * 
 * @author Brian M. Lima
 */
public abstract class MessageStore< M extends ElasticMessage<T>,T> extends WorkerThread {
    
    private int testInterval=1000;
    /**
     * A thread that sleeps for testInterval, tests this MessageStore and 
     * updates the available flag accordingly.
     */
    private final WorkerThread messageStoreTestThread=null;    
    
    /**
     * 
     */
    private final AtomicBoolean available=new AtomicBoolean();

    /**
     *  
     * @param anElasticMessage
     * @return 
     */
    public abstract boolean write(M anElasticMessage);
    
    /**
     *
     * @param anElasticMessageSet
     * @return
     */
    public abstract int bulkWrite(Set<M> anElasticMessageSet);
    
    /**
     * 
     * @return true if successfully closed, false otherwise.
     */
    public abstract boolean close();
    
    /**
     * Returns true if the MessageStore is available.
     * NOTE: This value is cached and updated when a call to 
     * {@link MessageStore#test()} is made. There is also a 
     * 
     * @return true if this message storage system is available.
     */
    public boolean isAvailable(){
        return available.get();
    }
    
    /**
     * Performs an active test of the Message Storage system and returns a 
     * boolean true if the system is capable of writing messages, false 
     * otherwise.
     * 
     * @return true on success, false otherwise.
     */
    public abstract boolean test() ;

    /**
     * @return the testInterval
     */
    public int getTestInterval() {
        return testInterval;
    }

    /**
     * @param testInterval the testInterval to set
     */
    public void setTestInterval(int testInterval) {
        this.testInterval = testInterval;    
    }
}
