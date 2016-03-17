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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * A container for filed descriptions.
 *
 * This class is used as a replacement key in maps for a String field name. It
 * allows the field name and the class to be carried for validation. You can
 * extend this class to hold more descriptive data.
 *
 * @author Brian M. Lima
 */
public class DataFieldDescription {

    /**
     * The name of the field.
     */
    @JsonProperty("fieldName")
    private final String fieldName;
    /**
     * The expected class of the field.
     */
    @JsonProperty("fieldClass")
    private final Class fieldClass;

    /**
     * Creates a new DataField.
     *
     * @param fieldName The name of the field.
     * @param fieldClass The expected class of the field.
     *
     */
    public DataFieldDescription(final String fieldName, final Class fieldClass) {
        this.fieldName = fieldName;
        this.fieldClass = fieldClass;
    }

    /**
     * Helper method for runtime casting.
     *
     * @param <T> A type
     * @param cls the class type this method will return
     * @param value the value to cast
     * @return the value as the class passed.
     */
    <T> T getAs(final Class<T> cls, final Object value) {
        if (value == null) {
            return null;
        }
        return cls.cast(value);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.fieldName);
        hash = 97 * hash + Objects.hashCode(this.fieldClass);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataFieldDescription other = (DataFieldDescription) obj;
        if (!Objects.equals(this.fieldName, other.fieldName)) {
            return false;
        }
        if (!Objects.equals(this.fieldClass, other.fieldClass)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return fieldName;
    }

    /**
     * The name of the field.
     *
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * The expected class of the field.
     *
     * @return the fieldClass
     */
    public Class getFieldClass() {
        return fieldClass;
    }

}
