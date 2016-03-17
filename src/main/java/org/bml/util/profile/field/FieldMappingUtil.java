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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bml.util.data.DataFieldDescription;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for class field mapping functions.
 *
 * Reflection based functions for mapping fields.
 *
 * @author Brian M. Lima
 */
public final class FieldMappingUtil {

    /**
     * Disable instantiation of this object.
     *
     * @throws InstantiationException by design.
     */
    private FieldMappingUtil() throws InstantiationException {
        throw new java.lang.InstantiationException("This class can not be instanced.");
    }

    /**
     * Creates a map of fields in this class to DataFieldDescription.
     *
     * @param theClass a class to map.
     * @return a map of fields in this class to DataFieldDescription
     */
    public static Map<String, DataFieldDescription> makeFieldToClassMap(final Class theClass) {
        Map<String, DataFieldDescription> mapOut = new ConcurrentHashMap<>();
        for (Field f : theClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(JsonProperty.class)) {
                mapOut.put(f.getName(), new DataFieldDescription(f.getName(), f.getType()));
            }
        }
        return mapOut;
    }

    /**
     * A utility for extracting field descriptions and values from POJO's that
     * have JsonProperty annotations.
     *
     * Parameter values are copied unless the parameter class implements FieldMapable.
     * Then the makeFieldMap method is called and its results are added to the output map.
     *
     * This has an effect of flattening the fields.
     *
     * @param theObject The object to map.
     * @return a map of DataFieldDescription to value for the passed object or an empty map if no annotations are found.
     * @throws IllegalArgumentException If the object can not be reflected.
     * @throws IllegalAccessException If there is a security issue.
     */
    public static Map<DataFieldDescription, Object> makeFieldMap(final Object theObject) throws IllegalArgumentException, IllegalAccessException {
        Map<DataFieldDescription, Object> mapOut = new ConcurrentHashMap<>();
        for (Field f : theObject.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(JsonProperty.class)) {
                Object obj = f.get(theObject);
                if (obj instanceof FieldMapable) {
                    Map<DataFieldDescription, Object> fieldMap = ((FieldMapable) obj).makeFieldMap();
                    if (fieldMap != null) {
                        mapOut.putAll(fieldMap);
                    }
                } else if (obj != null) {
                    mapOut.put(new DataFieldDescription(f.getName(), f.getType()), obj);
                }
            }
        }
        return mapOut;
    }

}
