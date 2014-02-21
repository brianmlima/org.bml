
package org.bml.util.server;

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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *The Proxy class is responsible for connecting to a host
 *inverse proxy server, sending data to and from the server,
 *and closing the connection to the server.
 *
 * @author Brian M. Lima
 */
public class BClient {

    private static Log LOG = LogFactory.getLog(BClient.class);
    protected Socket theClientSocket = new Socket();
    protected boolean theDebug = false;
    protected ObjectInputStream theObjectIn = null;
    protected ObjectOutputStream theObjectOut = null;

    /**
     * Creates a new instance of Proxy
     */
    public BClient() {
    }

    /**
     *The Connect method creates a connection to a specified
     *host and port, and opens an object input and object ouput
     *stream.
     *@param aHost: String - A host name or ip
     *@param aPort: int - The port the host server is running on
     *@return boolean - true if connected successfully
     */
    public boolean connect(String aHost, int aPort) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempting to connect to server: " + aHost +
                    " on port: " + aPort);
        }

        try {
            theClientSocket = new Socket(aHost, aPort);
        } catch (UnknownHostException e) {
            System.out.println(e);
            return false;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Connected to host: " + aHost +
                    " Now trying to open Object input and output streams: " + theClientSocket);
        }

        try {
            theObjectIn = new ObjectInputStream(theClientSocket.getInputStream());

            if (LOG.isDebugEnabled()) {
                LOG.debug("Openned input stream....attempting to open output stream for socket: " + theClientSocket);
            }

            theObjectOut = new ObjectOutputStream(theClientSocket.getOutputStream());
            theObjectOut.flush();
        } catch (SocketException e) {
            if (e.getMessage().contains("Too many open files")) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("THIS IS OK.... Too many open socket connections.... waiting 1 second and trying again(1): " + e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException f) {
                }
                this.connect(aHost, aPort);
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(e);
                }
            }
        } catch (Exception e1) {
            if (e1 != null && e1.getMessage() != null) {
                if (e1.getMessage().contains("(too many open files)")) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException f) {
                    }
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Too many open files....I'm going to try to reopen the connection");
                    }

                    this.connect(aHost, aPort);
                } else if (e1.getMessage().contains("Too many open files")) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("THIS IS OK.... Too many open socket connections.... waiting 1 second and trying again(2): " + e1);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException f) {
                    }
                    this.connect(aHost, aPort);
                } else {
                    System.out.println("FClient: " + e1);
                    return false;
                }
            } else {
                System.out.println("FClient: " + e1);
                return false;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Streams open to host: " + aHost);
        }

        return true;
    }

    /**
     *The Close method closes all sockets and streams
     *@return boolean - true if sockets and streams closed correctly
     */
    public boolean close() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Closing client connection");
        }

        try {
            theObjectIn.close();
            theObjectOut.flush();
            theObjectOut.close();
            theClientSocket.close();
        } catch (IOException e) {
            System.out.println("FCLIENT: close: " + e);
            LOG.error(e);
            return false;
        }
        return true;
    }
}
