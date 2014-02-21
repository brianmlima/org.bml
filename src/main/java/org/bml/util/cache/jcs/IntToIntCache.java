
package org.bml.util.cache.jcs;

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

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.control.CompositeCacheManager;
import org.bml.util.exception.UnavailableException;

/**
 * A org.apache.jcs based cache wrapper for mapping Integer's with an SQL lookup
 * for values when not found buy the cache. Ideally this class should be
 * instanced and used by a static class that simplifies use then accessed via
 * lookup by any component that needs access to the specific mappings the cache
 * holds.
 *
 * @author Brian M. Lima
 */
public class IntToIntCache {

  /**
   * Standard Logging. All logging should be funneled through this log.
   */
  private Log theLog;
  /**
   * Use to avoid calling .class.getName() in high throughput situations
   */
  private String theClassName;
  /**
   * Use to avoid calling .class.getSimpleName() in high throughput situations
   */
  private String theSimpleClassName;
  /**
   * A prepared statement where an int of position 1 can be set with a key,
   * executed and a value for caching returned if it exists
   */
  private String theLookupSQL;
  /**
   * The name of the JCS cache region.
   */
  private String theCacheRegionName;
  /**
   * The Properties object used to configure the JCS cache.
   */
  private Properties theCacheProperties;
  /**
   * The JCS cache object.
   */
  private JCS theCache;

  /**
   * Helper for initializing standard class members.
   */
  private void coreInit(final Class tmpClass) {
    theLog = LogFactory.getLog(tmpClass);
    theClassName = tmpClass.getName().intern();
    theSimpleClassName = tmpClass.getSimpleName().intern();
  }

  /**
   * Handles going to the database to retrieve values for keys not already in
   * theCache.
   *
   * @param theKey Integer Key that can be used in theSQL to retrieve a value
   * for the cache if it does not already exist in the cache.
   * @param theComboPooledDataSource C3PO data source. This is passed here so
   * that implementations can deal with choosing and managing of data sources
   * @return Integer or NULL as determined by theSQL provided when this object
   * was built. ReturnS null if theSQL does not return an int value for theKey.
   */
  private Integer getFromDB(final Integer theKey, final ComboPooledDataSource theComboPooledDataSource) {
    Connection myConnection = null;
    PreparedStatement myPreparedStatement = null;
    ResultSet myResultSet = null;
    Integer myValue = null;
    try {
      myConnection = theComboPooledDataSource.getConnection();
      myPreparedStatement = myConnection.prepareStatement(theLookupSQL);
      myPreparedStatement.setInt(1, theKey);
      myResultSet = myPreparedStatement.executeQuery();
      while (myResultSet.next()) {
        myValue = myResultSet.getInt(1);
      }
    } catch (SQLException mySQLException) {
      if (theLog.isErrorEnabled()) {
        theLog.error("SQLException caught while attempting retrieval from DB for cache forregion " + theCacheRegionName, mySQLException);
      }
    } catch (Exception myException) {
      if (theLog.isErrorEnabled()) {
        theLog.error("Exception caught while attempting retrieval from DB for cache forregion " + theCacheRegionName, myException);
      }
    } finally {
      DbUtils.closeQuietly(myConnection, myPreparedStatement, myResultSet);
    }
    return myValue;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    closeCache();
  }

  /**
   * Constructor for building a generic int to int cache supported by JCS and 
   * lookup backed by a database. 
   * 
   * @param theCacheRegionName The name of the JCS cache region.
   * @param theCacheProperties The Properties object used to configure the JCS cache.
   * @param theLookupSQL A prepared statement where an integer of position 1 can be set with a key,
   * executed and a value for caching returned if it exists.
   */
  public IntToIntCache(final String theCacheRegionName, final Properties theCacheProperties, final String theLookupSQL) {
    coreInit(this.getClass());
    this.theLookupSQL = theLookupSQL.intern();
    //Configure JCS cache region
    this.theCacheRegionName = theCacheRegionName;
    this.theCacheProperties = theCacheProperties;
    //TODO: Should check for preconfigured cache region    
    CompositeCacheManager theCompositeCacheManager = CompositeCacheManager.getUnconfiguredInstance();
    theCompositeCacheManager.configure(theCacheProperties);
    try {
      theCache = JCS.getInstance(theCacheRegionName);
    } catch (CacheException myCacheException) {
      if (theLog.isErrorEnabled()) {
        theLog.error("Unable to build/retrieve JSC cache region " + theCacheRegionName, myCacheException);
      }
      theCache = null;
    }
  }

  /**
   * Retrieves the value related to the key provided. Retrieves and adds the key
   * / value to theCache if it does not already exist.
   *
   * @param theKey Integer Key that can be used in theSQL to retrieve a value
   * for the cache if it does not already exist in the cache.
   * @param theComboPooledDataSource C3PO data source. This is passed here so
   * that implementations can deal with choosing and managing of data sources
   * @return Integer the cache value as determined by theSQL provided when this
   * object was built. Can return null if theSQL does not return an int value
   * for a particular key.
   * @throws UnavailableException. This can be thrown if theCache or the data
   * source is not functional because of configuration or any other reason.
   */
  public Integer get(final Integer theKey, final ComboPooledDataSource theComboPooledDataSource) throws UnavailableException {
    if (theCache == null) {
      throw new UnavailableException("The JCS cache in " + theSimpleClassName + " is unavailable for cache region " + theCacheRegionName);
    }
    Integer myValue = (Integer) theCache.get(theKey);
    if (myValue == null) {
      myValue = getFromDB(theKey, theComboPooledDataSource);
      if (myValue != null) {
        try {
          theCache.put(theKey, myValue);
        } catch (CacheException myCacheException) {
          if (theLog.isErrorEnabled()) {
            theLog.error("Unable to add to JSC cache for region " + theCacheRegionName, myCacheException);
          }
          throw new UnavailableException("The JCS cache in " + theSimpleClassName + " is unavailable for region " + theCacheRegionName);
        }
      }
    }
    return myValue;
  }

  /**
   * Attempts to clear and close the cache. Stopping any threads and cleaning up
   * resources.
   */
  public void closeCache() {
    if (this.theCache != null) {
      try {
        this.theCache.clear();
        this.theCache.dispose();
        this.theCache = null;
      } catch (CacheException theCacheException) {
        Logger.getLogger(IntToIntCache.class.getName()).log(Level.SEVERE, null, theCacheException);
      }
    }

  }
}
