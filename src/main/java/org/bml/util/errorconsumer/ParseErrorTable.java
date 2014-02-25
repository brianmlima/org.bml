package org.bml.util.errorconsumer;

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
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.CharacterUtils;
import org.bml.util.sql.DBUtil;

/**
 * <p>
 * Definition and handler for a database table named by {@link ParseErrorTable#TABLE_NAME}.
 * This class is the basis for a simple - efficient - bulk write ORM.
 * </p>
 *
 * @author Brian M. Lima
 */
public class ParseErrorTable {

    /**
     * A static {@link org.apache.commons.logging.Log} for use by {@link ParseErrorTable}
     * static methods.
     */
    private static final Log LOG = LogFactory.getLog(ParseErrorTable.class);

    /**
     * <p>
     * Instances should <b>NOT</b> be constructed in standard programming.
     * </p>
     */
    public ParseErrorTable() {
    }

    /**
     * <p>
     * A Map of <Column Name, Class> for use in automated population of
     * {@link PreparedStatement} objects.
     * </p>
     */
    public static final Map<String, Class> COLUMN_TYPE_MAP;

    /**
     * <p>
     * Map of the column name and a Byte representing the position of the column
     * in the prepared statement {@link ParseErrorTable#PREPARED_INSERT_SQL}.
     * </p>
     */
    public static final Map<String, Byte> COLUMN_PS_OFFSET_MAP;

    /**
     * A {@link Set} of column names for columns that can not be null.
     */
    public static final Set<String> REQUIRED_PARAMS = Collections.unmodifiableSet(new HashSet<String>());

    /**
     * The full name of the table this class interacts with.
     */
    public static final String TABLE_NAME = "parse_error".intern();
    /**
     * <p>
     * The SQL statement used for creating a {@link PreparedStatement} via
     * {@link java.sql.Connection#prepareStatement(java.lang.String) for
     * insertion into the table named by {@link ParseErrorTable#TABLE_NAME}
     * </p>
     */
    public static final String PREPARED_INSERT_SQL;

    static {
        //Prepare tmpParamMap
        Map<String, Class> tmpParamMap = new LinkedHashMap<String, Class>();
        //POPULATE the parameter map
        tmpParamMap.put("class_name".intern(), String.class);
        //tmpParamMap.put("host_name", String.class);
        tmpParamMap.put("uri".intern(), String.class);
        tmpParamMap.put("reason".intern(), String.class);

        //Prepare tmpParamToPSMap 
        byte columnPosition = 1;
        Map<String, Byte> tmpParamToPSMap = new LinkedHashMap<String, Byte>();
        for (Map.Entry<String, Class> e : tmpParamMap.entrySet()) {
            tmpParamToPSMap.put(e.getKey(), Byte.valueOf(columnPosition));
            columnPosition++;
        }
        //Provide unmodifyable view to ordered collections
        COLUMN_PS_OFFSET_MAP = Collections.unmodifiableMap(tmpParamToPSMap);
        COLUMN_TYPE_MAP = Collections.unmodifiableMap(tmpParamMap);
        //BUILD prepared statement for inserts.
        PREPARED_INSERT_SQL = "INSERT INTO " + TABLE_NAME + " (" + StringUtils.join(COLUMN_TYPE_MAP.keySet(), ',') + ") VALUES (" + StringUtils.repeat("?", ",", COLUMN_TYPE_MAP.size()) + ");";
        if (LOG.isInfoEnabled()) {
            LOG.info(report());
        }
    }

    /**
     * <p>
     * Returns a log printable report on the variables found in this class.
     * Helpful when modifying the table.
     * </p>
     *
     * @return A {@link String} report on the variables in this class
     */
    private static final String report() {
        //Prefer Writer for MapUtils.
        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        PrintStream pStream = new PrintStream(oStream);
        //StringWriter buff = new StringWriter();
        String nl = CharacterUtils.NL;
        //Class Info
        pStream.append("Class : ").append(ParseErrorTable.class.getCanonicalName()).append(nl);
        //Type Info
        pStream.append("Type : mini ORM").append(nl);
        //Table Name
        pStream.append("TABLE_NAME : ").append(TABLE_NAME).append(nl);
        //Table Name
        pStream.append("Prepared Insert Statement : ").append(PREPARED_INSERT_SQL).append(nl);
        //COLUMN_TYPE_MAP
        MapUtils.debugPrint(pStream, "Colum To Class Map", COLUMN_TYPE_MAP);
        //COLUMN_PS_OFFSET_MAP
        MapUtils.debugPrint(pStream, "Colum To PreparedStatement Offset Map", COLUMN_PS_OFFSET_MAP);
        return oStream.toString();
    }

    /**
     * <p>
     * Populates a {@link PreparedStatement} from the entries in a parameter map.
     * </p>
     * <p>
     * <b>NOTE</b>: This method will throw an {@link SQLException} if the passed
     * {@link PreparedStatement} is not created from
     * {@link ParseErrorTable#PREPARED_INSERT_SQL}
     * </p>
     *
     * @param thPreparedStatement {@link PreparedStatement} to populate values from the passed tehParamMap
     * @param theParamMap A {@link Map<String,String>} containing <Column Name, Value> for a single statement.
     * @param removeUsedFromMap {@link Boolean} if {@link Boolean#TRUE} entries
     * in theParamMap will be removed as they are set in thePreparedStatement.
     * This allows error checking for unused parameters but is generally set to
     * {@link Boolean#FALSE} in production.
     * @throws SQLException Per call to {@link PreparedStatement#setString(int, java.lang.String)}
     * @see {@link SQLException}
     * @see {@link PreparedStatement}
     */
    public static void populatePreparedStatement(final PreparedStatement thPreparedStatement, final Map<String, String> theParamMap, final Boolean removeUsedFromMap) throws SQLException {
        for (Map.Entry<String, Class> e : COLUMN_TYPE_MAP.entrySet()) {
            if (e.getValue().equals(String.class)) {
                DBUtil.setString(thPreparedStatement, e.getKey(), COLUMN_PS_OFFSET_MAP.get(e.getKey()), theParamMap, removeUsedFromMap);
            }
        }
    }
}
