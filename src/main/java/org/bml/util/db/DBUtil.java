package org.bml.util.db;
/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2014 Brian M. Lima
 * %%
 * This file is part of ORG.BML.
 *     ORG.BML is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *     ORG.BML is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with ORG.BML.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.net.InetAddress;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbutils.DbUtils;
import org.bml.util.PropertiesUtil;
import org.bml.util.exception.InvalidPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of database utility methods.
 * This class is centered around the use of {@link ComboPooledDataSource} as a
 * {@link DataSource}.
 *
 * @author Brian M. Lima
 */
public final class DBUtil {

    /**
     * Disables the default constructor.
     *
     * @throws InstantiationException Always.
     */
    private DBUtil() throws InstantiationException {
        throw new InstantiationException("Instances of this type are forbidden.");
    }
    /**
     * The standard sl4j logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DBUtil.class);

    /**
     * The query used to test {@link DataSource} objects.
     * <code>"Select 1 as result;"</code>
     */
    public static final String TEST_QUERY = "Select 1 as result;";

    /**
     * Sets parameters on the {@link CallableStatement} in the order they are passed.
     *
     * @param statement The statement to set parameters for.
     * @param values values to add to the statement in the order they should be set.
     * @throws SQLException on SQL error.
     */
    public static void setParameters(final CallableStatement statement, final Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            statement.setObject(i + 1, values[i]);
        }
    }

    /**
     * Creates a ComboPooledDataSource from a properly populated Properties
     * object.
     *
     * @param theProperties A Properties object containing the required properties for creating a ComboPooledDataSource.
     * @return a ComboPooledDataSource or null on error.
     * @throws InvalidPropertyException If a property is not of the correct type.
     * @pre theProperties!=null
     */
    public static ComboPooledDataSource makeComboPooledDataSource(final Properties theProperties) throws InvalidPropertyException {
        checkNotNull(theProperties, "Can not make a ComboPooledDataSource with a null Properties object.");
        ComboPooledDataSource aComboPooledDataSource = null;
        LOG.debug("Attempting creation of ComboPooledDataSource. Properties={}", theProperties.toString());
        try {
            aComboPooledDataSource = new ComboPooledDataSource();
            PoolConfigKey.configure(aComboPooledDataSource, theProperties);
        } catch (InvalidPropertyException ex) {
            LOG.error(String.format("IllegalArgumentException caught while configuring a ComboPooledDataSource. Properties=%s", theProperties.toString()), ex);
            throw ex;
        }
        LOG.info("Successful creation of ComboPooledDataSource. Properties={}", theProperties.toString());
        return aComboPooledDataSource;
    }

    /**
     * Creates a new ComboPooledDataSource from a properties object using properties found with the passed prefix.
     *
     * @param propertyPrefix the prefix for a datasources properties
     * @param theProperties a properties obejct containing properties necessary to build a ComboPooledDataSource.
     * @return a ComboPooledDataSource
     */
    public static ComboPooledDataSource makeComboPooledDataSource(final String propertyPrefix, final Properties theProperties) {
        checkNotNull(theProperties, "Can not create a new DataSource with the prefix %s with a null properties object.", propertyPrefix);
        checkNotNull(propertyPrefix, "Can not create a new DataSource with a null propertyPrefix object. Properties=%s", theProperties);
        Properties devProps = PropertiesUtil.copyProperties(propertyPrefix, theProperties);
        try {
            ComboPooledDataSource dataSource = DBUtil.makeComboPooledDataSource(devProps);
            if (DBUtil.testDataSource(dataSource)) {
                return dataSource;
            } else {
                LOG.error(String.format("DataSource for prefix %s , Properties %s did not pass testing", propertyPrefix, theProperties));
                DBUtil.closeQuietly(dataSource);
                return null;
            }
        } catch (InvalidPropertyException ex) {
            LOG.error(String.format("Can not create an instance of ComboPooledDataSource for the property prefix %s from Properties %s", propertyPrefix, theProperties), ex);
            return null;
        }
    }

    /**
     * Tests a {@link DataSource} with a test query from {@link #TEST_QUERY}.
     *
     * @param theDataSource A DataSource
     * @return True if the test was successful and false otherwise.
     * @pre theDataSource!=null
     */
    public static boolean testDataSource(final DataSource theDataSource) {
        checkNotNull(theDataSource, "Can not test a null DataSource. testQuery=%s", TEST_QUERY);
        Connection aConnection = null;
        try {
            aConnection = theDataSource.getConnection();
            testConnection(aConnection);
            return true;
        } catch (SQLException ex) {
            LOG.error(String.format("SQLException caught while testing DataSource. testQuery=%s", TEST_QUERY), ex);
            return false;
        } finally {
            if (aConnection != null) {
                DbUtils.closeQuietly(aConnection);
            }
        }
    }

    /**
     * Runs the {@link #TEST_QUERY} on a {@link Connection} and returns true on success, false otherwise.
     *
     * @param theConnection An SQL {@link Connection} to test.
     * @return true if the {@link Connection} passes the test. False otherwise.
     * @pre theConnection!=null;
     */
    public static boolean testConnection(final Connection theConnection) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = theConnection.prepareStatement(TEST_QUERY);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                LOG.error("No results were returned by the test query {} expected one or more.", TEST_QUERY);
                return false;
            }
            return true;
        } catch (SQLException ex) {
            LOG.error("SQLException caught while testing ComboPooledDataSource.", ex);
            return false;
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
        }
    }

    /**
     * Closes a {@link ComboPooledDataSource} quietly, tolerating nulls.
     *
     * @param theComboPooledDataSource A {@link ComboPooledDataSource} to close.
     */
    public static void closeQuietly(final ComboPooledDataSource theComboPooledDataSource) {
        if (theComboPooledDataSource != null) {
            theComboPooledDataSource.close();
        }
    }

    /**
     * Sets a String in a {@link PreparedStatement}. Handles null strings and can null a string on empty.
     *
     * @param theString The String to set to
     * @param theColumnIndex The column to set
     * @param nullOnEmpty Set as null if the string is empty.
     * @param nullString Set to a String that should be entered as null
     * @param thePreparedStatement The PreparedStatement to add to.
     * @throws SQLException If there is an issue adding the String to the PreparedStatement
     * @pre thePreparedStatement!=null
     * @pre theColumnIndex > 0
     */
    public static void setString(final String theString, final int theColumnIndex, final boolean nullOnEmpty, final String nullString, final PreparedStatement thePreparedStatement) throws SQLException {
        checkNotNull(thePreparedStatement, "Can not setString on a null PreparedStatement STRING=%s COLUMN_INDEX=%s", theString, theColumnIndex);
        checkArgument(theColumnIndex > 0, "Can not setString with a column index that is not greater than 0. STRING=%s COLUMN_INDEX=%s", theString, theColumnIndex);
        if (theString == null) {
            thePreparedStatement.setNull(theColumnIndex, Types.VARCHAR);
            return;
        }
        if (nullString != null && theString.equals(nullString)) {
            thePreparedStatement.setNull(theColumnIndex, Types.VARCHAR);
            return;
        }
        if (nullOnEmpty && theString.isEmpty()) {
            thePreparedStatement.setNull(theColumnIndex, Types.VARCHAR);
        } else {
            thePreparedStatement.setString(theColumnIndex, theString);
        }
    }

    /**
     * Sets a Integer in a {@link PreparedStatement}. Handles null Integer.
     *
     * @param theInteger The Integer to set to
     * @param theColumnIndex The column to set
     * @param thePreparedStatement The PreparedStatement to add to.
     * @throws SQLException If there is an issue adding the Integer to the PreparedStatement
     * @pre thePreparedStatement!=null
     * @pre theColumnIndex > 0
     */
    public static void setInteger(final Integer theInteger, final int theColumnIndex, final PreparedStatement thePreparedStatement) throws SQLException {
        checkNotNull(thePreparedStatement, "Can not setInteger on a null PreparedStatement Integer=%s COLUMN_INDEX=%s", theInteger, theColumnIndex);
        checkArgument(theColumnIndex > 0, "Can not setInteger with a column index that is not greater than 0. Integer=%s COLUMN_INDEX=%s", theInteger, theColumnIndex);
        if (theInteger == null) {
            thePreparedStatement.setNull(theColumnIndex, Types.INTEGER);
        } else {
            thePreparedStatement.setInt(theColumnIndex, theInteger);
        }
    }

    /**
     * Sets a Long in a {@link PreparedStatement}. Handles null Long.
     *
     * @param theLong The Long to set to
     * @param theColumnIndex The column to set
     * @param thePreparedStatement The PreparedStatement to add to.
     * @throws SQLException If there is an issue adding the Long to the PreparedStatement
     * @pre thePreparedStatement!=null
     * @pre theColumnIndex > 0
     */
    public static void setLong(final Long theLong, final int theColumnIndex, final PreparedStatement thePreparedStatement) throws SQLException {
        checkNotNull(thePreparedStatement, "Can not setLong on a null PreparedStatement Long=%s COLUMN_INDEX=%s", theLong, theColumnIndex);
        checkArgument(theColumnIndex > 0, "Can not setLong with a column index that is not greater than 0. Long=%s COLUMN_INDEX=%s", theLong, theColumnIndex);
        if (theLong == null) {
            thePreparedStatement.setNull(theColumnIndex, Types.BIGINT);
        } else {
            thePreparedStatement.setLong(theColumnIndex, theLong);
        }
    }

    /**
     * Sets a Double in a {@link PreparedStatement}. Handles null Double.
     *
     * @param theDouble The Double to set to
     * @param theColumnIndex The column to set
     * @param thePreparedStatement The PreparedStatement to add to.
     * @throws SQLException If there is an issue adding the Double to the PreparedStatement
     * @pre thePreparedStatement!=null
     * @pre theColumnIndex > 0
     */
    public static void setDouble(final Double theDouble, final int theColumnIndex, final PreparedStatement thePreparedStatement) throws SQLException {
        checkNotNull(thePreparedStatement, "Can not setDouble on a null PreparedStatement Double=%s COLUMN_INDEX=%s", theDouble, theColumnIndex);
        checkArgument(theColumnIndex > 0, "Can not setDouble with a column index that is not greater than 0. Double=%s COLUMN_INDEX=%s", theDouble, theColumnIndex);
        if (theDouble == null) {
            thePreparedStatement.setNull(theColumnIndex, Types.DOUBLE);
        } else {
            thePreparedStatement.setDouble(theColumnIndex, theDouble);
        }
    }

    /**
     * Sets a Date in a {@link PreparedStatement}. Handles null Date.
     *
     * @param theDate The Date to set to
     * @param theColumnIndex The column to set
     * @param thePreparedStatement The PreparedStatement to add to.
     * @throws SQLException If there is an issue adding the Date to the PreparedStatement
     * @pre thePreparedStatement!=null
     * @pre theColumnIndex > 0
     */
    public static void setDate(final Date theDate, final int theColumnIndex, final PreparedStatement thePreparedStatement) throws SQLException {
        checkNotNull(thePreparedStatement, "Can not setDate on a null PreparedStatement Date=%s COLUMN_INDEX=%s", theDate, theColumnIndex);
        checkArgument(theColumnIndex > 0, "Can not setDate with a column index that is not greater than 0. Date=%s COLUMN_INDEX=%s", theDate, theColumnIndex);
        if (theDate == null) {
            thePreparedStatement.setNull(theColumnIndex, Types.TIMESTAMP);
        } else {
            thePreparedStatement.setTimestamp(theColumnIndex, new Timestamp(theDate.getTime()));
        }
    }

    /**
     * Sets a Boolean in a {@link PreparedStatement}. Handles null Date.
     *
     * @param theBoolean The Boolean to set to
     * @param theColumnIndex The column to set
     * @param thePreparedStatement The PreparedStatement to add to.
     * @throws SQLException If there is an issue adding the Boolean to the PreparedStatement
     * @pre thePreparedStatement!=null
     * @pre theColumnIndex > 0
     */
    public static void setBoolean(final Boolean theBoolean, final int theColumnIndex, final PreparedStatement thePreparedStatement) throws SQLException {
        checkNotNull(thePreparedStatement, "Can not setBoolean on a null PreparedStatement Boolean=%s COLUMN_INDEX=%s", theBoolean, theColumnIndex);
        checkArgument(theColumnIndex > 0, "Can not setBoolean with a column index that is not greater than 0. Boolean=%s COLUMN_INDEX=%s", theBoolean, theColumnIndex);
        if (theBoolean == null) {
            thePreparedStatement.setNull(theColumnIndex, Types.BOOLEAN);
        } else {
            thePreparedStatement.setBoolean(theColumnIndex, theBoolean);
        }
    }

    /**
     * Sets a InetAddress in a {@link PreparedStatement}. Handles null InetAddress.
     * uses the value of InetAddress.getHostAddress()
     *
     * @param theInetAddress The InetAddress to set to
     * @param theColumnIndex The column to set
     * @param thePreparedStatement The PreparedStatement to add to.
     * @throws SQLException If there is an issue adding the InetAddress to the PreparedStatement
     * @pre thePreparedStatement!=null
     * @pre theColumnIndex > 0
     */
    public static void setINetHostAddress(final InetAddress theInetAddress, final int theColumnIndex, final PreparedStatement thePreparedStatement) throws SQLException {
        checkNotNull(thePreparedStatement, "Can not setINetHostAddress on a null PreparedStatement InetAddress=%s COLUMN_INDEX=%s", theInetAddress, theColumnIndex);
        checkArgument(theColumnIndex > 0, "Can not setINetHostAddress with a column index that is not greater than 0. InetAddress=%s COLUMN_INDEX=%s", theInetAddress, theColumnIndex);
        if (theInetAddress == null) {
            thePreparedStatement.setNull(theColumnIndex, Types.OTHER);
        } else {
            thePreparedStatement.setString(theColumnIndex, theInetAddress.getHostAddress());
        }
    }

    /**
     * Sets a Object in a {@link PreparedStatement}. Handles null Object.
     *
     * @param theObject The Object to set to
     * @param theColumnIndex The column to set
     * @param thePreparedStatement The PreparedStatement to add to.
     * @throws SQLException If there is an issue adding the Object.toString to the PreparedStatement
     * @pre thePreparedStatement!=null
     * @pre theColumnIndex > 0
     */
    public static void setObjectAsString(final Object theObject, final int theColumnIndex, final PreparedStatement thePreparedStatement) throws SQLException {
        checkNotNull(thePreparedStatement, "Can not setObjectAsString on a null PreparedStatement Object=%s COLUMN_INDEX=%s", theObject, theColumnIndex);
        checkArgument(theColumnIndex > 0, "Can not setObjectAsString with a column index that is not greater than 0. Object=%s COLUMN_INDEX=%s", theObject, theColumnIndex);
        if (theObject == null) {
            thePreparedStatement.setNull(theColumnIndex, Types.VARCHAR);
        } else {
            thePreparedStatement.setString(theColumnIndex, theObject.toString());
        }
    }

}
