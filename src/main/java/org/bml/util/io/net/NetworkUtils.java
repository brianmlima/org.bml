
package org.bml.util.io.net;

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

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.Conversion;

/**Encapsulation for a common operation throughout org.bml.util.
 * @author Brian M. Lima
 */
public class NetworkUtils {

    private static final Log LOG = LogFactory.getLog(NetworkUtils.class);
    private static final String SIMPLE_NAME = NetworkUtils.class.getSimpleName();

    public static Integer toNumericIp(String sIp) throws UnknownHostException {
        if (sIp == null || sIp.isEmpty()) {
             return null;
        }
        try {
            InetAddress ip = InetAddress.getByName(sIp);
            Integer theIPOut = Conversion.byteArrayToUnsignedInt(ip.getAddress());
            return theIPOut;
        } catch (UnknownHostException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("UnknownHostException Found while converting String " + sIp + " IPV4 adress to unsigned int.", ex);
            }
            throw ex;
        }
    }
}
