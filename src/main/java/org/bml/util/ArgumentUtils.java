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

import java.io.File;

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
 * it is reasonable in most situations to sacrifice a small amount of performance
 * for absolute stability.
 * </p>
 *
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
     * @param checkLong The {@link long} to be checked.
     * @param name The name used to identify the variable - to be checked, in exception messages.
     * @param minLong The minimum value of the passed long.
     * @param maxLong The maximum value of the passed long.
     * @throws IllegalArgumentException If any of the check conditions are not met.
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
     */
    public static void checkStringArg(final String checkString, final String name, final boolean allowNull, final boolean allowEmpty) {
        //check null
        if (checkString == null) {
            if (!allowNull) {
                throw new IllegalArgumentException(name + " can not be null.");
            }
            return;
        }
        //check empty
        if (checkString.isEmpty() && !allowEmpty) {
            throw new IllegalArgumentException(name + " can not be empty.");
        }
    }

    /**
     * <p>
     * Helper method for running file check sanity.
     * </p>
     * 
     * <ol>
     * <lh><b>Check Operations</b></lh>
     * <li>Null - Throws {@link IllegalArgumentException} if <code>File==null</code></li>
     * <li>Exists - Throws {@link IllegalArgumentException} if <code>exists && !File.exists</code></li>
     * <li>Read - Throws {@link IllegalArgumentException} if <code>!File.canRead()</code></li>
     * <li>File - Throws {@link IllegalArgumentException} if <code>isFile && !File.isFile()</code></li>
     * <li>Directory - Throws {@link IllegalArgumentException} if <code>!isFile && !File.isDirectory()</code></li>
     * </ol>
     *
     * @param toCheck The File object to examine.
     * @param name The name of the file to be printed in messages
     * @param isFile true if passed toCheck should be a file, false if toCheck
     * should be a directory.
     * @param exists If true enforce {@link File} existance check.
     * @throws IllegalArgumentException If any of the check conditions are not met.
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

}
