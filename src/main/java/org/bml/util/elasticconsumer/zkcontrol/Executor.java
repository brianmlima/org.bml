
package org.bml.util.elasticconsumer.zkcontrol;

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
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.bml.util.EncodingUtils;
import org.bml.util.elasticconsumer.ElasticConsumer;
import org.bml.util.threads.WorkerThread;
import org.bml.util.zoo.DataMonitorListener;


/**
 * @author Brian M. Lima
 */
public class Executor extends WorkerThread implements Watcher, DataMonitorListener {

    private static Log LOG = LogFactory.getLog(Executor.class);
    private String zNode;
    private DataMonitor theDataMonitor;
    private ZooKeeper theZookeeper;
    private ElasticConsumer theElasticConsumer;

    /**
     * @param hostPort
     * @param znode
     * @param theElasticConsumer
     * @throws KeeperException
     * @throws IOException 
     */
    public Executor(String hostPort, String znode, ElasticConsumer theElasticConsumer) throws KeeperException, IOException {
        this.theElasticConsumer = theElasticConsumer;
        theZookeeper = new ZooKeeper(hostPort, 30000, this);
         
        theDataMonitor = new DataMonitor(theZookeeper, znode, null, this);
    }

    /***************************************************************************
     * We do process any events ourselves, we just need to forward them on.
     *
     */
    public void process(WatchedEvent event) {
        theDataMonitor.process(event);
    }

    @Override
    protected void doIt() {
        try {
            synchronized (this) {
                while (!theDataMonitor.isDead() && this.getShouldRun()) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
        }
    }

    @Override
    protected void doShutdown() {
        this.setShouldRun(false);
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public void closing(int rc) {
        this.doShutdown();
        try {
            theZookeeper.close();
        } catch (InterruptedException ex) {
            Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void exists(byte[] data) {
        if (data == null) {
        } else {
            try {
                this.theElasticConsumer.flush();
                System.out.println(new String(data, EncodingUtils.UTF8));
                try {
                    theZookeeper.setData(zNode, "false".getBytes(), 1);
                } catch (KeeperException ex) {
                    Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
