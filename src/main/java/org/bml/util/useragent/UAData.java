package org.bml.util.useragent;

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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import net.sf.uadetector.OperatingSystem;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentFamily;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.UserAgentType;
import net.sf.uadetector.VersionNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulation for UA data.
 * This includes any data that can be parsed from the
 * User Agent.
 *
 * This class has inefficiencies and should not be used in a time sensitive pipe.
 *
 * @author Brian M. Lima
 */
public class UAData {

    /**
     * Standard class slf4j logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(UAData.class);
    /**
     * The user agent string.
     */
    private String theUserAgent = null;
    /**
     * The {@link UserAgentFamily}.
     */
    private UserAgentFamily theUserAgentFamily = null;
    /**
     * The {@link OperatingSystem}.
     */
    private OperatingSystem theOperatingSystem = null;
    /**
     * The producer.
     */
    private String theProducer = null;
    /**
     * The producer url.
     */
    private String theProducerURL = null;
    /**
     * The {@link UserAgentType}.
     */
    private UserAgentType theUserAgentType = null;
    /**
     * The {@Link VersionNumber}.
     */
    private VersionNumber theVersionNumber = null;

    /**
     * Creates a parameter map from this object.
     *
     * @return a map of property name to value.
     */
    public Map<String, String> toParamMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
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

        tmp = theUserAgent.toLowerCase(Locale.US);
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
        return map;
    }

    /**
     * Create a new instance of {@link UAData} and parses UAData from the passed user agent string.
     *
     * @param userAgent The user agent String.
     * @param userParser A UserAgentStringParser.
     * @throws Exception on error.
     */
    public UAData(final String userAgent, final UserAgentStringParser userParser) throws Exception {
        checkNotNull(userAgent, "Can not create a new instance of UAData with a null {} parameter.", "userAgent");
        checkArgument(!userAgent.isEmpty(), "Can not create a new instance of UAData with an empty {} parameter.", "userAgent");
        checkNotNull(userParser, "Can not create a new instance of UAData with a null {} parameter.", "userParser");
        this.theUserAgent = userAgent;
        ReadableUserAgent agent = userParser.parse(userAgent);
        this.theUserAgentFamily = agent.getFamily();
        this.theOperatingSystem = agent.getOperatingSystem();
        this.theProducer = agent.getProducer();
        this.theProducerURL = agent.getProducerUrl();
        this.theUserAgentType = agent.getType();
        this.theVersionNumber = agent.getVersionNumber();
    }

}
