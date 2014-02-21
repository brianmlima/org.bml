
package org.bml.util.errorconsumer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.bml.util.sql.DBUtil;

/**
 * @author Brian M. Lima
 */
public class ParseErrorTable {

    public static final Class THE_CLASS = ParseErrorTable.class;
    public static final Map<String, Class> PARAM_MAP = new LinkedHashMap<String, Class>();
    public static final Map<String, Integer> PARAM_TO_PS_MAP = new LinkedHashMap<String, Integer>();
    public static final Set<String> REQUIRED_PARAMS = new HashSet<String>();
    public static final String TABLE_NAME = "parse_error";
    public static final String PREPARED_INSERT_SQL;

    static {
        //POPULATE the parameter map
        PARAM_MAP.put("class_name", String.class);
        //PARAM_MAP.put("host_name", String.class);
        PARAM_MAP.put("uri", String.class);
        PARAM_MAP.put("reason", String.class);
        
        int c = 1;
        //POPULATE the PARAM_TO_PS_MAP based on the parameters in PARAM_MAP 
        for (Map.Entry<String, Class> e : PARAM_MAP.entrySet()) {
            PARAM_TO_PS_MAP.put(e.getKey(), c);
            c++;
        }
        //BUILD prepared statement for inserts.
        PREPARED_INSERT_SQL = "INSERT INTO " + TABLE_NAME + " ( host_name," + StringUtils.join(PARAM_MAP.keySet(), ',') + ") VALUES (" + StringUtils.repeat("?", ",", PARAM_MAP.size()) + ");";
    }


    
    
    /**
     *
     * @param ps
     * @param paramMap
     * @param remove
     * @return
     * @throws SQLException
     * @throws ParseException
     */
    public static Map<String, String> populatePreparedStatement(PreparedStatement ps, Map<String, String> paramMap, Boolean remove) throws SQLException{

        for (Map.Entry<String, Class> e : PARAM_MAP.entrySet()) {
            if (e.getValue().equals(String.class)) {
                DBUtil.setString(ps, e.getKey(), PARAM_TO_PS_MAP.get(e.getKey()), paramMap, remove);
            }
        }
        return paramMap;
    }    
}
