
package org.bml.util.geo.util.geolite;

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
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.exception.UnavailableException;
import org.bml.util.io.net.NetworkUtils;
import org.bml.util.search.SearchUtils;
import org.bml.util.sql.DBUtil;

/**
 * @author Brian M. Lima
 */
public class GISControler {

  private static boolean isAvailable = false;
  private static final Log LOG = LogFactory.getLog(GISControler.class);
  private static final String SIMPLE_NAME = GISControler.class.getSimpleName();
  private static Map<Integer, GeoLiteCityBlock> ipBlocks = null;
  private static Integer startIPs[] = null;

  static {
    int attemptNum = 1;
    while (!isAvailable) {
      LOG.info("Attempting load of GIS SYSTEM for the " + attemptNum + " Time");
      initGISFromDB();
      attemptNum++;
      if (!isAvailable) {
        LOG.warn("There was a problem loading the GIS sub system waiting 2 seconds and attempting again");
        try {
          Thread.currentThread().wait(2000);
        } catch (InterruptedException ex) {
          if (LOG.isErrorEnabled()) {
            LOG.error("InterruptedException encountered while initializing GISController. The GISController service will not be available.", ex);
          }
        }
      }
    }
  }

  /**
   */
  private static void initGISFromDB() {
    try {
      ComboPooledDataSource myComboPooledDataSource = DBUtil.getDefaultDataSource();
      StopWatch myStopWatch = new StopWatch();
      myStopWatch.start();
      ipBlocks = GeoLiteCityBlock.readFromDB(myComboPooledDataSource);

      myStopWatch.stop();
      if (LOG.isInfoEnabled()) {
        LOG.info("Finished Loading " + ipBlocks.size() + " IPBlocks Map in " + (myStopWatch.getTime()/1000) + " Seconds");
      }
      myStopWatch.start();
      Map<Integer, GeoLiteCityLocation> locationMap = GeoLiteCityLocation.readFromDB(myComboPooledDataSource);
      myStopWatch.stop();
      if (LOG.isInfoEnabled()) {
        LOG.info("Finished Loading " + locationMap.size() + " Locations in " + (myStopWatch.getTime()/1000) + " Seconds");
      }
      startIPs = ipBlocks.keySet().toArray(new Integer[ipBlocks.keySet().size()]);
      //This should not be necessary but we sort for now until the underlying structures have been 
      // proven
      Arrays.sort(startIPs);
      for (GeoLiteCityBlock block : ipBlocks.values()) {
        block.setTheLocation(locationMap.get(block.getLocId()));
      }
      //Mark for GC
      locationMap.clear();
      locationMap = null;

    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Exception caught while loading GIS data. GIS DATA LOOKUP WILL NOT BE AVAILABLE!", e);
      }
    } finally {
      if (ipBlocks != null && !ipBlocks.isEmpty() && startIPs != null && startIPs.length > 0) {
        isAvailable = true;
      } else {
        isAvailable = false;
      }
    }
  }

  /**
   *
   * @throws UnavailableException
   */
  private static void checkAvailability() throws UnavailableException {
    if (!isAvailable) {
      UnavailableException ex = new UnavailableException("GIS Lookup Service is unavailable");
      ex.fillInStackTrace();
      throw ex;
    }
  }

  /**
   *
   * @param ip An IP address as an integer to find a starting ip of the range the passed IP is a member of.
   * @return the numeric representation of the starting IP the passed IP is a member of.
   * @throws UnavailableException
   */
  public static Integer getStartIp(Integer ip) throws UnavailableException {
    checkAvailability();
    return SearchUtils.binarySearchLower(startIPs, 0, startIPs.length, ip);
  }

  /**
   *
   * @param ipString a octal string representation of an IP address
   * @return The numeric representation of the starting IP the passed IP is a member of.
   * @throws UnknownHostException
   * @throws UnavailableException
   */
  public static Integer getStartIp(final String ipString) throws UnknownHostException, UnavailableException {
    checkAvailability();
    //Sanity
    if (ipString == null || ipString.length() == 0) {
      return null;
    }

    Integer myIp = NetworkUtils.toNumericIp(ipString);
    if (myIp == null) {
      //LOG
      return null;
    }
    return getStartIp(myIp);
  }

  /**
   *
   * @param theStartIp the low or starting ip of a range to lookup a {@link GeoLiteCityBlock}
   * @return an {@link GeoLiteCityBlock} the passed starting ip refrences
   * @throws UnavailableException
   */
  public static GeoLiteCityBlock getBlock(final Integer theStartIp) throws UnavailableException {
    checkAvailability();
    //Sanity
    if (theStartIp == null) {
      //LOG
      return null;
    }

    Integer myStartIp = SearchUtils.binarySearchLower(startIPs, 0, startIPs.length, theStartIp);
    GeoLiteCityBlock myGeoLiteCityBlock = ipBlocks.get(myStartIp);
    if (myGeoLiteCityBlock != null && myStartIp <= myGeoLiteCityBlock.getEndIpNum()) {
      return myGeoLiteCityBlock;
    }
    return null;
  }

  /**
   *
   * @param ipString An IP to lookup a {@link GeoLiteCityBlock} for
   * @return an {@link GeoLiteCityBlock} the passed starting ip refrences
   * @throws UnknownHostException
   * @throws UnavailableException
   */
  public static GeoLiteCityBlock getBlock(final String ipString) throws UnknownHostException, UnavailableException {
    checkAvailability();
    Integer myStartIp = getStartIp(ipString);
    if (myStartIp == null) {
      return null;
    }
    return getBlock(myStartIp);
  }

  /**
   *
   * @param ip
   * @return todo
   * @throws UnavailableException
   */
  public static Map<String, String> getParamMap(Integer ip) throws UnavailableException {
    checkAvailability();
    Map<String, String> mapOut = new HashMap<String, String>();
    GeoLiteCityBlock block = getBlock(ip);
    if (block != null) {
      mapOut.putAll(block.getParamMap());
      GeoLiteCityLocation loc = block.getTheLocation();
      if (loc != null) {
        mapOut.putAll(GeoLiteCityLocation.toParamMap(loc));
      }
    }
    return mapOut;
  }

  /**
   *
   * @param theIpString
   * @return todo
   * @throws UnknownHostException
   * @throws UnavailableException
   */
  public static Map<String, String> getParamMap(final String theIpString) throws UnknownHostException, UnavailableException {
    checkAvailability();
    Integer myIp = NetworkUtils.toNumericIp(theIpString);
    if (myIp == null) {
      return null;
    }
    return getParamMap(myIp);
  }

  /**
   * @return the isAvailable
   */
  public static boolean isIsAvailable() {
    return isAvailable;
  }






}
