/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

import junit.framework.TestCase;

/**
 *
 * @author brianmlima
 */
public class DeviceClassTest extends TestCase {

    public DeviceClassTest(String testName) {
        super(testName);
    }

    /**
     * Test of fromId method, of class DeviceClass.
     */
    public void testFromId() {
        System.out.println("fromId");

        DeviceClass tmpDeviceClass;
        for (DeviceClass deviceClass : DeviceClass.values()) {
            tmpDeviceClass = DeviceClass.fromId(deviceClass.getId());
            assertEquals(deviceClass.getId(), tmpDeviceClass.getId());
            assertEquals(deviceClass, tmpDeviceClass);

            if (deviceClass.getId() != tmpDeviceClass.getId()) {
                fail(deviceClass.name());
            }
        }
        
    }

    /**
     * Test of isClass method, of class DeviceClass.
     */
    public void testIsClass() {
        System.out.println("isClass. Needs Implementation");
        
    }

}
