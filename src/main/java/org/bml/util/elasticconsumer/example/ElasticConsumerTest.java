package org.bml.util.elasticconsumer.example;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.pool2.PooledObjectFactory;
import org.bml.util.elasticconsumer.ElasticConsumer;

/**
 * A simple test harness for the ElasticConsumer framework.
 *
 * @author Brian M. Lima
 */
public class ElasticConsumerTest {

    public static void main(String args[]) throws Exception {
        boolean fair = true, debug = true, result = false;

        //We need the queue to create the thread factory and the ElasticConsumer after that you can disgard the refrence.
        BlockingQueue<ProcData> queueIn = new SynchronousQueue<ProcData>();

        //This is the factory that creates worker threads for the ElasticConsumer
        PooledObjectFactory<TestWorkerThread> factory = new TestWorkerThreadObjectFactory(queueIn, 2, TimeUnit.SECONDS, 100);

        //The ElasticConsumer component.
        ElasticConsumer<ProcData, TestWorkerThread> app = new ElasticConsumer<ProcData, TestWorkerThread>(factory, queueIn, 1, false);

        app.setDebug(debug);
        app.start();

        System.out.println("ElasticConsumer: ALIVE=" + app.isAlive());

        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("System is reporting " + processors + " processor cores.");

        List<LoadProducer> LoadProducerList = new ArrayList<LoadProducer>(processors);
        LoadProducer load;
        for (int c = 0; c < processors; c++) {
            load = new LoadProducer(app);
            load.start();
            LoadProducerList.add(load);
        }

        Thread.sleep(10000);

        app.setShouldRun(false);
        System.out.println("ElasticConsumer: ALIVE=" + app.isAlive());

    }
}
