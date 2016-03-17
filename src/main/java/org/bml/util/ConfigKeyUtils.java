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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.Properties;
import org.bml.util.exception.InvalidPropertyException;
import org.slf4j.Logger;

/**
 * A collection of utility methods for working with the configuration / config key enup pattern.
 *
 * Most of these are just methods that used to be copy and pasted in enums used to unmarshal
 * properties into configuration objects. This set of helper methods was created to remove duplicate code
 * in all of the enumerations that use the configuration key pattern.
 *
 * @author Brian M. Lima
 */
public final class ConfigKeyUtils {

    /**
     * Disables the default constructor.
     *
     * @throws InstantiationException Always.
     */
    private ConfigKeyUtils() throws InstantiationException {
        throw new InstantiationException("Instances of this type are forbidden.");
    }

    /**
     * Convenience method for getting a value {@link Object} from a {@link Properties}.
     *
     * @param theLog The logger from the config key object that is using the helper method.
     * @param theProperties A Properties object to pull values from.
     * @param theEnumName The name of the config key enumeration for logging purposes.
     * @param theConfigKey The ConfigKey object we are getting for.
     *
     * @return The value as the type {@link PoolConfigKeys#valueClass} cast as an Object.
     * @throws InvalidPropertyException If type is a numeric, exists, but can not be parsed.
     */
    public static Object getObject(final Logger theLog, final Properties theProperties, final String theEnumName, final ConfigKey theConfigKey) throws InvalidPropertyException {
        return getObject(theLog, theProperties, theConfigKey.isRequired(), theEnumName, theConfigKey.getPropertyName(), theConfigKey.getValueClass(), theConfigKey.getDefaultValue());
    }

    /**
     * Convenience method for getting a value {@link Object} from a {@link Properties}.
     *
     * @param theLog The logger from the config key object that is using the helper method.
     * @param theProperties A Properties object to pull values from.
     * @param isRequired Is a property required.
     * @param theEnumName The name of the config key enumeration for logging purposes.
     * @param thePropertyName The name of the property for logging and retrieval purposes.
     * @param theValueClass The class the value should be validated against and returned as.
     * @param theDefaultValue The default value of the configuration parameter.
     *
     * @return The value as the type {@link PoolConfigKeys#valueClass} cast as an Object.
     * @throws InvalidPropertyException If type is a numeric, exists, but can not be parsed.
     */
    public static Object getObject(final Logger theLog, final Properties theProperties, final boolean isRequired, final String theEnumName, final String thePropertyName, final Class theValueClass, final Object theDefaultValue) throws InvalidPropertyException {
        checkNotNull(theProperties, String.format("Can not get an Integer value for key %s from a null Properties.", theEnumName));
        theLog.trace("Attempting to get and or default KEY {} , TYPE {} , DEFAULT {}", theEnumName, theValueClass, theDefaultValue);
        String sValue = getAndDefault(theProperties, isRequired, thePropertyName, theValueClass, theDefaultValue);
        theLog.trace("Retrieved value {}  for KEY {} , TYPE {} , DEFAULT {}", sValue, theEnumName, theValueClass, theDefaultValue);

        if (theValueClass == String.class) {
            return sValue;
        } else if (theValueClass == Integer.class) {
            try {
                return Integer.valueOf(sValue);
            } catch (NumberFormatException e) {
                throw new InvalidPropertyException(String.format("NumberFormatException encountered while attempting to parse Integer from {KEY=%s,VALUE=%s} MESSAGE=%s", theEnumName, sValue, e.getMessage()));
            }
        } else if (theValueClass == Boolean.class) {
            return Boolean.valueOf(sValue);
        } else if (theValueClass == Long.class) {
            return Long.valueOf(sValue);
        }
        return null;
    }

    /**
     * Helper method for getting the string value for a property key.
     *
     * @param theProperties A Properties object to pull values from.
     * @param theConfigKey The ConfigKey object we are getting for.
     * @return The String value of the parameter or the default.
     * @throws InvalidPropertyException if the property is required and not set.
     */
    public static String getAndDefault(final Properties theProperties, final ConfigKey theConfigKey) throws InvalidPropertyException {
        if (theConfigKey.isRequired()) {
            String value = theProperties.getProperty(theConfigKey.getPropertyName());
            if (value == null) {
                InvalidPropertyException e = new InvalidPropertyException(String.format("Property is required but is not set or is null. PROPERTY=%s VALUE=%s", theConfigKey.getPropertyName(), null));
                e.setPropertyName(theConfigKey.getPropertyName());
                e.setExpectedPropertyType(theConfigKey.getValueClass());
                throw e;
            }
            return value;
        } else {
            return theProperties.getProperty(theConfigKey.getPropertyName(), theConfigKey.getDefaultValue().toString());
        }
    }

    /**
     * Helper method for getting the string value for a property key.
     *
     * @param theProperties A Properties object to pull values from.
     * @param isRequired Is a property required.
     * @param thePropertyName The name of the property for logging and retrieval purposes.
     * @param theValueClass The class the value should be validated against and returned as.
     * @param theDefaultValue The default value of the configuration parameter.
     * @return The String value of the parameter or the default.
     * @throws InvalidPropertyException if the property is required and not set.
     */
    public static String getAndDefault(final Properties theProperties, final boolean isRequired, final String thePropertyName, final Class theValueClass, final Object theDefaultValue) throws InvalidPropertyException {
        checkNotNull(theProperties, "Can not getAndDefault with a null {} object, theProperties={}, isRequired={}, thePropertyName={}, theValueClass={},theDefaultValue={}", "theProperties", theProperties, isRequired, thePropertyName, theValueClass, theDefaultValue);
        checkNotNull(thePropertyName, "Can not getAndDefault with a null {} object, theProperties={}, isRequired={}, thePropertyName={}, theValueClass={},theDefaultValue={}", "thePropertyName", theProperties, isRequired, thePropertyName, theValueClass, theDefaultValue);
        checkArgument(!thePropertyName.isEmpty(), "Can not getAndDefault with an empty {} object, theProperties={}, isRequired={}, thePropertyName={}, theValueClass={},theDefaultValue={}", "thePropertyName", theProperties, isRequired, thePropertyName, theValueClass, theDefaultValue);
        checkNotNull(theValueClass, "Can not getAndDefault with a null {} object, theProperties={}, isRequired={}, thePropertyName={}, theValueClass={},theDefaultValue={}", "theValueClass", theProperties, isRequired, thePropertyName, theValueClass, theDefaultValue);
        if (isRequired) {
            String value = theProperties.getProperty(thePropertyName);
            if (value == null) {
                InvalidPropertyException e = new InvalidPropertyException(String.format("Property is required but is not set or is null. PROPERTY=%s VALUE=%s", thePropertyName, null));
                e.setPropertyName(thePropertyName);
                e.setExpectedPropertyType(theValueClass);
                throw e;
            }
            return value;
        } else {
            if (theDefaultValue == null) {
                return theProperties.getProperty(thePropertyName);
            } else {
                return theProperties.getProperty(thePropertyName, theDefaultValue.toString());
            }
        }
    }

    /**
     * Gets a parameter from a {@link Properties} object as an Integer.
     *
     * @param theLog The logger from the config key object that is using the helper method.
     * @param theProperties A Properties object to pull values from.
     * @param isRequired Is a property required.
     * @param theEnumName The name of the config key enumeration for logging purposes.
     * @param thePropertyName The name of the property for logging and retrieval purposes.
     * @param theValueClass The class the value should be validated against and returned as.
     * @param theDefaultValue The default value of the configuration parameter.
     * @return An Integer or null.
     * @throws InvalidPropertyException If the property does not exist or is not an Integer.
     */
    public static Integer getInteger(final Logger theLog, final Properties theProperties, final boolean isRequired, final String theEnumName, final String thePropertyName, final Class theValueClass, final Object theDefaultValue) throws InvalidPropertyException {
        checkNotNull(theProperties, "Can not getInteger property with a null theProperties.");
        return (Integer) getObject(theLog, theProperties, isRequired, theEnumName, thePropertyName, theValueClass, theDefaultValue);
    }

    /**
     * Gets a parameter from a {@link Properties} object as an Boolean.
     *
     * @param theLog The logger from the config key object that is using the helper method.
     * @param theProperties A Properties object to pull values from.
     * @param isRequired Is a property required.
     * @param theEnumName The name of the config key enumeration for logging purposes.
     * @param thePropertyName The name of the property for logging and retrieval purposes.
     * @param theValueClass The class the value should be validated against and returned as.
     * @param theDefaultValue The default value of the configuration parameter.
     * @return An Boolean or null.
     * @throws InvalidPropertyException If the property does not exist or is not an Boolean.
     */
    public static Boolean getBoolean(final Logger theLog, final Properties theProperties, final boolean isRequired, final String theEnumName, final String thePropertyName, final Class theValueClass, final Object theDefaultValue) throws InvalidPropertyException {
        checkNotNull(theProperties, "Can not getBoolean property with a null theProperties.");
        return (Boolean) getObject(theLog, theProperties, isRequired, theEnumName, thePropertyName, theValueClass, theDefaultValue);
    }

    /**
     * Gets a parameter from a {@link Properties} object as a String.
     *
     * @param theLog The logger from the config key object that is using the helper method.
     * @param theProperties A Properties object to pull values from.
     * @param theEnumName The name of the enum in the config key.
     * @param theConfigKey The ConfigKey object we are getting for.
     * @return An String or null.
     * @throws InvalidPropertyException If the property does not exist or is not an String.
     */
    public static String getString(final Logger theLog, final Properties theProperties, final String theEnumName, final ConfigKey theConfigKey) throws InvalidPropertyException {
        checkNotNull(theProperties, "Can not getString property with a null theProperties.");
        return (String) getObject(theLog, theProperties, theEnumName, theConfigKey);
    }

    /**
     * Gets a parameter from a {@link Properties} object as a String.
     *
     * @param theLog The logger from the config key object that is using the helper method.
     * @param theProperties A Properties object to pull values from.
     * @param isRequired Is a property required.
     * @param theEnumName The name of the config key enumeration for logging purposes.
     * @param thePropertyName The name of the property for logging and retrieval purposes.
     * @param theValueClass The class the value should be validated against and returned as.
     * @param theDefaultValue The default value of the configuration parameter.
     * @return An String or null.
     * @throws InvalidPropertyException If the property does not exist or is not an String.
     */
    public static String getString(final Logger theLog, final Properties theProperties, final boolean isRequired, final String theEnumName, final String thePropertyName, final Class theValueClass, final Object theDefaultValue) throws InvalidPropertyException {
        checkNotNull(theProperties, "Can not getString property with a null theProperties.");
        return (String) getObject(theLog, theProperties, isRequired, theEnumName, thePropertyName, theValueClass, theDefaultValue);
    }

    /**
     * Gets a parameter from a {@link Properties} object as a Long.
     *
     * @param theLog The logger from the config key object that is using the helper method.
     * @param theProperties A Properties object to pull values from.
     * @param isRequired Is a property required.
     * @param theEnumName The name of the config key enumeration for logging purposes.
     * @param thePropertyName The name of the property for logging and retrieval purposes.
     * @param theValueClass The class the value should be validated against and returned as.
     * @param theDefaultValue The default value of the configuration parameter.
     * @return An Long or null.
     * @throws InvalidPropertyException If the property does not exist or is not an String.
     */
    public static Long getLong(final Logger theLog, final Properties theProperties, final boolean isRequired, final String theEnumName, final String thePropertyName, final Class theValueClass, final Object theDefaultValue) throws InvalidPropertyException {
        checkNotNull(theProperties, "Can not getLong property with a null theProperties.");
        return (Long) getObject(theLog, theProperties, isRequired, theEnumName, thePropertyName, theValueClass, theDefaultValue);
    }

}
