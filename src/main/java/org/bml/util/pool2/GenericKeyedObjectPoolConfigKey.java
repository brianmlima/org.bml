/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bml.util.pool2;

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
import java.beans.PropertyVetoException;
import java.util.Properties;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.bml.util.exception.InvalidPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enumeration for configuration keys related to a {@link GenericKeyedObjectPoolConfig}.
 *
 * Has configuration keys and helper methods for minimum property validation and
 * a configuration utility method.
 *
 * @author Brian M. Lima
 */
public enum GenericKeyedObjectPoolConfigKey {

    /**
     * A pool configuration key.
     */
    MAX_IDLE_PER_KEY(false, GenericKeyedObjectPoolConfig.DEFAULT_MAX_IDLE_PER_KEY, Integer.class, "MAX_IDLE_PER_KEY"),
    /**
     * A pool configuration key.
     */
    MAX_TOTAL(false, GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL, Integer.class, "MAX_TOTAL"),
    /**
     * A pool configuration key.
     */
    MAX_TOTAL_PER_KEY(false, GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL_PER_KEY, Integer.class, "MAX_TOTAL_PER_KEY"),
    /**
     * A pool configuration key.
     */
    MIN_IDLE_PER_KEY(false, GenericKeyedObjectPoolConfig.DEFAULT_MIN_IDLE_PER_KEY, Integer.class, "MIN_IDLE_PER_KEY"),
    /**
     * A pool configuration key.
     */
    BLOCK_WHEN_EXHAUSTED(false, GenericKeyedObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED, Boolean.class, "BLOCK_WHEN_EXHAUSTED"),
    /**
     * A pool configuration key.
     */
    EVICTION_POLICY_CLASS_NAME(false, GenericKeyedObjectPoolConfig.DEFAULT_EVICTION_POLICY_CLASS_NAME, String.class, "EVICTION_POLICY_CLASS_NAME"),
    /**
     * A pool configuration key.
     */
    JMX_ENABLE(false, GenericKeyedObjectPoolConfig.DEFAULT_JMX_ENABLE, Boolean.class, "JMX_ENABLE"),
    /**
     * A pool configuration key.
     */
    JMX_NAME_PREFIX(false, GenericKeyedObjectPoolConfig.DEFAULT_JMX_NAME_PREFIX, String.class, "JMX_NAME_PREFIX"),
    /**
     * A pool configuration key.
     */
    LIFO(false, GenericKeyedObjectPoolConfig.DEFAULT_LIFO, Boolean.class, "LIFO"),
    /**
     * A pool configuration key.
     */
    MAX_WAIT_MILLIS(false, GenericKeyedObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS, Long.class, "MAX_WAIT_MILLIS"),
    /**
     * A pool configuration key.
     */
    MIN_EVICTABLE_IDLE_TIME_MILLIS(false, GenericKeyedObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS, Long.class, "MIN_EVICTABLE_IDLE_TIME_MILLIS"),
    /**
     * A pool configuration key.
     */
    NUM_TESTS_PER_EVICTION_RUN(false, GenericKeyedObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN, Integer.class, "NUM_TESTS_PER_EVICTION_RUN"),
    /**
     * A pool configuration key.
     */
    SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS(false, GenericKeyedObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS, Long.class, "SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS"),
    /**
     * A pool configuration key.
     */
    TEST_ON_BORROW(false, GenericKeyedObjectPoolConfig.DEFAULT_TEST_ON_BORROW, Boolean.class, "TEST_ON_BORROW"),
    /**
     * A pool configuration key.
     */
    TEST_ON_RETURN(false, GenericKeyedObjectPoolConfig.DEFAULT_TEST_ON_RETURN, Boolean.class, "TEST_ON_RETURN"),
    /**
     * A pool configuration key.
     */
    TEST_WHILE_IDLE(false, GenericKeyedObjectPoolConfig.DEFAULT_TEST_WHILE_IDLE, Boolean.class, "TEST_WHILE_IDLE"),
    /**
     * A pool configuration key.
     */
    TIME_BETWEEN_EVICTION_RUNS_MILLIS(false, GenericKeyedObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS, Long.class, "TIME_BETWEEN_EVICTION_RUNS_MILLIS");

    /**
     * The standard sl4j logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(GenericKeyedObjectPoolConfigKey.class);

    /**
     * Is the config key required.
     */
    private final boolean required;
    /**
     * the keys default value.
     */
    private final Object defaultValue;
    /**
     * The class of the value.
     */
    private final Class valueClass;
    /**
     * The name of the property.
     */
    private final String thePropertyName;

    /**
     * Create a new GenericKeyedObjectPoolConfigKey enum entry.
     *
     * @param required Is the config key required.
     * @param defautlValue the keys default value.
     * @param valueClass The class of the value.
     * @param thePropertyName The name of the property.
     */
    private GenericKeyedObjectPoolConfigKey(final boolean required, final Object defautlValue, final Class valueClass, final String thePropertyName) {
        this.required = required;
        this.defaultValue = defautlValue;
        this.valueClass = valueClass;
        this.thePropertyName = thePropertyName;
    }

    /**
     * Uses the passed Properties to configure the passed GenericKeyedObjectPoolConfig.
     *
     * @param theGenericKeyedObjectPoolConfig a GenericKeyedObjectPoolConfig to configure.
     * @param theProperties A properties object containing the minimum required properties to configure a GenericKeyedObjectPoolConfig.
     * @throws InvalidPropertyException If there is an issue with the properties or if the GenericKeyedObjectPoolConfig veto's a value.
     * @pre theComboPooledDataSource!=null
     * @pre theProperties!=null
     */
    public static void configure(final GenericKeyedObjectPoolConfig theGenericKeyedObjectPoolConfig, final Properties theProperties) throws InvalidPropertyException {
        checkNotNull(theGenericKeyedObjectPoolConfig, "Can not configure a null theComboPooledDataSource.");
        checkNotNull(theProperties, "Can not configure with a null theProperties.");
        for (GenericKeyedObjectPoolConfigKey key : GenericKeyedObjectPoolConfigKey.values()) {
            try {
                GenericKeyedObjectPoolConfigKey.set(key, theGenericKeyedObjectPoolConfig, theProperties);
            } catch (PropertyVetoException ex) {
                LOG.error(String.format("A PropertyVetoException was encountered while configuring a GenericKeyedObjectPoolConfig. PROPERTY=%s VALUE=%s", key.name(), key.getAndDefault(theProperties)), ex);
                throw new InvalidPropertyException(String.format("A PropertyVetoException was encountered while configuring a GenericKeyedObjectPoolConfig. The GenericKeyedObjectPoolConfig will not except the property value. PROPERTY=%s VALUE=%s", key.name(), key.getAndDefault(theProperties)));
            }
        }
    }

    /**
     * Sets a configuration parameter in the passed {@link ComboPooledDataSource}.
     * An exception is thrown in the three error cases.
     * <ol>
     * <li>The {@link PoolConfigKey} does not exist in the {@link Properties} object.</li>
     * <li>The {@link PoolConfigKey} value is required and is null in the {@link Properties} object.</li>
     * <li>The {@link PoolConfigKey} value is set but the value in the {@link Properties} object is not of the expected type.</li>
     * </ol>
     *
     * @param theGenericKeyedObjectPoolConfigKey The configuration key to set.
     * @param theGenericKeyedObjectPoolConfig The GenericKeyedObjectPoolConfig to be configured.
     * @param theProperties The Properties object.
     * @throws PropertyVetoException If the ComboPooledDataSource rejects a property.
     * @throws InvalidPropertyException if the property is required and not set.
     * @pre thePoolConfigKey!=null
     * @pre theGenericKeyedObjectPoolConfig!=null
     * @pre theProperties!=null
     */
    private static void set(final GenericKeyedObjectPoolConfigKey theGenericKeyedObjectPoolConfigKey, final GenericKeyedObjectPoolConfig theGenericKeyedObjectPoolConfig, final Properties theProperties) throws PropertyVetoException, InvalidPropertyException {
        checkNotNull(theGenericKeyedObjectPoolConfigKey, "Can not set a parameter with a null theGenericKeyedObjectPoolConfigKey");
        checkNotNull(theGenericKeyedObjectPoolConfig, String.format("Can not set property %s with a null theGenericKeyedObjectPoolConfig.", theGenericKeyedObjectPoolConfigKey.name()));
        checkNotNull(theProperties, String.format("Can not set property %s with a null theProperties.", theGenericKeyedObjectPoolConfigKey.name()));
        //GenericKeyedObjectPoolConfigKey theGenericKeyedObjectPoolConfigKey

        final GenericKeyedObjectPoolConfigKey key = theGenericKeyedObjectPoolConfigKey;
        final GenericKeyedObjectPoolConfig config = theGenericKeyedObjectPoolConfig;
        LOG.trace("Setting KEY {} , TYPE {}, PROPERTIES {}", key.name(), key.valueClass, theProperties);

        switch (key) {
            case MAX_IDLE_PER_KEY:
                config.setMaxIdlePerKey(key.getInteger(theProperties));
                break;
            case MAX_TOTAL:
                config.setMaxTotal(key.getInteger(theProperties));
                break;
            case MAX_TOTAL_PER_KEY:
                config.setMaxTotalPerKey(key.getInteger(theProperties));
                break;
            case MIN_IDLE_PER_KEY:
                config.setMinIdlePerKey(key.getInteger(theProperties));
                break;
            case BLOCK_WHEN_EXHAUSTED:
                config.setBlockWhenExhausted(key.getBoolean(theProperties));
                break;
            case EVICTION_POLICY_CLASS_NAME:
                config.setEvictionPolicyClassName(key.getString(theProperties));
                break;
            case JMX_ENABLE:
                config.setJmxEnabled(key.getBoolean(theProperties));
                break;
            case JMX_NAME_PREFIX:
                config.setJmxNamePrefix(key.getString(theProperties));
                break;
            case LIFO:
                config.setLifo(key.getBoolean(theProperties));
                break;
            case MAX_WAIT_MILLIS:
                LOG.trace("KEY {} TYPE {} PROPERTIES {}", key.name(), key.valueClass, theProperties);
                LOG.trace("Setting {} to {}, TYPE {}", key.name(), key.valueClass, key.getLong(theProperties));
                config.setMaxWaitMillis(key.getLong(theProperties));
                break;
            case MIN_EVICTABLE_IDLE_TIME_MILLIS:
                config.setMinEvictableIdleTimeMillis(key.getLong(theProperties));
                break;
            case NUM_TESTS_PER_EVICTION_RUN:
                config.setNumTestsPerEvictionRun(key.getInteger(theProperties));
                break;
            case SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS:
                config.setSoftMinEvictableIdleTimeMillis(key.getLong(theProperties));
                break;
            case TEST_ON_BORROW:
                config.setTestOnBorrow(key.getBoolean(theProperties));
                break;
            case TEST_ON_RETURN:
                config.setTestOnReturn(key.getBoolean(theProperties));
                break;
            case TEST_WHILE_IDLE:
                config.setTestWhileIdle(key.getBoolean(theProperties));
                break;
            case TIME_BETWEEN_EVICTION_RUNS_MILLIS:
                config.setTimeBetweenEvictionRunsMillis(key.getLong(theProperties));
                break;
            default:
                break;
        }
    }

    /**
     * Helper method for getting the string value for a property key.
     *
     * @param theProperties A Properties object.
     * @return The String value of the parameter or the default.
     * @throws InvalidPropertyException if the property is required and not set.
     */
    private String getAndDefault(final Properties theProperties) throws InvalidPropertyException {
        if (this.required) {
            String value = theProperties.getProperty(this.thePropertyName);
            if (value == null) {
                InvalidPropertyException e = new InvalidPropertyException(String.format("Property is required but is not set or is null. PROPERTY=%s VALUE=%s", this.thePropertyName, null));
                e.setPropertyName(this.thePropertyName);
                e.setExpectedPropertyType(this.valueClass);
                throw e;
            }
            return value;
        } else {
            return theProperties.getProperty(this.thePropertyName, this.defaultValue.toString());
        }
    }

    /**
     * Convenience method for getting a value {@link Object} from a {@link Properties}.
     *
     * @param properties A Properties object to pull values from.
     * @return The value as the type {@link PoolConfigKeys#valueClass} cast as an Object.
     * @throws InvalidPropertyException If type is a numeric, exists, but can not be parsed.
     */
    private Object getObject(final Properties properties) throws InvalidPropertyException {
        checkNotNull(properties, String.format("Can not get an Integer value for key %s from a null Properties.", this.name()));
        LOG.trace("Attempting to get and or default KEY {} , TYPE {} , DEFAULT {}", this.name(), this.valueClass, this.defaultValue);
        String sValue = this.getAndDefault(properties);
        LOG.trace("Retrieved value {}  for KEY {} , TYPE {} , DEFAULT {}", sValue, this.name(), this.valueClass, this.defaultValue);
        if (this.valueClass == String.class) {
            return sValue;
        } else if (this.valueClass == Integer.class) {
            try {
                return Integer.valueOf(sValue);
            } catch (NumberFormatException e) {
                throw new InvalidPropertyException(String.format("NumberFormatException encountered while attempting to parse Integer from {KEY=%s,VALUE=%s} MESSAGE=%s", this.name(), sValue, e.getMessage()));
            }
        } else if (this.valueClass == Boolean.class) {
            return Boolean.valueOf(sValue);
        } else if (this.valueClass == Long.class) {
            return Long.valueOf(sValue);
        }
        return null;
    }

    /**
     * Gets a parameter from a {@link Properties} object as an Integer.
     *
     * @param theProperties A Properties object.
     * @return An Integer or null.
     * @throws InvalidPropertyException If the property does not exist or is not an Integer.
     */
    private Integer getInteger(final Properties theProperties) throws InvalidPropertyException {
        checkNotNull(theProperties, "Can not getInteger property with a null theProperties.");
        return (Integer) getObject(theProperties);
    }

    /**
     * Gets a parameter from a {@link Properties} object as an Boolean.
     *
     * @param theProperties A Properties object.
     * @return An Boolean or null.
     * @throws InvalidPropertyException If the property does not exist or is not an Boolean.
     */
    private Boolean getBoolean(final Properties theProperties) throws InvalidPropertyException {
        checkNotNull(theProperties, "Can not getBoolean property with a null theProperties.");
        return (Boolean) getObject(theProperties);
    }

    /**
     * Gets a parameter from a {@link Properties} object as a String.
     *
     * @param theProperties A Properties object.
     * @return An String or null.
     * @throws InvalidPropertyException If the property does not exist or is not an String.
     */
    private String getString(final Properties theProperties) throws InvalidPropertyException {
        checkNotNull(theProperties, "Can not getString property with a null theProperties.");
        return (String) getObject(theProperties);
    }

    /**
     * Gets a parameter from a {@link Properties} object as a Long.
     *
     * @param theProperties A Properties object.
     * @return An Long or null.
     * @throws InvalidPropertyException If the property does not exist or is not an String.
     */
    private Long getLong(final Properties theProperties) throws InvalidPropertyException {
        checkNotNull(theProperties, "Can not getLong property with a null theProperties.");
        return (Long) getObject(theProperties);
    }

}
