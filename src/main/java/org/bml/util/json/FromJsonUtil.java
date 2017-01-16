/*
 */
package org.bml.util.json;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2017 Brian M. Lima
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

/**
 * A simple interface user for deserialization from raw json.
 *
 * @author Brian M. Lima
 * @param <T>
 */
public interface FromJsonUtil<T> {

    public T fromJson(final String rawJson) throws Exception;
}
