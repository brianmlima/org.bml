
package org.bml.util.io.net;

import java.io.IOException;
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
    
    
    public static void main(String args[]){
        getThisHostName();
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
