
package org.bml.util.geo.util.geolite;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian M. Lima
 *
 */
public class GeoLiteCityBlock {

    private static final Log LOG = LogFactory.getLog(GeoLiteCityBlock.class);
    private static final String SIMPLE_NAME = GeoLiteCityBlock.class.getSimpleName();
    public static final String TABLE_NAME = "geo.geoLiteCityBlock";
    public static final String PREPARED_SELECT_SQL;
    public static final Map<String, Class> PARAM_MAP = new LinkedHashMap<String, Class>();
    public static final Set<String> REQUIRED_PARAMS = new HashSet<String>();
    private Map<String, String> blockMap = null;
    private GeoLiteCityLocation theLocation = null;

    /**
     * @return the theLocation
     */
    public GeoLiteCityLocation getTheLocation() {
        return theLocation;
    }

    /**
     * @param theLocation the theLocation to set
     */
    public void setTheLocation(GeoLiteCityLocation theLocation) {
        this.theLocation = theLocation;
    }

    /**
     * @return the startIpNum
     */
    public int getStartIpNum() {
        return startIpNum;
    }

    /**
     * @param startIpNum the startIpNum to set
     */
    public void setStartIpNum(int startIpNum) {
        this.startIpNum = startIpNum;
    }

    /**
     * @return the endIpNum
     */
    public int getEndIpNum() {
        return endIpNum;
    }

    /**
     * @param endIpNum the endIpNum to set
     */
    public void setEndIpNum(int endIpNum) {
        this.endIpNum = endIpNum;
    }

    /**
     * @return the locId
     */
    public int getLocId() {
        return locId;
    }

    /**
     * @param locId the locId to set
     */
    public void setLocId(int locId) {
        this.locId = locId;
    }

    /**
     * The Field enum.
     */
    public static enum FIELD {

        STARTIP("startIpNum", Integer.class, null, Boolean.TRUE),
        ENDIP("endIpNum", Integer.class, null, Boolean.TRUE),
        LOCID("locId", Integer.class, null, Boolean.TRUE);
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
        for (GeoLiteCityBlock.FIELD field : GeoLiteCityBlock.FIELD.values()) {
            PARAM_MAP.put(field.fieldName, field.fieldClass);
            if (field.required) {
                REQUIRED_PARAMS.add(field.fieldName);
            }
        }
        PREPARED_SELECT_SQL = "SELECT " + StringUtils.join(PARAM_MAP.keySet(), ',') + " FROM " + TABLE_NAME + " ;";
    }
    private int startIpNum = 0, endIpNum = 0, locId = 0;

    public GeoLiteCityBlock(int startIpNum, int endIpNum, int locId) {
        this.startIpNum = startIpNum;
        this.endIpNum = endIpNum;
        this.locId = locId;
    }

    public GeoLiteCityBlock(final String startIpNum, final String endIpNum, final String locId) {
        this.startIpNum = Integer.parseInt(startIpNum);
        this.endIpNum = Integer.parseInt(endIpNum);
        this.locId = Integer.parseInt(locId);
    }

    public Map<String, String> getParamMap() {
        if (this.blockMap == null) {
            this.blockMap = toParamMap(this);
        }
        return this.blockMap;
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
    private String getMemberAsString(GeoLiteCityBlock.FIELD field) {
        if (startIpNum != endIpNum) {
            return null;
        }
        switch (field) {
            case LOCID:
                return String.valueOf(this.locId);
            case STARTIP:
                return String.valueOf(this.startIpNum);
            case ENDIP:
                return String.valueOf(this.endIpNum);
            default:
                return null;
        }
    }

    public static Map<String, String> toParamMap(GeoLiteCityBlock obj) {
        Map<String, String> map = new HashMap<String, String>();
        for (GeoLiteCityBlock.FIELD field : GeoLiteCityBlock.FIELD.values()) {
            map.put(field.fieldName, obj.getMemberAsString(field));
        }
        return map;
    }

    public static Map<Integer, GeoLiteCityBlock> readFromDB(ComboPooledDataSource dataSource) {
        Map<Integer, GeoLiteCityBlock> mapOut = new TreeMap<Integer, GeoLiteCityBlock>();
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

            rs = st.executeQuery(GeoLiteCityBlock.PREPARED_SELECT_SQL);
            while (rs.next()) {
                c++;
                mapOut.put(rs.getInt(FIELD.STARTIP.fieldName), new GeoLiteCityBlock(rs.getInt(FIELD.STARTIP.fieldName), rs.getInt(FIELD.ENDIP.fieldName), rs.getInt(FIELD.LOCID.fieldName)));
                if ((c % 100000) == 0) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Loaded " + c + " IP Block to Location mappings");
                    }
                }
            }
        } catch (SQLException ex) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("SQLException caught while loading GeoLiteCityBlock objects ", ex);
            }
        } finally {
            DbUtils.closeQuietly(con, st, rs);
        }
        return mapOut;
    }
}
