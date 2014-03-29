package org.bml.util.elasticmessager.stores;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import org.bml.util.elasticmessager.ElasticMessage;
import org.bml.util.elasticmessager.MessageStore;
import org.bml.util.elasticmessager.messages.StringElasticMessage;

/**
 * <p>
 * This is a simple MessageStore based on a File. This should not be used in prodiction
 * as it has to lock during the write operation. This is because Java can not handle
 * multiple threads writing to the same stream.
 * </p>
 *
 * @author Brian M. Lima
 */
public class SimpleFileMessageStore extends MessageStore<StringElasticMessage, String> {

    private final File outputFile;
    private final FileOutputStream outputStream;
    private final PrintWriter writer;
    private final Object WRITE_LOCK;
    private final String NL = System.getProperty("line.seperator");

    public SimpleFileMessageStore(final File outputFile) throws FileNotFoundException {
        super();
        this.outputFile = outputFile;
        this.outputStream = new FileOutputStream(outputFile);
        writer = new PrintWriter(outputStream);
        WRITE_LOCK = new Object();
    }

    @Override
    public boolean write(final StringElasticMessage anElasticMessage) {
        if (anElasticMessage == null) {
            return false;
        }
        synchronized (WRITE_LOCK) {
            this.writer.println(anElasticMessage.getContent());
        }
        return true;
    }

    @Override
    public int bulkWrite(final Set<StringElasticMessage> anElasticMessageSet) {
        if (anElasticMessageSet == null) {
            return -1;
        }
        if (anElasticMessageSet.isEmpty()) {
            return 0;
        }
        //write to tmp buffer;
        StringBuilder buff = new StringBuilder();
        for (ElasticMessage<String> message : anElasticMessageSet) {
            buff.append(message.getContent());
            buff.append(NL);
        }
        //get lock and do single write
        synchronized (WRITE_LOCK) {
            this.writer.print(buff.toString());
        }
        //set written
        for (ElasticMessage<String> message : anElasticMessageSet) {
            message.setHasBeenWriten(true);
        }
        //return number wrote.
        return anElasticMessageSet.size();
    }

    @Override
    public boolean close() {
        synchronized (WRITE_LOCK) {
            try {
                writer.flush();
                writer.close();
                outputStream.flush();
                outputStream.close();
                return true;
            } catch (IOException ex) {
                return false;
            }
        }
    }

    @Override
    public boolean test() {
        synchronized (WRITE_LOCK) {
            return !writer.checkError();
        }
    }

    @Override
    protected void doIt() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
