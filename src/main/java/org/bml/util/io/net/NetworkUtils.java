
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.ConversionUtils;

/**Encapsulation for a common operation throughout org.bml.util.
 * @author Brian M. Lima
 */
public class NetworkUtils {
    
    
    /**
     * The minimum value an IPV4 network port can have. NOTE: 0 is a reserved port however it is still available so it is included here
     */
    public static final int MIN_IPV4_NETWORK_PORT=0;
    /**
     * The maximum value an IPV4 network port can have. NOTE: this includes ephemeral ports.
     */
    public static final int MAX_IPV4_NETWORK_PORT=65535;
    

    private static final Log LOG = LogFactory.getLog(NetworkUtils.class);
    private static final String SIMPLE_NAME = NetworkUtils.class.getSimpleName();

    public static Integer toNumericIp(String sIp) throws UnknownHostException {
        if (sIp == null || sIp.isEmpty()) {
             return null;
        }
        try {
            InetAddress ip = InetAddress.getByName(sIp);
            Integer theIPOut = ConversionUtils.byteArrayToUnsignedInt(ip.getAddress());
            return theIPOut;
        } catch (UnknownHostException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("UnknownHostException Found while converting String " + sIp + " IPV4 adress to unsigned int.", ex);
            }
            throw ex;
        }
    }
    
    /**
     * Order of host name source.
     * <ol>
     * <li><code>Runtime.getRuntime().exec("hostname")</code>(Linux)</li>
     * <ol><code>Runtime.getRuntime().exec("gethostname")</code>(Unix)</li>
     * <ol><code>InetAddress.getLocalHost().getHostName()</code>(Other)</li>
     * </ol>
     * @return The host name of the server that this code runs on.
     */
    public static String getThisHostName(){
        String hostname=null;
        //get Runtime
        Runtime runtime = Runtime.getRuntime();
        try {
            //Attempt hostname call
            Process process=Runtime.getRuntime().exec("hostname");
            System.out.println(process.exitValue());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
        
        
        
        return hostname;
    }
    
    
    
    
    
}
