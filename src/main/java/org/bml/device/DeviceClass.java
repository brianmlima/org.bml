
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

import org.bml.util.useragent.UserAgentUtils;

/**
 * A simple encapsulation for enumeration of classes of devices connected to the
 * internet that can be parsed from a user agent.
 *
 * The getId function allows you to use an id in databases to save time,
 * network, and storage resources.
 *
 * The fromId function allows the marshaling of an id into a DeviceClass.
 *
 * @author Brian M. Lima
 */
public enum DeviceClass {
    /** If all other tests result false then Unknown */
    UNKNOWN(0),
    /** Any known Mobile device */
    MOBILE(1),
    /** Any known Desktop Device.*/
    DESKTOP(2),
    /** Any known Smart TV */
    SMARTTV(3),
    /** A Robot / Crawler */
    BOT(4),
    /** A Robot / Crawler */
    TABLET(5);
  
    /** Storage for the id */ 
    private final int id;

    /**Constructs a new DeviceClass with an id.
     * @param id the id used to denote this DeviceClass in serialized form.
     */
    DeviceClass(int id) {
        this.id = id;
    }

    /** Getter for id
     * @return The id for this DeviceClass
     */
    public int getId() {
        return id;
    }

    /**Helper method for getting a DeviceClass from it's id
     * 
     * @param id the id of a known DeviceClass
     * @return A known DeviceClass or null if the id does not exist.
     */
    public static DeviceClass fromId(int id) {
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
                return null;
        }
    }

    /** Static method for DeviceClass testing of a user agent string. 
     * NOTE: There is no caching. It is recommended that you use DeviceType if 
     * you plan on accessing any test more than once or plan on testing for UNKNOWN. 
     * 
     * @return the isMobile test result
     */
    public static Boolean isClass(DeviceClass deviceClass, String userAgent) {
        switch (deviceClass) {
            case UNKNOWN:
                return null;
            case MOBILE:
                return UserAgentUtils.isMobileDevice(userAgent);
            case DESKTOP:
                return UserAgentUtils.isDesktopDevice(userAgent);
            case SMARTTV:
                return UserAgentUtils.isSmartTvDevice(userAgent);
            case BOT:
                return UserAgentUtils.isBot(userAgent);
            case TABLET:
                //return UserAgentUtils.isTablet(userAgent);
                return null;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "name=" + name() + " id=" + id;
    }
}
