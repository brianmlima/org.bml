
package org.bml.util.io.net;

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
