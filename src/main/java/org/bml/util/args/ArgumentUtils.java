package org.bml.util.args;

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
import java.io.File;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.bml.util.io.net.NetworkUtils;

/**
 * <p>
 * A collection of utilities for checking arguments and throwing
 * {@link IllegalArgumentException} on error. The methods here are designed to reduce
 * duplication often found in code where you are contractually obligated
 * to throw {@link IllegalArgumentException} when bad arguments are found.
 * </p>
 *
 * <p>
 * This class is the core of a belief that {@link IllegalArgumentException} should be
 * thrown when ever a caller should have checked parameters. This does lead to
 * a situation where parameters are likely to be checked multiple times, however
 * it is reasonable in most situations to sacrifice a measured amount of performance
 * for absolute stability. This mantra follows, unforseen errors should not
 * bring a system down. Errors in passed parameters will happen, even in 0
 * defect systems where all coding and engineering standards are strictly
 * adhered to. Generally it has been my experience that code that expects only
 * design by contract pre and post conditions without validation fail hard.
 * I believe that even systems like the above should not fail hard and should be
 * engineered to notify us of the error and move on.
 * </p>
 *@deprecated This class has been depreciated in favor of a combination of {@link org.apache.commons.validator.Validator} and {@link com.google.common.base.Preconditions} framework which provides the same and broader functionality with greater flexibility.
 * @author Brian M. Lima
 */
public class ArgumentUtils {
    
    /**
     * Instances should <b>NOT</b> be constructed in standard programming.
     */
    public ArgumentUtils() {
    }

    /**
     * <p>
     * Checks an {@link Object} for null. Throws an {@link IllegalArgumentException} if null
     * is found.
     * </p>
     *
     * @param checkObject The {@link Object} to be checked.
     * @param name The name used to identify the variable - to be checked, in exception messages.
     * @throws IllegalArgumentException If any of the check conditions are not met.
 *@deprecated This class and method has been depreciated in favor of a combination of {@link org.apache.commons.validator.Validator} and {@link com.google.common.base.Preconditions} framework which provides the same and broader functionality with greater flexibility.
     */
    public static void checkNullArg(final Object checkObject, final String name) throws IllegalArgumentException {
        if (checkObject == null) {
            throw new IllegalArgumentException("Can not operate on a null " + name);
        }
    }

    /**
     * <p>
     * Checks a long for min and max values. Throws an {@link IllegalArgumentException} if the long does not pass checks.
     * </p>
     *
     * @param checkLong The long to be checked.
     * @param name The name used to identify the variable - to be checked, in exception messages.
     * @param minLong The minimum value of the passed long.
     * @param maxLong The maximum value of the passed long.
     * @throws IllegalArgumentException If any of the check conditions are not met.
 *@deprecated This class and method has been depreciated in favor of a combination of {@link org.apache.commons.validator.Validator} and {@link com.google.common.base.Preconditions} framework which provides the same and broader functionality with greater flexibility.
     */
    public static void checkLongArg(final long checkLong, final String name, final long minLong, final long maxLong) {
        if (minLong == Long.MIN_VALUE && maxLong == Long.MAX_VALUE) {
            return;
        }
        if (checkLong < minLong) {
            throw new IllegalArgumentException("Can not operate. " + name + " VALUE=" + checkLong + " is less than minimum VALUE=" + minLong);
        }
        if (checkLong > maxLong) {
            throw new IllegalArgumentException("Can not operate. " + name + " VALUE=" + checkLong + " is greater than minimum VALUE=" + maxLong);
        }
    }

    /**
     * <p>
     * Checks a {@link String} for null and empty. Throws an {@link IllegalArgumentException} if the String does not pass checks.
     * </p>
     *
     * @param checkString The {@link String} to be checked.
     * @param name The name used to identify the String to be checked in exception messages.
     * @param allowNull If true allow the checkString to be null.
     * @param allowEmpty If true allow the checkString to be empty.
     * @throws IllegalArgumentException If any of the check conditions are not met.
 *@deprecated This class and method has been depreciated in favor of a combination of {@link org.apache.commons.validator.Validator} and {@link com.google.common.base.Preconditions} framework which provides the same and broader functionality with greater flexibility.
     */
    public static void checkStringArg(final String checkString, final String name, final boolean allowNull, final boolean allowEmpty) {
        //CHECK NULL
        if (!allowNull) {
            ArgumentUtils.checkNullArg(checkString, name);
        }
        //RETURN IF NULL
        if (checkString == null) {
            return;
        }
        //check empty
        if (checkString.isEmpty()) {
            if (!allowEmpty) {
                throw new IllegalArgumentException(name + " can not be empty.");
            }
        }
    }

    /**
     * <p>
     * Checks a {@link String} for null and empty. Throws an {@link IllegalArgumentException} if the String does not pass checks.
     * </p>
     *
     * @param checkString The {@link String} to be checked.
     * @param name The name used to identify the String to be checked in exception messages.
     * @param validator an implementation of {@link ArgumentValidator}
     * @throws IllegalArgumentException If any of the check conditions are not met.
 *@deprecated This class and method has been depreciated in favor of a combination of {@link org.apache.commons.validator.Validator} and {@link com.google.common.base.Preconditions} framework which provides the same and broader functionality with greater flexibility.
     */
    public static void checkStringArg(final String checkString, final String name, final ArgumentValidator<String> validator) {
        validator.check(checkString, name);
    }

    /**
     * <p>
     * Helper method for running file check sanity.
     * </p>
     *
     * <ol>
     * <lh><b>Check Operations</b></lh>
     * <li>Null - Throws IllegalArgumentException if <code>File==null</code></li>
     * <li>Exists - Throws IllegalArgumentException if <code>exists && !File.exists</code></li>
     * <li>Read - Throws IllegalArgumentException if <code>!File.canRead()</code></li>
     * <li>File - Throws IllegalArgumentException if <code>isFile && !File.isFile()</code></li>
     * <li>Directory - Throws IllegalArgumentException if <code>!isFile && !File.isDirectory()</code></li>
     * </ol>
     *
     * @param toCheck The File object to examine.
     * @param name The name of the file to be printed in messages
     * @param isFile true if passed toCheck should be a file, false if toCheck
     * should be a directory.
     * @param exists If true enforce {@link File} existance check.
     * @throws IllegalArgumentException If any of the check conditions are not met.
 *@deprecated This class and method has been depreciated in favor of a combination of {@link org.apache.commons.validator.Validator} and {@link com.google.common.base.Preconditions} framework which provides the same and broader functionality with greater flexibility.
     */
    public static void checkFileArg(final File toCheck, final String name, final boolean isFile, final boolean exists) throws IllegalArgumentException {
        if (toCheck == null) {
            throw new IllegalArgumentException("Can not process. Null " + name);
        }
        if (exists && !toCheck.exists()) {
            throw new IllegalArgumentException("Can not process. Non-existant " + name + ". FILE=" + toCheck.getAbsolutePath());
        }
        if (!toCheck.canRead()) {
            throw new IllegalArgumentException("Can not process. Check permissions. Can not read " + name + ". FILE=" + toCheck.getAbsolutePath());
        }
        if (isFile) {
            if (!toCheck.isFile()) {
                throw new IllegalArgumentException("Can not process. " + name + " is not a File. FILE=" + toCheck.getAbsolutePath());
            }
        } else {
            if (!toCheck.isDirectory()) {
                throw new IllegalArgumentException("Can not process. " + name + " is not a File. FILE=" + toCheck.getAbsolutePath());
            }
        }
    }

    /**
     * Checks an array and throws an {@link IllegalArgumentException} if any of the conditions are not met.
     *
     * @param array The array of {@link String} to check.
     * @param name The print friendly name of the array used in exception messages.
     * @param allowNullArray If true the check will allow a null array. If false and the array is null an {@link IllegalArgumentException} is thrown.
     * @param minArrayLength An integer of 0 or greater. If the arrays length is less than this parameter an {@link IllegalArgumentException} is thrown.
     * @param maxArrayLength An integer of 1 or greater. If the arrays length is greater than this parameter an {@link IllegalArgumentException} is thrown.
     * @throws IllegalArgumentException if any of the conditions of the array or it's values are not met.
     * @pre name != null
     * @pre minArrayLength >= 0
     * @pre maxArrayLength >= minArrayLength
 *@deprecated This class and method has been depreciated in favor of a combination of {@link org.apache.commons.validator.Validator} and {@link com.google.common.base.Preconditions} framework which provides the same and broader functionality with greater flexibility.
     */
    public static void checkArray(final Object[] array, final String name, boolean allowNullArray, final int minArrayLength, final int maxArrayLength) throws IllegalArgumentException {

        if (array == null) {
            if (allowNullArray) {
                return;
            } else {
                throw new IllegalArgumentException("Array " + name + " can not be null.");
            }
        }

        if (array.length < minArrayLength) {
            throw new IllegalArgumentException("Array " + name + " length " + array.length + " is less than minimum " + minArrayLength);
        }
        if (array.length < maxArrayLength) {
            throw new IllegalArgumentException("Array " + name + " length " + array.length + " is greater than maximum " + maxArrayLength);
        }
    }

    /**
     * <p>
     * Checks a {@link String} array and it's values and throws an {@link IllegalArgumentException} if any of the conditions are not met.
     *
     * </p>
     *
     * @param array The array of {@link String} to check.
     * @param name The print friendly name of the array used in exception messages.
     * @param allowNullArray If true the check will allow a null array. If false and the array is null an {@link IllegalArgumentException} is thrown.
     * @param minArrayLength An integer of 0 or greater. If the arrays length is less than this parameter an {@link IllegalArgumentException} is thrown.
     * @param maxArrayLength An integer of 1 or greater. If the arrays length is greater than this parameter an {@link IllegalArgumentException} is thrown.
     * @param allowNullValues If true the check will allow null array elements. If false and an array element is null an {@link IllegalArgumentException} is thrown.
     * @param allowEmptyValues If true the check will allow empty array elements. If false and an array element is empty an {@link IllegalArgumentException} is thrown.
     * @throws IllegalArgumentException if any of the conditions of the array or it's values are not met.
 *@deprecated This class and method has been depreciated in favor of a combination of {@link org.apache.commons.validator.Validator} and {@link com.google.common.base.Preconditions} framework which provides the same and broader functionality with greater flexibility.
     * @pre name !=null
     * @pre minArrayLength >= 0
     * @pre maxArrayLength >= minArrayLength
     */
    public static void checkStringArray(final String[] array, final String name, final boolean allowNullArray, final int minArrayLength, final int maxArrayLength, final boolean allowNullValues, final boolean allowEmptyValues) throws IllegalArgumentException {
        //check base array
        checkArray(array, name, allowNullArray, minArrayLength, maxArrayLength);
        //return if allow null and is null
        if (array == null) {
            return;
        }
        //short circut if there is no reason to continue checking.
        if (allowNullValues && allowEmptyValues) {
            return;
        }

        String namePrefix = "Array " + name + " value index ";
        //check values
        for (int c = 0; c < array.length; c++) {
            checkStringArg(array[c], namePrefix + c, allowNullValues, allowEmptyValues);
        }
    }

    /**
     * <p>
     * Checks a {@link String} array and it's values and throws an {@link IllegalArgumentException} if any of the conditions are not met.
     *
     * </p>
     *
     * @param array The array of {@link String} to check.
     * @param name The print friendly name of the array used in exception messages.
     * @param allowNullArray If true the check will allow a null array. If false and the array is null an {@link IllegalArgumentException} is thrown.
     * @param minArrayLength An integer of 0 or greater. If the arrays length is less than this parameter an {@link IllegalArgumentException} is thrown.
     * @param maxArrayLength An integer of 1 or greater. If the arrays length is greater than this parameter an {@link IllegalArgumentException} is thrown.
     * @throws IllegalArgumentException if any of the conditions of the array or it's values are not met.
 *@deprecated This class and method has been depreciated in favor of a combination of {@link org.apache.commons.validator.Validator} and {@link com.google.common.base.Preconditions} framework which provides the same and broader functionality with greater flexibility.
     * @pre name !=null
     * @pre minArrayLength >= 0
     * @pre maxArrayLength >= minArrayLength
     */
    public static void checkStringArray(final String[] array, final String name, final boolean allowNullArray, final int minArrayLength, final int maxArrayLength, final ArgumentValidator<String> valueValidator) throws IllegalArgumentException {
        //check base array
        checkArray(array, name, allowNullArray, minArrayLength, maxArrayLength);
        //return if allow null and is null
        if (array == null) {
            return;
        }
        String namePrefix = "Array " + name + " value index ";
        //check values
        for (int c = 0; c < array.length; c++) {
            valueValidator.check(array[c], namePrefix + c);
        }
    }

    /**
     * <p>
     * Validation method for email addresses both local and network.
     * </p>
     *
     * @param email The potential email address to be validated.
     * @param allowLocal True if the email address can be a local address, false otherwise
     * @throws IllegalArgumentException if any pre-conditions are not met or if the passed email does not validate as an email address.
     * @pre email != null
     * @pre !email.isEmpty()
 *@deprecated This class and method has been depreciated in favor of a combination of {@link org.apache.commons.validator.Validator} and {@link com.google.common.base.Preconditions} framework which provides the same and broader functionality with greater flexibility.
     */
    public static void checkEmail(final String email, final boolean allowLocal) throws IllegalArgumentException {
        ArgumentUtils.checkStringArg(email, "email address", false, false);
        //This may be able to be cached. There are stil some reported thread saftey issues with commons validator
        EmailValidator validator = EmailValidator.getInstance(allowLocal);
        if (!validator.isValid(email)) {
            throw new IllegalArgumentException("String passed does not pass vaidation by " + validator.getClass().getName() + " implementation. Not a vaild email address.");
        }
    }

    /**
     * 
     * @param allowLocal
     * @return 
 *@deprecated This class and method has been depreciated in favor of a combination of {@link org.apache.commons.validator.Validator} and {@link com.google.common.base.Preconditions} framework which provides the same and broader functionality with greater flexibility.
     */
    public static ArgumentValidator<String> getEmailArgumentValidator(final boolean allowLocal) {
        return new ArgumentValidator<String>() {
            EmailValidator validator = EmailValidator.getInstance(allowLocal);

            @Override
            public void check(String argument, String name) throws IllegalArgumentException {
                if (!validator.isValid(argument)) {
                    throw new UnsupportedOperationException("Argument " + name + " is not a valid email address");
                }
            }
        };
    }

    /**
     * <p>
     * Validation method for IPV4 network port values.
     * </p>
     *
     * @param port the potential IPV4 port to validate
     * @throws IllegalArgumentException if the passed port is not valid
 *@deprecated This class and method has been depreciated in favor of a combination of {@link org.apache.commons.validator.Validator} and {@link com.google.common.base.Preconditions} framework which provides the same and broader functionality with greater flexibility.
     */
    public static void checkIPV4Port(final int port) throws IllegalArgumentException {
        if (!GenericValidator.isInRange(port, NetworkUtils.MIN_IPV4_NETWORK_PORT, NetworkUtils.MAX_IPV4_NETWORK_PORT)) {
            throw new IllegalArgumentException("Passed port " + port + " is not in the range of " + NetworkUtils.MIN_IPV4_NETWORK_PORT + " - " + NetworkUtils.MAX_IPV4_NETWORK_PORT + ". Not a vaild network port.");
        }
    }

}
