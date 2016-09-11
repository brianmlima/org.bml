/**
 * This file is part of org.bml.
 *
 * org.bml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.bml is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.bml. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bml.util;

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
 *
 * This class is a data container for the Configuration Key Enumeration pattern.
 *
 * @author Brian M. Lima
 */
public class ConfigKey {

    /**
     * Creates a new ConfigKey instance.
     *
     * @param required Is the config value required.
     * @param defaultValue The default value.
     * @param valueClass The expected class of the value.
     * @param propertyName The name of the property that contains the configuration value.
     */
    public ConfigKey(final boolean required, final Object defaultValue, final Class valueClass, final String propertyName) {
        this.required = required;
        this.defaultValue = defaultValue;
        this.valueClass = valueClass;
        this.propertyName = propertyName;
    }

    /**
     * Is the config key required.
     */
    private final boolean required;
    /**
     * the keys default value.
     */
    private final Object defaultValue;
    /**
     * The class of the value.
     */
    private final Class valueClass;
    /**
     * The name of the property.
     */
    private final String propertyName;

    /**
     * Is the config key required.
     *
     * @return the required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * the keys default value.
     *
     * @return the theDefaultValue
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * The class of the value.
     *
     * @return the theValueClass
     */
    public Class getValueClass() {
        return valueClass;
    }

    /**
     * The name of the property.
     *
     * @return the thePropertyName
     */
    public String getPropertyName() {
        return propertyName;
    }

}
