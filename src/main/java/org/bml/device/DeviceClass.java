package org.bml.device;

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
import org.bml.util.exception.DisabledException;
import org.bml.util.useragent.UserAgentUtils;

/**
 * <p>
 * A simple encapsulation for enumeration of classes of devices connected to the that can be parsed from an http user agent.
 * </p>
 * <p>
 * This device classification was built for a high speed -- low overhead
 * classification system. In systems that do not require high speed and or where
 * you can scale out to handle loads it is recomended that you use an RDBMS or
 * some other configuration method to define device classes. This will allow for
 * runtime addition of device classes. As always there is a tradeoff between the
 * ease of use and speed of an enumeration and the ease of update.
 * </p>
 *
 * <p>
 * In order to use {@link DeviceClass#isClass(org.bml.device.DeviceClass, java.lang.String)}
 * you must ensure that the {@link UserAgentUtils#getPARSER()} !=null;</br>
 * If the {@link UserAgentUtils} {@link UAParser} is not configured correctly
 * this functionality will always throw a {@link DissabledException}
 * </p>
 *
 * @todo A configuration based version of this class needs to be implemented to 
 * allow for situations where runtime configuration is required.
 * 
 * @pre UserAgentUtils.getPARSER() != null;
 *
 * @author Brian M. Lima
 */
public enum DeviceClass {

    /**
     * If all other tests result false then Unknown
     */
    UNKNOWN(0),
    /**
     * Any known Mobile device
     */
    MOBILE(1),
    /**
     * Any known Desktop Device.
     */
    DESKTOP(2),
    /**
     * Any known Smart TV
     */
    SMARTTV(3),
    /**
     * A Robot / Crawler
     */
    BOT(4),
    /**
     * A Tablet
     */
    TABLET(5);

    /**
     * Storage for the id
     */
    private final int id;

    /**
     * Constructs a new DeviceClass with an id.
     *
     * @param id the id used to denote this DeviceClass in serialized form.
     * @pre GenericValidator.isInRange(id, 0, 5);
     */
    DeviceClass(final int id) {
        this.id = id;
    }

    /**
     * Getter for id
     *
     * @return The id for this DeviceClass
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the appropriate
     * Helper method for getting a DeviceClass from it's id
     *
     * @param id the id of a known DeviceClass
     * @return A known DeviceClass or null if the id does not exist.
     * @throws IllegalArgumentException if id is outside the known id range
     * @pre GenericValidator.isInRange(id, 0, 5)
     * @post DeviceClass != null ;
     */
    public static DeviceClass fromId(final int id) throws IllegalArgumentException {
        switch (id) {
            case 0:
                return UNKNOWN;
            case 1:
                return MOBILE;
            case 2:
                return DESKTOP;
            case 3:
                return SMARTTV;
            case 4:
                return BOT;
            case 5:
                return TABLET;
            default:
                throw new IllegalArgumentException("Can not create DeviceClass with id=" + id + ". Id is out of range.");
        }
    }

    /**
     * Static method for DeviceClass testing of a user agent string.
     * NOTE: There is no caching. It is recommended that you use DeviceType if
     * you plan on accessing any test more than once or plan on testing for UNKNOWN.
     *
     * @param deviceClass A {@link DeviceClass} to test for equality.
     * @param userAgent {@link String} containing a user agent
     * @return boolean
     * <p>
     * if the userAgent is the passed {@link DeviceClass}</p>
     * @throws DisabledException
     * <p>
     * If the underlying parser system has not been setup correctly or has been disabled for some reason.</p>
     *
     * @pre UserAgentUtils#getPARSER() != null;
     * @pre deviceClass !=null;
     * @pre userAgent!=null;
     * @pre !userAgent.isEmpty();
     *
     */
    public static boolean isClass(DeviceClass deviceClass, String userAgent) throws DisabledException {
        switch (deviceClass) {
            case UNKNOWN: //This is unfortunate code. TODO: Think about removing the Unknown Test
                if (UserAgentUtils.isMobileDevice(userAgent)) {
                    return false;
                }
                if (UserAgentUtils.isDesktopDevice(userAgent)) {
                    return false;
                }
                if (UserAgentUtils.isSmartTvDevice(userAgent)) {
                    return false;
                }
                if (UserAgentUtils.isBot(userAgent)) {
                    return false;
                }
                if (UserAgentUtils.isTablet(userAgent)) {
                    return false;
                }
                return true;
            case MOBILE:
                return UserAgentUtils.isMobileDevice(userAgent);
            case DESKTOP:
                return UserAgentUtils.isDesktopDevice(userAgent);
            case SMARTTV:
                return UserAgentUtils.isSmartTvDevice(userAgent);
            case BOT:
                return UserAgentUtils.isBot(userAgent);
            case TABLET:
                return UserAgentUtils.isTablet(userAgent);
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "name=" + name() + " id=" + id;
    }
}
