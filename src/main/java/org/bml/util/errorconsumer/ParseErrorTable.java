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
        PARAM_MAP.put("uri", String.class);
        PARAM_MAP.put("reason", String.class);
        
        int c = 1;
        //POPULATE the PARAM_TO_PS_MAP based on the parameters in PARAM_MAP 
        for (Map.Entry<String, Class> e : PARAM_MAP.entrySet()) {
            PARAM_TO_PS_MAP.put(e.getKey(), c);
            c++;
        }
        //BUILD prepared statement for inserts.
        PREPARED_INSERT_SQL = "INSERT INTO " + TABLE_NAME + " (" + StringUtils.join(PARAM_MAP.keySet(), ',') + ") VALUES (" + StringUtils.repeat("?", ",", PARAM_MAP.size()) + ");";
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
