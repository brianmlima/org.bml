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


package org.bml.util.useragent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**Encapsulation of User Agent utilities. All code should use this class and not
 * specific implementations.
 * @author Brian M. Lima
 */
public class UserAgentUtils {

    private static Log LOG = LogFactory.getLog(UserAgentUtils.class);

    /**
     * Encapsulation for user agent parsing of mobile devices.
     *
     * @param userAgent The request user agent
     * @return Boolean null if unable to process, true if mobile, false
     * otherwise.
     */
    public static Boolean isMobileDevice(String userAgent) {
        if (userAgent == null) {
            return null;
        }
        if (userAgent.isEmpty()) {
            return false;
        }
        try {
            return net.sourceforge.wurfl.core.utils.UserAgentUtils.isMobileBrowser(userAgent);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOG.error("ArrayIndexOutOfBoundsException encountered while parsing User Agent using net.sourceforge.wurfl.core.utils.UserAgentUtils isMobileBrowser userAgent="+userAgent);
            return null;
        } catch (Exception e) {
            LOG.error("Exception encountered while parsing User Agent using net.sourceforge.wurfl.core.utils.UserAgentUtils userAgent="+userAgent, e);
            return null;
        }
    }

    /**
     * Encapsulation for user agent parsing of Desktop devices.
     *
     * @param userAgent The request user agent
     * @return Boolean null if unable to process, true if desktop, false
     * otherwise.
     */
    public static Boolean isDesktopDevice(String userAgent) {
        if (userAgent == null) {
            return null;
        }
        if (userAgent.isEmpty()) {
            return null;
        }
        try {
            return net.sourceforge.wurfl.core.utils.UserAgentUtils.isDesktopBrowser(userAgent);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOG.error("ArrayIndexOutOfBoundsException encountered while parsing User Agent using net.sourceforge.wurfl.core.utils.UserAgentUtils  isDesktopDevice userAgent="+userAgent);
            return null;
        } catch (Exception e) {
            LOG.error("Exception encountered while parsing User Agent using net.sourceforge.wurfl.core.utils.UserAgentUtils userAgent="+userAgent, e);
            return null;
        }

    }

    /**
     * Encapsulation for user agent parsing of Desktop devices.
     *
     * @param userAgent The request user agent
     * @return Boolean null if unable to process, true if desktop, false
     * otherwise.
     */
    public static Boolean isSmartTvDevice(String userAgent) {
        if (userAgent == null) {
            return null;
        }
        if (userAgent.isEmpty()) {
            return false;
        }
        try {
          
            return net.sourceforge.wurfl.core.utils.UserAgentUtils.isSmartTvBrowser(userAgent);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOG.error("ArrayIndexOutOfBoundsException encountered while parsing User Agent using net.sourceforge.wurfl.core.utils.UserAgentUtils isSmartTvDevice userAgent="+userAgent);
            return null;
        } catch (Exception e) {
            LOG.error("Exception encountered while parsing User Agent using net.sourceforge.wurfl.core.utils.UserAgentUtils userAgent="+userAgent, e);
            return null;
        }
    }

    /**
     * Encapsulation for user agent parsing of bots.
     *
     * @param userAgent The request user agent
     * @return Boolean null if unable to process, true if a bot, false
     * otherwise.
     */
    public static Boolean isBot(String userAgent) {
        if (userAgent == null) {
            return null;
        }
        if (userAgent.isEmpty()) {
            return false;
        }
        try {
            return net.sourceforge.wurfl.core.utils.UserAgentUtils.isBot(userAgent);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOG.error("ArrayIndexOutOfBoundsException encountered while parsing User Agent using net.sourceforge.wurfl.core.utils.UserAgentUtils isBot userAgent="+userAgent);
            return null;
        } catch (Exception e) {
            LOG.error("Exception encountered while parsing User Agent using net.sourceforge.wurfl.core.utils.UserAgentUtils userAgent="+userAgent, e);
            return null;
        }
    }

}
