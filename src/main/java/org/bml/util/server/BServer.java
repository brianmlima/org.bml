/**
 *   This file is part of org.bml.
 *
 *   org.bml is free software: you can redistribute it and/or modify it under the
 *   terms of the GNU General Public License as published by the Free Software
 *   Foundation, either version 3 of the License, or (at your option) any later
 *   version.
 *
 *   org.bml is distributed in the hope that it will be useful, but WITHOUT ANY
 *   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 *   A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License along with
 *   org.bml. If not, see <http://www.gnu.org/licenses/>.
 */


package org.bml.util.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The InvProxyA class abstracts the inverse proxy side of the Proxy /
 * InverseProxy Pattern. The result is that the user is unaware of data being
 * sent over the network. A user can call a method on the proxy (see. ProxyA),
 * the data is sent to this class running as a server on another box, and the
 * appropriate method is called.
 *
 * @author Cameron Byrd
 */
public abstract class BServer {

    private static Log LOG = LogFactory.getLog(BServer.class);
    protected boolean theDone = false;

    /**
     * Creates a new instance of InvProxyA
     */
    public BServer() {;
    }

    /**
     * The runInvProxy class starts a new server socket and listens for
     * connections from clients. Upon recieving a connection, it starts a new
     * InvProxyThread to process the connection.
     *
     * @param aParm: ParmDB - A parameter object containing parameters
     */
    public void runInvProxy(int aPort, int aNumThreads, long aSleepTime, int aMaxQueueLength) {


        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempting to start server on port: " + aPort);
        }

        //Start Server
        ServerSocket myServerSocket = null;
        try {
            myServerSocket = new ServerSocket(aPort, aMaxQueueLength);
        } catch (IOException e) {
            System.out.println(e);
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Server started on port: " + aPort);
        }

        //Continuously accept connections and start threads to
        //process connections.
        while (!theDone) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Ready to accept client connection");
            }

            Socket myClientSocket = null;
            try {
                myClientSocket = myServerSocket.accept();
            } catch (IOException e) {
                System.out.println("Accept Error: " + e);
                break;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Accepted connection from client: " + myClientSocket.getInetAddress()
                        + "\nStarting thread to deal with connection");
            }

            //Make sure there aren't too many active threads
            if (aNumThreads != -1) {
                while (Thread.activeCount() > aNumThreads) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Too many active threads.  Waiting " + aSleepTime
                                + "(ms) before trying to start new HitServerInvProxyThread again");
                    }
                    try {
                        Thread.sleep(aSleepTime);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                        break;
                    }
                }
            }
            if (myClientSocket != null && myClientSocket.isConnected()) {
                new InvProxyThread(this, myClientSocket);
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Client Socket is null or not connected when starting thread");
                }
                break;
            }
        }

        //Closing socket
        if (LOG.isDebugEnabled()) {
            LOG.debug("Closing server socket.  In general, we should never get here.");
        }

        try {
            myServerSocket.close();
        } catch (IOException e) {
            System.out.println(e);
            return;
        }
    }

    /**
     * The stopServer method sets theDone data member to true telling the server
     * to close gracefully
     */
    public void stopServer() {
        theDone = true;
    }

    /**
     * The abstract ProcessConnection method passes the user an input and output
     * stream, and allows them to control data flow over a socket.
     *
     * @param aIn: ObjectInputStream - Socket object input stream
     * @param aOut: ObjectOutputStream - Socket object output stream
     * @param aLog: Log - A loging object
     */
    public abstract void processConnection(ObjectInputStream aIn, ObjectOutputStream aOut);

    /**
     * The HitServerInvProxyThread class is responsible for processing a client
     * connection to the InvProxyA
     */
    protected class InvProxyThread extends Thread {

        private BServer theServer = null;
        private Socket theClientSocket = null;
        private ObjectInputStream theObjectIn = null;
        private ObjectOutputStream theObjectOut = null;

        public InvProxyThread(BServer aServer, Socket aClientSocket) {
            theServer = aServer;
            theClientSocket = aClientSocket;

            this.start();
        }

        /**
         * The run method processes the request from the client connection It
         * opens readers and writers to the socket, determines which method to
         * call on the HitServerImpl class, calls the method, and returns the
         * response to the user.
         */
        public void run() {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Attempting to open input and output streams to the socket: " + theClientSocket);
            }

            try {
                theObjectOut = new ObjectOutputStream(theClientSocket.getOutputStream());
                theObjectOut.flush();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Openned output stream....attempting to open input stream for socket: " + theClientSocket);
                }
                theObjectIn = new ObjectInputStream(theClientSocket.getInputStream());
            } catch (Exception e1) {
                try {
                    theClientSocket.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
                System.out.println(e1 + ":" + theClientSocket.getInetAddress().toString());
                return;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Openned readers and writers to the socket.  Now I'm going to process the request");
            }

            //Process request
            theServer.processConnection(theObjectIn, theObjectOut);

            //close all connections
            if (LOG.isDebugEnabled()) {
                LOG.debug("Closing client socket connections");
            }

            try {
                theObjectIn.close();
                theObjectOut.flush();
                theObjectOut.close();
                theClientSocket.close();
            } catch (IOException e) {
                System.out.println(e);
                return;
            }
        }
    }
}
