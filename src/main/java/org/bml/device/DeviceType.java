package org.bml.device;

import org.bml.util.ArgumentUtils;
import org.bml.util.exception.DisabledException;

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
/**
 * Device type data structure and cached on demand testing support.
 *
 * @author Brian M. Lima
 */
public class DeviceType {

    /**
     * The User Agent String
     */
    private final String userAgent;
    /**
     * Storage for test results
     */
    private final Boolean isDeviceArray[] = new Boolean[DeviceClass.values().length];
    /**
     * Storage for has test occurred to avoid testing the userAgent more than
     * once per device type
     */
    private final boolean hasTestedArray[] = new boolean[DeviceClass.values().length];

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
     * @param userAgent An http connections user agent
     * @throws IllegalArgumentException <ol><li>if userAgent is null</li><li>if userAgent is empty</li></ol>
     */
    public DeviceType(String userAgent) throws IllegalArgumentException {
        ArgumentUtils.checkStringArg(userAgent, "userAgent parameter", false, false);
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
     * @throws DisabledException If the underlying parser is not configured and or purposely disabled.
     * @throws IllegalArgumentException if the deviceClass parameter is null;
     */
    public boolean test(DeviceClass deviceClass) throws DisabledException, IllegalArgumentException {
        ArgumentUtils.checkNullArg(deviceClass, "deviceClass parameter");
        int deviceId = deviceClass.getId();
        if (!hasTestedArray[deviceId]) {
            isDeviceArray[deviceId] = DeviceClass.isClass(deviceClass, userAgent);
            hasTestedArray[deviceId] = true;
        }
        return isDeviceArray[deviceId];
    }

    /**
     * Uses the UAParser implementation to classify the userAgent {@link String} this {@link DeviceType} instance wraps.
     *
     * @return The {@link DeviceClass} enum value for this {@link DeviceType} instance.
     * @throws DisabledException If the underlying parser is not configured and or purposely disabled.
     */
    public DeviceClass getDeviceClass() throws DisabledException {
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
     * @throws DisabledException If the underlying parser is not configured and or purposely disabled.
     */
    public Boolean isMobile() throws DisabledException {
        return test(DeviceClass.MOBILE);
    }

    /**
     * Test user agent for a isDeskTop signature
     *
     * @return the isDesktop test result
     * @throws DisabledException If the underlying parser is not configured and or purposely disabled.
     */
    public Boolean isDesktop() throws DisabledException {
        return test(DeviceClass.DESKTOP);
    }

    /**
     * Test user agent for a SmartTv signature
     *
     * @return the isSmartTV test result
     * @throws DisabledException If the underlying parser is not configured and or purposely disabled.
     */
    public Boolean isSmartTV() throws DisabledException {
        return test(DeviceClass.SMARTTV);
    }

    /**
     * Test user agent for a Bot signature
     *
     * @return the isBot test result
     * @throws DisabledException If the underlying parser is not configured and or purposely disabled.
     */
    public Boolean isBot() throws DisabledException {
        return test(DeviceClass.BOT);
    }

    /**
     * NOTE: This method can be slow as it's worst case requires testing for all
     * other class membership before unknown can be determined.
     *
     * @return the isUnknown test result
     * @throws DisabledException If the underlying parser is not configured and or purposely disabled.
     */
    public Boolean isUnknown() throws DisabledException {
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
