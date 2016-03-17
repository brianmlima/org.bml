/**/
package org.bml.util.data;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2016 Brian M. Lima
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
 * Interface for getters. This is handy when using enumeration to deal with data
 * fields.
 *
 * @author Brian M. Lima
 *
 * @param <T> The Type of the fields value.
 */
public interface AddUtil<T> {

    /**
     * Adder for this field utilities field from a dataObject.
     *
     * @param dataObject An instance of an object where the field can be found.
     */
    void add(T dataObject);
}
