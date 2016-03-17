package org.bml.util.fabric;

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

/**
 * FabricNode represents a particle in a 2d plane. It can be thought of as a 
 * coordinate in 2d space that is aware of and able to act on its surrounding 
 * FabricNode objects.
 * 
 * @author Brian M. Lima
 */
public abstract class FabricNode<T> {

    private FabricNode<T> north=null;
    private FabricNode<T> south=null;
    private FabricNode<T> east=null;
    private FabricNode<T> west=null;
    
    private final T payload;

    public FabricNode(T payload){
        this.payload=payload;
    }
    
}
