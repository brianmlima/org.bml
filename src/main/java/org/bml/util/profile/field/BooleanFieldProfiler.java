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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bml.util.data.AddUtil;

/**
 * A Utility for profiling of Boolean Objects.
 *
 * @author Brian M. Lima
 */
public class BooleanFieldProfiler implements AddUtil<Boolean> {

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
    private final Class fieldClass = Boolean.class;

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
     * Tracks the number of true examples added to this profiler.
     */
    @JsonProperty("trueCount")
    private int trueCount = 0;
    /**
     * Tracks the number of false examples added to this profiler.
     */
    @JsonProperty("falseCount")
    private int falseCount = 0;

    /**
     * Creates a new instance of BooleanFieldProfiler.
     *
     * @param fieldName The name of the field to be profiled.
     */
    public BooleanFieldProfiler(final String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void add(final Boolean dataObject) {
        sampleSize++;
        if (dataObject == null) {
            nullCount++;
            return;
        }
        if (dataObject) {
            trueCount++;
        } else {
            falseCount++;
        }
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
