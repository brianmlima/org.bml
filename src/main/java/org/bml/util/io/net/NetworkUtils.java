
package org.bml.util.io.net;

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
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.ConversionUtils;

/**
 * Encapsulation for a common Network operations.
 *
 * @author Brian M. Lima
 */
public class NetworkUtils {

    /**
     * The minimum value an IPV4 network port can have. NOTE: 0 is a reserved port however it is still available so it is included here
     */
    public static final int MIN_IPV4_NETWORK_PORT = 0;
    /**
     * The maximum value an IPV4 network port can have. NOTE: this includes ephemeral ports.
     */
    public static final int MAX_IPV4_NETWORK_PORT = 65535;

    /**
     * Enables or disables precondition checking.
     */
    public static boolean CHECKED = true;

    /**
     * Standard Commons Logging {@link Log}
     */
    private static final Log LOG = LogFactory.getLog(NetworkUtils.class);

    /**
     * <p>
     * Conversion utility for changing an ip address String into a positive integer.
     * This is very handy for range searches, bucketing, and storage in data stores that
     * do not have built in IP address storage and query models.
     * </p>
     *
     * @param ipString A string representing an IPV4 IP address or a network host name.
     * @return a positive integer representation of an IP address
     * @throws UnknownHostException per InetAddress.getByName(ipString)
     * @throws NullPointerException if the passed ipString is null
     * @throws IllegalArgumentException if the passed ipString.isEmpty()
     *
     * @pre ipString!=null
     * @pre !ipString.isEmpty()
     *
     */
    public static Integer toNumericIp(final String ipString) throws UnknownHostException, NullPointerException, IllegalArgumentException {
        if (CHECKED) {
            Preconditions.checkNotNull(ipString, "Can not convert a null ipString to a numeric IP");
            Preconditions.checkArgument(ipString.isEmpty(), "Can not convert an empty ipString to a numeric IP");
        }
        try {
            InetAddress ip = InetAddress.getByName(ipString);
            return ConversionUtils.byteArrayToUnsignedInt(ip.getAddress());
        } catch (UnknownHostException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("UnknownHostException Found while converting String " + ipString + " IPV4 adress to unsigned int.", ex);
            }
            throw ex;
        }
    }

    /**
     * <p>
     * Attempt to resolve this computers host name.
     * </p>
     * <p>
     * <b>WARNING:</b>This method uses Runtime and a Process. It also waits for
     * the process to complete before returning.
     * </p>
     * <p>
     * Order of host name source.
     * <ol>
     * <li><code>Runtime.getRuntime().exec("hostname")</code>(Linux)</li>
     * <li><code>Runtime.getRuntime().exec("gethostname")</code>(Unix)</li>
     * <li><code>InetAddress.getLocalHost().getHostName()</code>(Other)</li>
     * </ol>
     * </p>
     *
     * @return The host name of the server that this code runs on.
     * @todo implement multiple hostname attempts and try to guess the rite one for the OS.
     */
    public static String getThisHostName() throws IOException, InterruptedException {
        String hostname = null;
        Process process;
        StringWriter writer = new StringWriter();
        InputStream processInputStream;
        //get Runtime
        Runtime runtime = Runtime.getRuntime();
        try {
            //Attempt hostname call
            process = Runtime.getRuntime().exec("hostname");
            process.waitFor();
            processInputStream = process.getInputStream();
            IOUtils.copy(processInputStream, writer, CharEncoding.UTF_8);
            hostname = writer.toString();
            process.exitValue();
        } catch (IOException ioe) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("IPException caught while attempting to resolve hostname.", ioe);
            }
            throw ioe;
        }
        return hostname;
    }

    /**
     * Instances should <b>NOT</b> be constructed in standard programming.
     */
    public NetworkUtils() {
    }

}
