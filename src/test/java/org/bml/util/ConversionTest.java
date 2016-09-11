/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bml.util;

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

import java.util.Arrays;
import junit.framework.TestCase;

/**
 *
 * @author brianmlima
 */
public class ConversionTest extends TestCase {

    public ConversionTest(String testName) {
        super(testName);
    }

    /**
     * Test of unsignedIntToByteArray method, of class ConversionUtils.
     */
    public void testUnsignedIntToByteArray() {
        System.out.println("unsignedIntToByteArray");
        int value = 0;
        byte[] expResult = new byte[]{0, 0, 0, 0};
        byte[] result = ConversionUtils.unsignedIntToByteArray(value);

        if (!Arrays.equals(result, expResult)) {
            fail();
        }
    }

    /**
     * Test of byteArrayToUnsignedInt method, of class ConversionUtils.
     */
    public void testByteArrayToUnsignedInt() {
        System.out.println("byteArrayToUnsignedInt");
        byte[] byteArray = new byte[]{0, 0, 0, 0};
        int expResult = 0;
        int result = ConversionUtils.byteArrayToUnsignedInt(byteArray);

        assertEquals(expResult, result);
        if (result != expResult) {
            fail();
        }

    }

}
