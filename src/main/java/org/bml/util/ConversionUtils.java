package org.bml.util;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
 * <p>
 * Encapsulation for type conversion utility methods. Currently this class contains
 * methods used for converting int to byte[] and back. These methods are handy for
 * storage and handling of IPV4 addresses.
 * </p>
 *
 * @todo Add support for long conversions. This will enable IPV6 addresses
 * @author Brian M. Lima
 */
public class ConversionUtils {

    /**
     * Enables or disables precondition checking.
     */
    public static boolean CHECKED = true;

    /**
     * Standard Commons Logging {@link Log}
     */
    private static final Log LOG = LogFactory.getLog(ConversionUtils.class);

    /**
     * Instances should <b>NOT</b> be constructed in standard programming.
     */
    public ConversionUtils() {
    }

    /**
     * <p>
     * Converts an unsigned integer to a byte array.
     * </p>
     *
     * @param value an integer of 0 or greater
     * @return an array of bytes representing the passed unsigned int.
     * @throws IllegalArgumentException if the value passed is less than 0
     * @pre value>=0
     */
    public static final byte[] unsignedIntToByteArray(final int value) throws IllegalArgumentException {
        if (CHECKED) {
            Preconditions.checkArgument(value > -1, "Passed integer value is less than 0 value=%s", value);
        }
        return new byte[] {
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value};
    }

    /**
     * <p>
     * Utility for conversion of byte[] to int.
     * </p>
     *
     * @param byteArray An array of 4 bytes to convert to an unsigned int
     * @return The int defined by the passed byte array.
     *
     * @throws NullPointerException if the byteArray parameter is passed as null.
     * @throws IllegalArgumentException if the byte array passed length is not 4.
     *
     * @pre byteArray != null
     * @pre byteArray.length == 4
     */
    public static final int byteArrayToUnsignedInt(byte[] byteArray) throws NullPointerException, IllegalArgumentException {
        if (CHECKED) {
            Preconditions.checkNotNull(byteArray, "Can not convert a null byte array to an unsigned integer");
            Preconditions.checkArgument(byteArray.length == 4, "Can not convert byte array to unsigned int expected length of 4 found %s", byteArray.length);
        }
        return (byteArray[0] << 24)
                + ((byteArray[1] & 0xFF) << 16)
                + ((byteArray[2] & 0xFF) << 8)
                + (byteArray[3] & 0xFF);
    }
}
