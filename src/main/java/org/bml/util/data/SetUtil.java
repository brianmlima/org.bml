/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * Interface for setters. This is handy when using enumeration to deal with setting data
 * fields.
 *
 * @author Brian M. Lima
 *
 * @param <T> The Type of the fields value.
 * @param <R> The data container object where the field is set.
 */
public interface SetUtil<T, R> {

    /**
     * setter for this field utilities field from a dataObject.
     *
     * @param valueObject An instance of an object that is the value to be set.
     * @param dataObject An instance of an object where the field can be set.
     * @throws java.lang.Exception on error.
     */
    void set(T valueObject, R dataObject) throws Exception;
}
