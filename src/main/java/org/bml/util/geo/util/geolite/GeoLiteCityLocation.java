
package org.bml.util.geo.util.geolite;

/*
 * #%L
 * orgbml
 * %%
 * Copyright (C) 2008 - 2013 Brian M. Lima
 * %%
 * This file is part of org.bml.
 * 
 * org.bml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.bml is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with org.bml.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.errorconsumer.ParseError;

/**
 * This class reads a CSV file of location data from a standard format from
 * GeoLiteCity. There are also methods for adding this data to a database so it
 * can be used by location services.
 *
 * NOTE: This is also an experiment using a static ENUM for field data. In other
 * high performance POJO implementations in org.bml.util several maps are used
 * to coordinate and obfuscate field operations and insertions. This class
 * attempts to use an ENUM to reduce the possibility of typo error and coalesce
 * field declaration into a single place.
 *
 * @author Brian M. Lima
 */
public class GeoLiteCityLocation {

    private static final Log LOG = LogFactory.getLog(GeoLiteCityLocation.class);
    private static final String SIMPLE_NAME = GeoLiteCityLocation.class.getSimpleName();
    public static final Map<String, Class> PARAM_MAP = new LinkedHashMap<String, Class>();
    public static final Map<String, Integer> PARAM_TO_PS_MAP = new LinkedHashMap<String, Integer>();
    public static final Map<String, Integer> PARAM_TO_MAX_LEN_MAP = new LinkedHashMap<String, Integer>();
    public static final Set<String> REQUIRED_PARAMS = new HashSet<String>();
    public static final String TABLE_NAME = "geo.geoLiteCityLocation";
    public static final String PREPARED_INSERT_SQL, PREPARED_SELECT_SQL;

    /**
     * The Field enum.
     */
    public static enum FIELD {

        LOCID("locId", Integer.class, null, Boolean.TRUE),
        COUNTRY("country", String.class, 32, Boolean.FALSE),
        REGION("region", String.class, 32, Boolean.FALSE),
        CITY("city", String.class, 32, Boolean.FALSE),
        POSTALCODE("postalCode", String.class, 32, Boolean.FALSE),
        LATITUDE("latitude", Double.class, null, Boolean.TRUE),
        LONGITUDE("longitude", Double.class, null, Boolean.TRUE),
        METROCODE("metroCode", String.class, 32, Boolean.FALSE),
        AREACODE("areaCode", Long.class, null, Boolean.FALSE);
        private final String fieldName;
        private final Class fieldClass;
        private final Integer maxLen;
        private final Boolean required;

        FIELD(String fieldName, Class fieldClass, Integer maxLen, Boolean required) {
            this.fieldName = fieldName;
            this.fieldClass = fieldClass;
            this.maxLen = maxLen;
            this.required = required;
        }
    };

    static {
        for (FIELD field : FIELD.values()) {
            PARAM_MAP.put(field.fieldName, field.fieldClass);
            if (field.required) {
                REQUIRED_PARAMS.add(field.fieldName);
            }
            if (field.maxLen != null) {
                PARAM_TO_MAX_LEN_MAP.put(field.fieldName, field.maxLen);
            }
        }

        int c = 1;
        //POPULATE the PARAM_TO_PS_MAP based on the parameters in PARAM_MAP 
        for (Map.Entry<String, Class> e : PARAM_MAP.entrySet()) {
            PARAM_TO_PS_MAP.put(e.getKey(), c);
            c++;
        }
        //BUILD prepared statement for inserts.
        PREPARED_INSERT_SQL = "INSERT INTO " + TABLE_NAME + " (" + StringUtils.join(PARAM_MAP.keySet(), ',') + ") VALUES (" + StringUtils.repeat("?", ",", PARAM_MAP.size()) + ");";
        PREPARED_SELECT_SQL = "SELECT " + StringUtils.join(PARAM_MAP.keySet(), ',') + " FROM " + TABLE_NAME + " ;";
    }
    private Integer locId = null;
    private String country = null;
    private String region = null;
    private String city = null;
    private String postalCode = null;
    private Double latitude = null;
    private Double longitude = null;
    private String metroCode = null;
    private Long areaCode = null;

    public GeoLiteCityLocation(Integer locId, String country, String region, String city, String postalCode, Double latitude, Double longitude, String metroCode, Long areaCode) {
        this.locId = locId;
        this.country = country;
        this.region = region;
        this.city = city;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.metroCode = metroCode;
        this.areaCode = areaCode;
    }

    public static Map<Integer, GeoLiteCityLocation> readFromDB(ComboPooledDataSource dataSource) {
        Map<Integer, GeoLiteCityLocation> mapOut = new HashMap<Integer, GeoLiteCityLocation>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        GeoLiteCityBlock tmp = null;
        int c = 0;
        try {
            con = dataSource.getConnection();

            st = con.createStatement();
            st.setMaxRows(Integer.MAX_VALUE);
            st.setQueryTimeout(600000);
            st.setFetchSize(100000);

            rs = st.executeQuery(PREPARED_SELECT_SQL);
            while (rs.next()) {
                c++;
                mapOut.put(rs.getInt(FIELD.LOCID.fieldName),
                        new GeoLiteCityLocation(
                        rs.getInt(FIELD.LOCID.fieldName),
                        rs.getString(FIELD.COUNTRY.fieldName),
                        rs.getString(FIELD.REGION.fieldName),
                        rs.getString(FIELD.CITY.fieldName),
                        rs.getString(FIELD.POSTALCODE.fieldName),
                        rs.getDouble(FIELD.LATITUDE.fieldName),
                        rs.getDouble(FIELD.LONGITUDE.fieldName),
                        rs.getString(FIELD.METROCODE.fieldName),
                        rs.getLong(FIELD.AREACODE.fieldName)));
                if ((c % 100000) == 0) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Loaded " + c + " Location mappings");
                    }
                }
            }
        } catch (SQLException ex) {
            if(LOG.isWarnEnabled()){
            LOG.warn("SQLException caught while loading GeoLiteCityBlock objects ", ex);
            }
        } finally {
            DbUtils.closeQuietly(con, st, rs);
        }
        return mapOut;
    }

    /**
     * Helper method for field value retrieval. NOTE: This method eliminates the
     * setNulls step present in other POJO implementations.
     *
     * NOTE: This could be done using reflection at a speed cost
     *
     * @param field the field to return as a string
     * @return Field value as a String or null.
     */
    private String getMemberAsString(FIELD field) {
        switch (field) {
            case LOCID:
                if (this.locId != null) {
                    return String.valueOf(this.locId);
                }
                return null;
            case COUNTRY:
                if (this.country != null) {
                    return String.valueOf(this.country);
                }
                return null;
            case REGION:
                if (this.region != null) {
                    return String.valueOf(this.region);
                }
                return null;
            case CITY:
                if (this.city != null) {
                    return String.valueOf(this.city);
                }
                return null;
            case POSTALCODE:
                if (this.postalCode != null) {
                    return String.valueOf(this.postalCode);
                }
                return null;
            case LATITUDE:
                if (this.latitude != null) {
                    return String.valueOf(this.latitude);
                }
                return null;
            case LONGITUDE:
                if (this.longitude != null) {
                    return String.valueOf(this.longitude);
                }
                return null;
            case METROCODE:
                if (this.metroCode != null) {
                    return String.valueOf(this.metroCode);
                }
                return null;
            case AREACODE:
                if (this.areaCode != null) {
                    return String.valueOf(this.areaCode);
                }
                return null;
            default:
                return null;
        }
    }

    /**
     * @param obj GeoLiteCityLocation container object holding the field values.
     * @return A map containing the fields in the passed container object.
     */
    public static Map<String, String> toParamMap(GeoLiteCityLocation obj) {
        Map<String, String> map = new HashMap<String, String>();
        for (FIELD field : FIELD.values()) {
            map.put(field.fieldName, obj.getMemberAsString(field));
        }
        return map;
    }

    /**
     * @param paramMap A parameter map containing fields to check for length
     * overruns
     * @throws ParseError On field size overage.
     */
    public static void checkMaxLen(Map<String, String> paramMap) throws ParseError {
        String aString;
        Integer maxLen;
        for (Map.Entry<String, Integer> entry : PARAM_TO_MAX_LEN_MAP.entrySet()) {
            maxLen = entry.getValue();
            aString = paramMap.get(entry.getKey());
            if (aString == null) {
                continue;
            }
            if (aString.length() > maxLen) {
                ParseError pe = new ParseError(SIMPLE_NAME, "UNAVAILABLE", "Parameter " + entry.getKey() + " exceeds the length allowed by the database " + maxLen);
                throw pe;
            }
        }
    }

}
