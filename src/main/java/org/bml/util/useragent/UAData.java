
package org.bml.util.useragent;

import java.util.LinkedHashMap;
import java.util.Map;
import net.sf.uadetector.OperatingSystem;
import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentFamily;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.UserAgentType;
import net.sf.uadetector.VersionNumber;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Encapsulation for UA data. This includes any data that is parser from the
 * User Agent.
 *
 * @author Brian M. Lima
 */
public class UAData {

    private static final Log LOG = LogFactory.getLog(UAData.class);
    private String theUserAgent = null;
    private UserAgentFamily theUserAgentFamily = null;
    private OperatingSystem theOperatingSystem = null;
    private String theProducer = null;
    private String theProducerURL = null;
    private UserAgentType theUserAgentType = null;
    private VersionNumber theVersionNumber = null;

    public Map<String, String> getTheParamMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        try {

            String tmp = null;
            if (theUserAgent == null || theUserAgent.isEmpty()) {
                map.put("userAgentType".intern(), null);
                map.put("userAgentFamily".intern(), null);
                map.put("userAgentVersion".intern(), null);
                map.put("userAgentProducer".intern(), null);
                map.put("userAgentOSFamily".intern(), null);
                map.put("userAgentOSName".intern(), null);
                return map;
            }

            tmp = theUserAgent.toLowerCase();
            if (tmp.contains("ipad".intern())) {
                map.put("userAgentType".intern(), "ipad".intern());
            } else if (tmp.contains("iphone".intern())) {
                map.put("userAgentType".intern(), "iphone".intern());
            } else if (tmp.contains("ipod".intern())) {
                map.put("userAgentType".intern(), "ipod".intern());
            } else if (tmp.contains("ipod touch".intern())) {
                map.put("userAgentType".intern(), "ipod touch".intern());
            } else {
                if (theUserAgentType != null) {
                    map.put("userAgentType".intern(), theUserAgentType.getName().intern());
                } else {
                    map.put("userAgentType".intern(), null);
                }
            }

            if (theUserAgentFamily != null) {
                map.put("userAgentFamily".intern(), theUserAgentFamily.toString().intern());
            } else {
                map.put("userAgentFamily".intern(), null);
            }

            if (theVersionNumber != null) {
                map.put("userAgentVersion".intern(), theVersionNumber.getMajor().intern());
            } else {
                map.put("userAgentVersion".intern(), null);
            }

            map.put("userAgentProducer".intern(), theProducer.intern());

            if (theOperatingSystem != null) {
                map.put("userAgentOSFamily".intern(), theOperatingSystem.getFamilyName().intern());
                map.put("userAgentOSName".intern(), theOperatingSystem.getName().intern());
            } else {
                map.put("userAgentOSFamily".intern(), null);
                map.put("userAgentOSName".intern(), null);
            }
        } catch (Exception e) {
            if(LOG.isWarnEnabled()){
                LOG.warn("Exception caught while parsing out the UserAgent.", e);
            }
            map.put("userAgentType".intern(), null);
            map.put("userAgentFamily".intern(), null);
            map.put("userAgentVersion".intern(), null);
            map.put("userAgentProducer".intern(), null);
            map.put("userAgentOSFamily".intern(), null);
            map.put("userAgentOSName".intern(), null);
        }
        return map;
    }

    public UAData(final String userAgent, UserAgentStringParser userParser) throws Exception {
        if (userAgent == null || userAgent.isEmpty()) {
            throw new Exception("Unable to parse empty UserAgent");
        }
        if (userParser == null) {
            throw new Exception("Unable to parse UserAgent with null UserAgentStringParser ");
        }

        this.theUserAgent = userAgent;
        UserAgent agent = userParser.parse(userAgent);
        this.theUserAgentFamily = agent.getFamily();
        this.theOperatingSystem = agent.getOperatingSystem();
        this.theProducer = agent.getProducer();
        this.theProducerURL = agent.getProducerUrl();
        this.theUserAgentType = agent.getType();
        this.theVersionNumber = agent.getVersionNumber();
    }

    public static void main(String args[]) throws Exception {
        UserAgentStringParser userParser = UserAgentTool.USER_AGENT_PARSER_POOL.borrowObject();
        String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21 PagePeeker/2.1;  http://pagepeeker.com/robots/";
        UAData data = new UAData(userAgent, userParser);
        System.out.println(data.toString());
        UserAgentTool.USER_AGENT_PARSER_POOL.returnObject(userParser);
    }
}
