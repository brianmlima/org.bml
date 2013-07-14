/**
 *   This file is part of org.bml.
 *
 *   org.bml is free software: you can redistribute it and/or modify it under the
 *   terms of the GNU General Public License as published by the Free Software
 *   Foundation, either version 3 of the License, or (at your option) any later
 *   version.
 *
 *   org.bml is distributed in the hope that it will be useful, but WITHOUT ANY
 *   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 *   A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License along with
 *   org.bml. If not, see <http://www.gnu.org/licenses/>.
 */


package org.bml.util.sql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.Conversion;
import org.bml.util.TimeUtils;

/**
 * @author Brian M. Lima
 */
public abstract class DBUtil {

  /**
   */
  private static final Log LOG = LogFactory.getLog(DBUtil.class);

  /**
   * Creates a ComboPooledDataSource from a properly populated Properties object
   *
   * @param theProperties
   * @return a ComboPooledDataSource or null on error
   * @throws IOException
   */
  public static ComboPooledDataSource makeComboPooledDataSource(Properties theProperties) throws IOException {
    ComboPooledDataSource aComboPooledDataSource = null;

    LOG.info("Begining creation of ComboPooledDataSource " + theProperties.toString());
    try {
      aComboPooledDataSource = new ComboPooledDataSource();
      aComboPooledDataSource.setDriverClass(theProperties.getProperty("JDBC_DRIVER"));
      aComboPooledDataSource.setJdbcUrl(theProperties.getProperty("JDBCURL"));
      aComboPooledDataSource.setUser(theProperties.getProperty("DBUSER"));
      aComboPooledDataSource.setPassword(theProperties.getProperty("DBPASS"));
      aComboPooledDataSource.setPreferredTestQuery("SELECT 1 AS dbcp_connection_test;");
      aComboPooledDataSource.setMaxPoolSize(20);
      aComboPooledDataSource.setIdleConnectionTestPeriod(10000);
      aComboPooledDataSource.setAutoCommitOnClose(true);

      aComboPooledDataSource.setDebugUnreturnedConnectionStackTraces(true);
      aComboPooledDataSource.setMaxConnectionAge(30000);
      aComboPooledDataSource.setAcquireIncrement(2);
      aComboPooledDataSource.setAcquireRetryAttempts(4);
      aComboPooledDataSource.setAcquireRetryDelay(500);
      aComboPooledDataSource.setTestConnectionOnCheckin(true);
      aComboPooledDataSource.setTestConnectionOnCheckout(true);
      aComboPooledDataSource.setMaxIdleTime(5000);
      aComboPooledDataSource.setUnreturnedConnectionTimeout(120000);
      /**
       * try { aComboPooledDataSource.setLogWriter(new PrintWriter(System.out));
       * } catch (SQLException ex) { LOG.warn(ex); }
       */
    } catch (PropertyVetoException ex) {
      LOG.warn(ex);
    }
    LOG.info("Finished creation of ComboPooledDataSource " + theProperties.toString());
    return aComboPooledDataSource;
  }
  
  public static final Float DEFAULT_FLOAT = -1.0f;
  private static ComboPooledDataSource DEFAULT_DATA_SOURCE;

  static {
    //INIT DEFAULT_DATA_SOURCE
    try {
      DEFAULT_DATA_SOURCE = makeComboPooledDataSource(System.getProperties());
    } catch (IOException ex) {
      LOG.warn("IOException caught while initializing the default data source. Check system properties " + System.getProperties().toString(), ex);
    }
  }

  public static ComboPooledDataSource getDefaultDataSource() {
    return DEFAULT_DATA_SOURCE;
  }

  /**
   *
   * @param ps
   * @param keyName
   * @param fieldId
   * @param map
   * @param remove
   * @throws SQLException
   */
  public static void setFloat(PreparedStatement ps, String keyName, int fieldId, Map<String, String> map, Boolean remove) throws SQLException {
    String val = map.get(keyName);
    if (remove) {
      map.remove(keyName);
    }
    if (val == null) {
      ps.setString(fieldId, null);
      return;
    }
    if (val.isEmpty()) {
      ps.setFloat(fieldId, DEFAULT_FLOAT);

    } else {
      if (val.equals("null")) {
        ps.setFloat(fieldId, DEFAULT_FLOAT);
      } else {
        ps.setFloat(fieldId, Float.parseFloat(val));
      }
    }
  }

  /**
   *
   * @param ps
   * @param keyName
   * @param fieldId
   * @param map
   * @param remove
   * @throws SQLException
   */
  public static void setString(PreparedStatement ps, String keyName, int fieldId, Map<String, String> map, Boolean remove) throws SQLException {
    String val = map.get(keyName);
    if (remove) {
      map.remove(keyName);
    }
    if (val == null) {
      ps.setString(fieldId, null);
      return;
    }
    if (val.isEmpty()) {
      ps.setString(fieldId, null);
    } else {
      if (val.equals("null")) {
        ps.setString(fieldId, null);
      } else {
        ps.setString(fieldId, val);
      }
    }
  }

  /**
   *
   * @param ps
   * @param keyName
   * @param fieldId
   * @param map
   * @param remove
   * @throws SQLException
   */
  public static void setInt(PreparedStatement ps, String keyName, int fieldId, Map<String, String> map, Boolean remove) throws SQLException {

    String val = map.get(keyName);
    if (remove) {
      map.remove(keyName);
    }
    if (val == null) {
      ps.setString(fieldId, null);
      return;
    }
    if (val.isEmpty()) {
      ps.setString(fieldId, null);
    } else {
      try {
        ps.setInt(fieldId, Integer.parseInt(val));
      } catch (NumberFormatException nfe) {
        ps.setString(fieldId, null);
      }
    }
  }

  /**
   *
   * @param ps
   * @param keyName
   * @param fieldId
   * @param map
   * @param remove
   * @throws SQLException
   */
  public static void setLong(PreparedStatement ps, String keyName, int fieldId, Map<String, String> map, boolean remove) throws SQLException {
    String val = map.get(keyName);
    if (remove) {
      map.remove(keyName);
    }
    if (val == null) {
      ps.setString(fieldId, null);
      return;
    }
    if (val.isEmpty()) {
      ps.setString(fieldId, null);
    } else {
      try {
        ps.setLong(fieldId, Long.parseLong(val));
      } catch (NumberFormatException nfe) {
        ps.setString(fieldId, null);
      }
    }
  }

  /**
   *
   * @param ps
   * @param keyName
   * @param fieldId
   * @param map
   * @param remove
   * @throws SQLException
   */
  public static void setBoolean(PreparedStatement ps, String keyName, int fieldId, Map<String, String> map, boolean remove) throws SQLException {
    String val = map.get(keyName);
    if (remove) {
      map.remove(keyName);
    }
    if (val == null) {
      ps.setBoolean(fieldId, false);
      return;
    }
    if (val.isEmpty()) {
      ps.setBoolean(fieldId, false);
    } else {
      ps.setBoolean(fieldId, Boolean.parseBoolean(val));
    }
  }

  /**
   *
   * @param ps
   * @param keyName
   * @param fieldId
   * @param map
   * @param remove
   * @param dateFormat
   * @throws SQLException
   * @throws ParseException
   */
  public static void setTimeStamp(PreparedStatement ps, String keyName, int fieldId, Map<String, String> map, boolean remove, DateFormat dateFormat) throws SQLException, ParseException, NumberFormatException {
    String val = map.get(keyName);
    if (remove) {
      map.remove(keyName);
    }
    if (val == null) {
      ps.setTimestamp(fieldId, null);
      return;
    }
    Date parse, currentDate = new Date(new Date().getTime() + 3600000);
    try {
      parse = dateFormat.parse(val);
      if (parse.after(currentDate)) {
        LOG.warn("Date parsed from FIELD=" + keyName + " VALUE=" + val + " Is more than one hour in the future.");
      }
      if (parse.before(TimeUtils.UTC_DATE_2005)) {
        LOG.warn("Date parsed from FIELD=" + keyName + " VALUE=" + val + " Is before .");
      }

    } catch (NumberFormatException nfe) {
      nfe.fillInStackTrace();
      throw nfe;
    }
    Timestamp timestamp = new Timestamp(parse.getTime());
    ps.setTimestamp(fieldId, timestamp);
  }

  /**
   *
   * @param ps
   * @param fieldId
   * @param timeStamp
   * @throws SQLException
   */
  public static void setTimeStamp(PreparedStatement ps, int fieldId, Timestamp timeStamp) throws SQLException {
    if (timeStamp == null) {
      ps.setTimestamp(fieldId, null);
      return;
    }

    ps.setTimestamp(fieldId, timeStamp);
  }

  /**
   *
   * @param ps
   * @param keyName
   * @param fieldId
   * @param map
   * @param remove
   * @throws SQLException
   * @throws ParseException
   */
  public static void setInetAddress(PreparedStatement ps, String keyName, int fieldId, Map<String, String> map, boolean remove) throws SQLException, ParseException, UnknownHostException {
    String val = map.get(keyName);
    if (remove) {
      map.remove(keyName);
    }
    if (val == null) {
      ps.setTimestamp(fieldId, null);
      return;
    }
    InetAddress ip;
    try {
      ip = InetAddress.getByName(val);
      ps.setInt(fieldId, Conversion.byteArrayToUnsignedInt(ip.getAddress()));
    } catch (UnknownHostException ex) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("UnknownHostException Found while converting IPV4 adress to unsigned int.", ex);
      } else if (LOG.isWarnEnabled()) {
        LOG.warn("UnknownHostException Found while converting IPV4 adress to unsigned int. ADDRESS=" + val);
      }
      throw ex;
    }
  }
}
