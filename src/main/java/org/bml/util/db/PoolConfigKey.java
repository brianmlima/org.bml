/**/
package org.bml.util.db;

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
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;
import org.bml.util.exception.InvalidPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enumeration for configuration keys related to a JDBC ComboPooledDataSource.
 *
 * Has configuration keys and helper methods for minimum property validation and
 * a configuration utility method.
 *
 * @author Brian M. Lima
 */
public enum PoolConfigKey {

    /**
     * The full name of the driver class to use.
     */
    JDBC_DRIVER(null, true, String.class),
    /**
     * The JDBC URL to connect to.
     */
    JDBC_URL(null, true, String.class),
    /**
     * The User to log in as.
     */
    DB_USER(null, true, String.class),
    /**
     * The Users password.
     */
    DB_PASS(null, true, String.class),
    /**
     * The query used to test connections.
     */
    PREFERED_TEST_QUERY("SELECT 1 AS dbcp_connection_test;", false, String.class),
    /**
     * Break and throw errors if there is an aquisition failure.
     */
    BREAK_AFTER_ACQUIRE_FAILURE("false", false, Boolean.class),
    /**
     * The Maximum number of connections allowed in the pool.
     */
    MAX_POOL_SIZE("20", false, Integer.class),
    /**
     * The number of milliseconds between checks for idle connections.
     */
    IDLE_CONNECTION_TEST_PERIOD("10000", false, Integer.class),
    /**
     * The pool should call commit when a connection is closed.
     */
    AUTO_COMMIT_ON_CLOSE("true", false, Boolean.class),
    /**
     * Print debugging data on connections that have not been returned.
     */
    DEBUG_UNRETURNED_CONNECTION_STACK_TRACES("true", false, Boolean.class),
    /**
     * The maximum number of milliseconds old a connection can be before it is destroyed.
     */
    MAX_CONNECTION_AGE("30000", false, Integer.class),
    /**
     * The number of connections to add to the pool when all connections are in use.
     */
    AQUIRE_INCREMENT("2", false, Integer.class),
    /**
     * The number of times to try an aquire a connection after one failed attempt.
     */
    AQUIRE_RETRY_ATTEMPTS("4", false, Integer.class),
    /**
     * The name of the connection test class to use.
     */
    CONNECTION_TESTER_CLASS_NAME(null, false, String.class),
    /**
     * Run the connection test on check-in.
     */
    TEST_CONNECTION_ON_CHECKIN("true", false, Boolean.class),
    /**
     * Run the connection test on check-out.
     */
    TEST_CONNECTION_ON_CHECKOUT("true", false, Boolean.class),
    /**
     * The maximum time in milliseconds a connection can sit idle before it is destroyed.
     */
    MAX_IDLE_TIME("120000", false, Integer.class),
    /**
     * The number of milliseconds a client calling getConnection() will wait
     * for a Connection to be checked-in or acquired when the pool is exhausted.
     * Zero means wait indefinitely. Setting any positive value will cause the
     * getConnection() call to time-out and break with an SQLException after
     * the specified number of milliseconds.
     */
    CHECKOUT_TIMEOUT("0", false, Integer.class);

    /**
     * The standard sl4j logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PoolConfigKey.class);

    /**
     * The String representation of the configuration keys default value.
     */
    private final String defaultValue;
    /**
     * Denotes if the property key is required.
     * If this is true and a property does not exist an IllegalFormatException
     * is thrown.
     */
    private final boolean required;
    /**
     * The property keys value type.
     * The value will be converted to this value type and methods will throw
     * an exception if the value is not of this type.
     */
    private final Class valueClass;

    /**
     * Creates a new PoolConfigKeys enumeration.
     *
     * @param defaultValue The String representation of the configuration keys default value.
     * @param required Denotes if the property key is required.
     * @param valueClass The property keys value type.
     * @throws NullPointerException If <code>valueClass==null</code>.
     * @throws IllegalArgumentException if <code>(valueClass != String.class && valueClass != Integer.class && valueClass != Boolean.class)</code>.
     * @pre valueClass!=null;
     * @pre (valueClass == String.class || valueClass == Integer.class || valueClass == Boolean.class)
     */
    PoolConfigKey(final String defaultValue, final boolean required, final Class valueClass) throws NullPointerException, IllegalArgumentException {
        checkNotNull(valueClass, "Can not create a new PoolConfigKey with a null valueClass");
        this.defaultValue = defaultValue;
        this.required = required;
        this.valueClass = valueClass;
    }

    /**
     * Uses the passed Properties to configure the passed ComboPooledDataSource.
     *
     * @param theComboPooledDataSource a ComboPooledDataSource to configure.
     * @param theProperties A properties object containing the minimum required properties to configure a ComboPooledDataSource.
     * @throws InvalidPropertyException If there is an issue with the properties or if the ComboPooledDataSource veto's a value.
     * @pre theComboPooledDataSource!=null
     * @pre theProperties!=null
     */
    public static void configure(final ComboPooledDataSource theComboPooledDataSource, final Properties theProperties) throws InvalidPropertyException {
        checkNotNull(theComboPooledDataSource, "Can not configure a null theComboPooledDataSource.");
        checkNotNull(theProperties, "Can not configure with a null theProperties.");
        for (PoolConfigKey key : PoolConfigKey.values()) {
            try {
                PoolConfigKey.set(key, theComboPooledDataSource, theProperties);
            } catch (PropertyVetoException ex) {
                LOG.error(String.format("A PropertyVetoException was encountered while configuring a ComboPooledDataSource. PROPERTY=%s VALUE=%s", key.name(), key.getAndDefault(theProperties)), ex);
                throw new InvalidPropertyException(String.format("A PropertyVetoException was encountered while configuring a ComboPooledDataSource. The ComboPooledDataSource will not except the property value. PROPERTY=%s VALUE=%s", key.name(), key.getAndDefault(theProperties)));
            }
        }
    }

    /**
     * Checks a {@link Properties} object for required PoolConfigKey.
     *
     * @param theProperties A properties object containing the minimum required properties to configure a ComboPooledDataSource.
     * @throws InvalidPropertyException if the property is required and not set.
     */
    public static void validateJDBCProps(final Properties theProperties) throws InvalidPropertyException {
        for (PoolConfigKey key : PoolConfigKey.values()) {
            if (key.isRequired()) {
                key.getAndDefault(theProperties); //should throw InvalidPropertyException if not set
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
     * @param thePoolConfigKey The configuration key to set.
     * @param theComboPooledDataSource The ComboPooledDataSource to be configured.
     * @param theProperties The Properties object.
     * @throws PropertyVetoException If the ComboPooledDataSource rejects a property.
     * @throws InvalidPropertyException if the property is required and not set.
     * @pre thePoolConfigKey!=null
     * @pre theComboPooledDataSource!=null
     * @pre theProperties!=null
     */
    private static void set(final PoolConfigKey thePoolConfigKey, final ComboPooledDataSource theComboPooledDataSource, final Properties theProperties) throws PropertyVetoException, InvalidPropertyException {
        checkNotNull(thePoolConfigKey, "Can not set a parameter with a null thePoolConfigKey");
        checkNotNull(theComboPooledDataSource, String.format("Can not set property %s with a null theComboPooledDataSource.", thePoolConfigKey.name()));
        checkNotNull(theProperties, String.format("Can not set property %s with a null theProperties.", thePoolConfigKey.name()));
        switch (thePoolConfigKey) {
            case JDBC_DRIVER://The full name of the driver class to use.
                theComboPooledDataSource.setDriverClass(JDBC_DRIVER.getString(theProperties));
                break;
            case JDBC_URL://The JDBC URL to connect to.
                theComboPooledDataSource.setJdbcUrl(JDBC_URL.getString(theProperties));
                break;
            case DB_USER://The User to log in as.
                theComboPooledDataSource.setUser(DB_USER.getString(theProperties));
                break;
            case DB_PASS://The Users password.
                theComboPooledDataSource.setPassword(DB_PASS.getString(theProperties));
                break;
            case PREFERED_TEST_QUERY://The query used to test connections.
                theComboPooledDataSource.setPreferredTestQuery(PREFERED_TEST_QUERY.getString(theProperties));
                break;
            case BREAK_AFTER_ACQUIRE_FAILURE://break if there is a problem during accuisition.
                theComboPooledDataSource.setBreakAfterAcquireFailure(BREAK_AFTER_ACQUIRE_FAILURE.getBoolean(theProperties));
                break;
            case MAX_POOL_SIZE://The Maximum number of connections allowed in the pool.
                theComboPooledDataSource.setMaxPoolSize(MAX_POOL_SIZE.getInteger(theProperties));
                break;
            case IDLE_CONNECTION_TEST_PERIOD://The number of milliseconds between checks for idle connections.
                theComboPooledDataSource.setIdleConnectionTestPeriod(IDLE_CONNECTION_TEST_PERIOD.getInteger(theProperties));
                break;
            case AUTO_COMMIT_ON_CLOSE://The pool should call comit when a connection is closed.
                theComboPooledDataSource.setAutoCommitOnClose(AUTO_COMMIT_ON_CLOSE.getBoolean(theProperties));
                break;
            case DEBUG_UNRETURNED_CONNECTION_STACK_TRACES://Print debuging data on connections that have not been returned.
                theComboPooledDataSource.setDebugUnreturnedConnectionStackTraces(DEBUG_UNRETURNED_CONNECTION_STACK_TRACES.getBoolean(theProperties));
                break;
            case MAX_CONNECTION_AGE://The maximum number of milliseconds old a connection can be before it is destroyed.
                theComboPooledDataSource.setMaxConnectionAge(MAX_CONNECTION_AGE.getInteger(theProperties));
                break;
            case AQUIRE_INCREMENT://The number of connections to add to the pool when all connections are in use.
                theComboPooledDataSource.setAcquireIncrement(AQUIRE_INCREMENT.getInteger(theProperties));
                break;
            case AQUIRE_RETRY_ATTEMPTS://The number of times to try an aquire a connection after one failed attempt.
                theComboPooledDataSource.setAcquireRetryAttempts(AQUIRE_RETRY_ATTEMPTS.getInteger(theProperties));
                break;
            case CONNECTION_TESTER_CLASS_NAME:
                theComboPooledDataSource.setConnectionTesterClassName(CONNECTION_TESTER_CLASS_NAME.getAndDefault(theProperties));
                break;
            case TEST_CONNECTION_ON_CHECKIN://Run the connection test on check-in.
                theComboPooledDataSource.setTestConnectionOnCheckin(TEST_CONNECTION_ON_CHECKIN.getBoolean(theProperties));
                break;
            case TEST_CONNECTION_ON_CHECKOUT://Run the connection test on check-out.
                theComboPooledDataSource.setTestConnectionOnCheckout(TEST_CONNECTION_ON_CHECKOUT.getBoolean(theProperties));
                break;
            case MAX_IDLE_TIME://The maximum time in milliseconds a connection can sit idle before it is destroyed.
                theComboPooledDataSource.setMaxIdleTime(MAX_IDLE_TIME.getInteger(theProperties));
                break;
            case CHECKOUT_TIMEOUT://The maximum time in milliseconds a data source will wait for a connection.
                theComboPooledDataSource.setCheckoutTimeout(CHECKOUT_TIMEOUT.getInteger(theProperties));
                break;
            default:
                break;
        }
    }

    /**
     * Gets a parameter from a {@link Properties} object as an Integer.
     *
     * @param properties A Properties object.
     * @return An Integer or null.
     * @throws InvalidPropertyException If the property does not exist or is not an Integer.
     */
    private Integer getInteger(final Properties properties) throws InvalidPropertyException {
        return (Integer) getObject(properties);
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
     * @param properties A Properties object.
     * @return An String or null.
     * @throws InvalidPropertyException If the property does not exist or is not an String.
     */
    private String getString(final Properties properties) throws InvalidPropertyException {
        return (String) getObject(properties);
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
            String value = theProperties.getProperty(this.name());
            if (value == null) {
                InvalidPropertyException e = new InvalidPropertyException(String.format("Property is required but is not set or is null. PROPERTY=%s VALUE=%s", this.name(), null));
                e.setPropertyName(this.name());
                e.setExpectedPropertyType(this.valueClass);
                throw e;
            }
            return value;
        } else {
            return theProperties.getProperty(this.name(), this.defaultValue);
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
        String sValue = this.getAndDefault(properties);
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
        }
        return null;
    }

    /**
     * Getter for defaultValue.
     *
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Getter for required.
     *
     * @return the required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Getter for valueClass.
     *
     * @return the valueClass
     */
    public Class getValueClass() {
        return valueClass;
    }

}
