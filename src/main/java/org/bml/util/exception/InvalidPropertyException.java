/*
 */

package org.bml.util.exception;

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
 * Thrown ins situations where a required property is missing or invalid.
 * This is essentially a checked version of IllegalArgumentException for properties.
 *
 * @author Brian M. Lima
 */
public class InvalidPropertyException extends Exception {

    /**
     * The property name.
     */
    private String propertyName;

    /**
     * The property value.
     */
    private Object propertyValue;
    /**
     * The property value expected type.
     */
    private Class expectedPropertyType;

    /**
     * Constructs a new exception with <code>null</code> as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link InvalidPropertyException#initCause}.
     */
    public InvalidPropertyException() {
    }

    /**
     * Constructs a new exception with the specified detail message. The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     * later retrieval by the {@link #getMessage()} method.
     */
    public InvalidPropertyException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     * <p>
     * Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     * by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A <tt>null</tt> value is
     * permitted, and indicates that the cause is nonexistent or
     * unknown.)
     */
    public InvalidPropertyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A <tt>null</tt> value is
     * permitted, and indicates that the cause is nonexistent or
     * unknown.)
     */
    public InvalidPropertyException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message,
     * cause, suppression enabled or disabled, and writable stack
     * trace enabled or disabled.
     *
     * @param message the detail message.
     * @param cause the cause. (A {@code null} value is permitted,
     * and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression whether or not suppression is enabled
     * or disabled
     * @param writableStackTrace whether or not the stack trace should
     * be writable
     */
    public InvalidPropertyException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @return the propertyName
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @param propertyName the propertyName to set
     */
    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * @return the propertyValue
     */
    public Object getPropertyValue() {
        return propertyValue;
    }

    /**
     * @param propertyValue the propertyValue to set
     */
    public void setPropertyValue(final Object propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     * @return the expectedPropertyType
     */
    public Class getExpectedPropertyType() {
        return expectedPropertyType;
    }

    /**
     * @param expectedPropertyType the expectedPropertyType to set
     */
    public void setExpectedPropertyType(final Class expectedPropertyType) {
        this.expectedPropertyType = expectedPropertyType;
    }

}
