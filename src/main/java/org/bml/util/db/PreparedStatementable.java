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

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Used for manipulation sets of objects that can be added to a {@link PreparedStatement}.
 * Very handy for working with batch inserts and or updates.
 *
 * @author Brian M. Lima
 */
public interface PreparedStatementable {

    /**
     * Adds data from a {@link T} to a {@link PreparedStatement}.
     *
     * @param thePreparedStatement A {@link PreparedStatement} to add data to.
     * @return true on success, false if there is an error that does not throw an exception.
     * @throws SQLException If there is an issue adding data to the {@link PreparedStatement}.
     * @throws IllegalArgumentException if the {@link T} does not pass validation.
     * @pre <code>thePreparedStatement!=null</code>
     */
    boolean addToStatemet(final PreparedStatement thePreparedStatement) throws SQLException, IllegalArgumentException;

    /**
     * Getter for a prepared statement to enter T into a data source.
     *
     * @return A prepared statement.
     */
    String getPreparedStatementSQL();

}
