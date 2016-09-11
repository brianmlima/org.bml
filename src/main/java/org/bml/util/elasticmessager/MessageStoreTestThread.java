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
import org.bml.util.threads.WorkerThread;

/**
 *
 * @author Brian M. Lima
 */
public class MessageStoreTestThread extends WorkerThread {

    private int testInterval = 1000;

    @Override
    protected void doIt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

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
