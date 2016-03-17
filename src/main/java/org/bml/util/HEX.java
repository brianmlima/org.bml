/**
 * This file is part of org.bml.
 *
 * org.bml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.bml is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.bml. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bml.util;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2016 Brian M. Lima
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
 * A utility for dealing with Hex numerics and strings.
 *
 * @author Brian M. Lima
 */
public final class HEX {

    /**
     * The base 16 encoding number.
     * I am no longer a magic number.
     */
    public static final int BASE_16 = 16;

    /**
     * Disables the default constructor.
     *
     * @throws InstantiationException Always.
     */
    private HEX() throws InstantiationException {
        throw new InstantiationException("Instances of this type are forbidden.");
    }

    /**
     * Checks to see that a string only contains valid hex values. NOTE: This does not include a preceeding "0x".
     *
     * @param theString a string with potential hex chars.
     * @return true if theString only contains valid hex chars.
     */
    public static boolean isHex(final String theString) {
        final int len = theString.length();
        char aChar;
        for (int c = 0; c < len; c++) {
            aChar = theString.charAt(c);
            if (!(((aChar >= '0' && aChar <= '9') || (aChar >= 'a' && aChar <= 'f') || (aChar >= 'A' && aChar <= 'F')))) {
                return false;
            }
        }
        return true;
    }
}
