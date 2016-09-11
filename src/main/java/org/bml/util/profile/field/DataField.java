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
/**
 * An annotation for use to denote a data field.
 *
 * This is used in classes where fields in the class are mapped to things like SQL databases.
 * It allows passing information to the runtime that allows static methods that
 * use reflection and annotations to build ORM's.
 *
 * @author Brian M. Lima
 */
public @interface DataField {

    /**
     * True if this field should be ignored, false otherwise.
     */
    boolean ignore() default false;
}
