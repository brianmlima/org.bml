/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bml.util.profile.field;

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

import java.util.Map;
import org.bml.util.data.DataFieldDescription;

/**
 *
 * @author Brian M. Lima
 */
public interface FieldMapable {

    /**
     * Creates a field map from this object using reflection.
     *
     * @return A map of this objects DataFieldDescription's to value.
     * @throws IllegalArgumentException if there is an issue using reflection getting a field.
     * @throws IllegalAccessException if there is a security issue using reflection getting a field.
     */
    Map<DataFieldDescription, Object> makeFieldMap() throws IllegalArgumentException, IllegalAccessException;

}
