package org.bml.util.log;

import org.bml.util.Validatable;
import org.bml.util.db.PreparedStatementable;

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
/**
 * Interface for objects that have initialization then parsing.
 *
 * @author Brian M. Lima
 */
public interface LineParsable extends Validatable, PreparedStatementable {

    /**
     * Parses data and performs any other necessary operations
     * necessary to the population of this object.
     *
     * @return true on success, false otherwise.
     * @throws Exception Allows implementations to throw any necessary exceptions.
     */
    boolean parseLine() throws Exception;

}
