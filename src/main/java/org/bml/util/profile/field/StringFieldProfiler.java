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
package org.bml.util.profile.field;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.bml.util.SortUtils;
import org.bml.util.data.AddUtil;

/**
 * A Utility for profiling a Set of Strings.
 *
 * @author Brian M. Lima
 */
public class StringFieldProfiler implements AddUtil<String> {

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
    private final Class fieldClass = String.class;

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
     * Tracks the number of empty examples added to this profiler.
     */
    @JsonProperty("emptyCount")
    private int emptyCount = 0;

    /**
     * Statistics object for String lengths.
     */
    private final SynchronizedDescriptiveStatistics lengthStats = new SynchronizedDescriptiveStatistics();

    /**
     * Gets the tracked min length.
     *
     * @return the tracked min length.
     */
    @JsonGetter("minLength")
    public double getMinLength() {
        return lengthStats.getMin();
    }

    /**
     * Gets the tracked max length.
     *
     * @return the tracked max length.
     */
    @JsonGetter("maxLength")
    public double getMaxLength() {
        return lengthStats.getMax();
    }

    /**
     * Gets the tracked mean length.
     *
     * @return the tracked mean length.
     */
    @JsonGetter("meanLength")
    public double getMeanLength() {
        return lengthStats.getMean();
    }

    /**
     * Creates a new instance of StringFieldProfiler.
     *
     * @param fieldName The name of the field to be profiled.
     */
    public StringFieldProfiler(final String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * A map of value to integer (number of occurrences).
     */
    private Map<String, Integer> occurrenceMap = new HashMap<>();

    /**
     * Gets a map of value to integer (number of occurrences).
     *
     * @return an occurrence map.
     */
    @JsonGetter("occurrenceMap")
    public Map<String, Integer> getOccuranceMap() {
        return SortUtils.sortByValueHighToLow(occurrenceMap);
    }

    @Override
    public void add(final String dataObject) {
        sampleSize++;
        if (dataObject == null) {
            nullCount++;
            return;
        }
        if (dataObject.isEmpty()) {
            emptyCount++;
        }
        lengthStats.addValue(dataObject.length());
        Integer count = occurrenceMap.get(dataObject);
        occurrenceMap.put(dataObject, (count == null) ? Integer.valueOf(1) : count + 1);
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
     * @return the fieldClass
     */
    public Class getFieldClass() {
        return fieldClass;
    }
}
