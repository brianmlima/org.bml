/*
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
import java.util.HashMap;
import java.util.Map;
import org.bml.util.SortUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bml.util.data.AddUtil;

/**
 *
 * @author brianmlima
 * @param <T>
 */
public class GenericFieldProfiler<T> implements AddUtil<T> {

    /**
     * The name of the field this profiler is profiling.
     */
    @JsonProperty("fieldName")
    private final String fieldName;
    /**
     * The Class of the field this profiler is profiling.
     * This is for clarity JSON serialization.
     */
    @JsonProperty("fieldClass")
    private final Class fieldClass;

    /**
     * Tracks the number of examples added to this profiler.
     */
    @JsonProperty("sampleSize")
    private int sampleSize = 0;
    /**
     * Tracks the number of null examples added to this profiler.
     */
    @JsonProperty("nullCount")
    private int nullCount = 0;

    /**
     * Creates a new instance of StringFieldProfiler.
     *
     * @param fieldName The name of the field to be profiled.
     * @param fieldClass The class of the field to be profiled.
     */
    public GenericFieldProfiler(final String fieldName, final Class fieldClass) {
        this.fieldName = fieldName;
        this.fieldClass = fieldClass;
    }

    /**
     * A map of value to integer (number of occurrences).
     */
    private Map<T, Integer> occurrenceMap = new HashMap<>();

    /**
     * Gets a map of value to integer (number of occurrences).
     *
     * @return an occurrence map.
     */
    @JsonProperty("occurrenceMap")
    public Map<T, Integer> getOccuranceMap() {
        return SortUtils.sortByValueHighToLow(occurrenceMap);
    }

    @Override
    public void add(final T dataObject) {
        sampleSize++;
        if (dataObject == null) {
            nullCount++;
            return;
        }
        occurrenceMap.put(
                dataObject,
                occurrenceMap.computeIfAbsent(dataObject, k -> Integer.valueOf(0)) + 1
        );
    }

    /**
     * The name of the field this profiler is profiling.
     *
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * The Class of the field this profiler is profiling.
     * This is for clarity JSON serialization.
     *
     * @return the fieldClass
     */
    public Class getFieldClass() {
        return fieldClass;
    }

}
