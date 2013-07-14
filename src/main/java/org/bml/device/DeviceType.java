package org.bml.device;

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

/**
 * Device type data structure and cached on demand testing support.
 *
 * @author Brian M. Lima
 */
public class DeviceType {

    /**
     * The User Agent String
     */
    private String userAgent = null;
    /**
     * Storage for test results
     */
    private Boolean isDeviceArray[] = new Boolean[DeviceClass.values().length];
    /**
     * Storage for has test occurred to avoid testing the userAgent more than
     * once per device type
     */
    private boolean hasTestedArray[] = new boolean[DeviceClass.values().length];

    /**
     * Access to the user agent string.
     *
     * @return the userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Create a new DeviceType from a user agent string
     *
     * @param userAgent
     */
    public DeviceType(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Implements testing a this object for membership in any of the
     * enumerations of DeviceClass.
     *
     * Because of the implementation we are using currently I have taken
     * measures to avoid re-testing and only test when necessary.
     *
     * @param deviceClass The DeviceClass to test for
     * @return True if a match is found, null if unable to test (IE: null
     * userAgent), or True if a match is found.
     */
    public Boolean test(DeviceClass deviceClass) {
        int deviceId = deviceClass.getId();
        if (!hasTestedArray[deviceId]) {
            isDeviceArray[deviceId] = DeviceClass.isClass(deviceClass, userAgent);
            hasTestedArray[deviceId] = true;
        }
        return isDeviceArray[deviceId];
    }

    /**
     * Helper method
     *
     * @return
     */
    public DeviceClass getDeviceClass() {
        if (this.isDesktop() == Boolean.TRUE) {
            return DeviceClass.DESKTOP;
        }
        if (this.isMobile() == Boolean.TRUE) {
            return DeviceClass.MOBILE;
        }
        if (this.isBot() == Boolean.TRUE) {
            return DeviceClass.BOT;
        }
        if (this.isSmartTV() == Boolean.TRUE) {
            return DeviceClass.SMARTTV;
        }
        if (this.isUnknown() == Boolean.TRUE) {
            return DeviceClass.UNKNOWN;
        }
        return null;
    }

    /**
     * Test user agent for a Bot signature
     *
     * @return the isMobile test result
     */
    public Boolean isMobile() {
        return test(DeviceClass.MOBILE);
    }

    /**
     * Test user agent for a isDeskTop signature
     *
     * @return the isDesktop test result
     */
    public Boolean isDesktop() {
        return test(DeviceClass.DESKTOP);
    }

    /**
     * Test user agent for a SmartTv signature
     *
     * @return the isSmartTV test result
     */
    public Boolean isSmartTV() {
        return test(DeviceClass.SMARTTV);
    }

    /**
     * Test user agent for a Bot signature
     *
     * @return the isBot test result
     */
    public Boolean isBot() {
        return test(DeviceClass.BOT);
    }

    /**
     * NOTE: This method can be slow as it's worst case requires testing for all
     * other class membership before unknown can be determined.
     *
     * @return the isUnknown test result
     */
    public Boolean isUnknown() {
        if (this.userAgent == null) {
            return null;
        }
        for (DeviceClass deviceClass : DeviceClass.values()) {
            if (deviceClass == DeviceClass.UNKNOWN) {
                continue;
            }
            if (test(deviceClass) == Boolean.TRUE) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
