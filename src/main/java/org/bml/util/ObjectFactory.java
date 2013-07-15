
package org.bml.util;

/*
 * #%L
 * orgbml
 * %%
 * Copyright (C) 2008 - 2013 Brian M. Lima
 * %%
 * This file is part of org.bml.
 * 
 * org.bml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.bml is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with org.bml.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
/** This class is just a holder so people do not have to use
 * PoolableObjectFactory or import commons pooling in core java projects.
 * @author Brian M. Lima
 */
public interface ObjectFactory<T> {

    public T makeObject();

    public boolean destroyObject(T obj);
}