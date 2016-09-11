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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.ConversionUtils;

/**
 * @author Brian M. Lima
 */
public class GISData {

    private static Log LOG = LogFactory.getLog(GISData.class);
    private static final String SIMPLE_NAME = GISData.class.getSimpleName();
    public static final Map<String, Class> PARAM_MAP = new LinkedHashMap<String, Class>();
    public static final Map<String, Integer> PARAM_TO_PS_MAP = new LinkedHashMap<String, Integer>();
    public static final String TABLE_NAME = "geo.gisCore";
    public static final String PREPARED_GET_SQL;

    static {

        PARAM_MAP.put("startIpNum", Long.class);
        PARAM_MAP.put("endIpNum", Long.class);
        PARAM_MAP.put("locId", Long.class);
        PARAM_MAP.put("country", String.class);
        PARAM_MAP.put("region", String.class);
        PARAM_MAP.put("city", String.class);
        PARAM_MAP.put("postalCode", String.class);
        PARAM_MAP.put("latitude", Float.class);
        PARAM_MAP.put("longitude", Float.class);
        PARAM_MAP.put("metroCode", String.class);
        PARAM_MAP.put("areaCode", String.class);

        PREPARED_GET_SQL = "SELECT " + StringUtils.join(PARAM_MAP.keySet(), ',') + " FROM " + TABLE_NAME + " WHERE ? BETWEEN startIpNum AND endIpNum ;";
    }

    private static void setInetAddress(PreparedStatement ps, String ipAddress) throws SQLException, UnknownHostException {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return;
        }
        InetAddress ip;
        try {
            ip = InetAddress.getByName(ipAddress);
            ps.setInt(1, ConversionUtils.byteArrayToUnsignedInt(ip.getAddress()));
        } catch (UnknownHostException ex) {
            LOG.warn("UnknownHostException Found while converting IPV4 adress to unsigned int.", ex);
            throw ex;
        }
    }

    public static GISData fromIp(String ipAddress, PreparedStatement ps) throws UnknownHostException, SQLException {
        GISData data = new GISData();

        ResultSet resultSet = null;
        try {
            ps.clearParameters();
            setInetAddress(ps, ipAddress);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                data.startIpNum = resultSet.getLong(1);
                data.endIpNum = resultSet.getLong(2);
                data.locId = resultSet.getLong(3);
                data.country = resultSet.getString(4);
                data.region = resultSet.getString(5);
                data.city = resultSet.getString(6);
                data.postalCode = resultSet.getString(7);
                data.latitude = resultSet.getFloat(8);
                data.longitude = resultSet.getFloat(9);
                data.metroCode = resultSet.getString(10);
                data.areaCode = resultSet.getString(11);
            }
        } catch (SQLException e) {
            LOG.warn("SQLException caught while obtaining GISData from IP=" + ipAddress);
            throw e;
        } finally {
            DbUtils.closeQuietly(resultSet);
        }
        return data;
    }

    public static GISData fromIp(String ipAddress, Connection connection) throws UnknownHostException, SQLException {
        GISData data = new GISData();

        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(PREPARED_GET_SQL);
            setInetAddress(ps, ipAddress);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                data.startIpNum = resultSet.getLong(1);
                data.endIpNum = resultSet.getLong(2);
                data.locId = resultSet.getLong(3);
                data.country = resultSet.getString(4);
                data.region = resultSet.getString(5);
                data.city = resultSet.getString(6);
                data.postalCode = resultSet.getString(7);
                data.latitude = resultSet.getFloat(8);
                data.longitude = resultSet.getFloat(9);
                data.metroCode = resultSet.getString(10);
                data.areaCode = resultSet.getString(11);
            }
        } catch (SQLException e) {
            LOG.warn("SQLException caught while obtaining GISData from IP=" + ipAddress);
            throw e;
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(ps);
        }
        return data;
    }

    private Long startIpNum = null;
    private Long endIpNum = null;
    private Long locId = null;
    private String country = null;
    private String region = null;
    private String city = null;
    private String postalCode = null;
    private Float latitude = null;
    private Float longitude = null;
    private String metroCode = null;
    private String areaCode = null;

    public Map<String, String> getTheParamMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        if (startIpNum != null) {
            map.put("startIpNum", String.valueOf(startIpNum));
        } else {
            map.put("startIpNum", null);
        }
        if (endIpNum != null) {
            map.put("endIpNum", String.valueOf(endIpNum));
        } else {
            map.put("endIpNum", null);
        }

        if (locId != null) {
            map.put("locId", String.valueOf(locId));
        } else {
            map.put("locId", null);
        }
        map.put("country", country);
        map.put("region", region);
        map.put("city", city);
        map.put("postalCode", postalCode);
        if (latitude != null) {
            map.put("latitude", String.valueOf(latitude));
        } else {
            map.put("latitude", null);
        }
        if (longitude != null) {
            map.put("longitude", String.valueOf(longitude));
        } else {
            map.put("longitude", null);
        }
        map.put("metroCode", metroCode);
        map.put("areaCode", areaCode);
        return map;

    }

    /**
     * @return the startIpNum
     */
    public Long getStartIpNum() {
        return startIpNum;
    }

    /**
     * @return the endIpNum
     */
    public Long getEndIpNum() {
        return endIpNum;
    }

    /**
     * @return the locId
     */
    public Long getLocId() {
        return locId;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @return the latitude
     */
    public Float getLatitude() {
        return latitude;
    }

    /**
     * @return the longitude
     */
    public Float getLongitude() {
        return longitude;
    }

    /**
     * @return the metroCode
     */
    public String getMetroCode() {
        return metroCode;
    }

    /**
     * @return the areaCode
     */
    public String getAreaCode() {
        return areaCode;
    }
}
