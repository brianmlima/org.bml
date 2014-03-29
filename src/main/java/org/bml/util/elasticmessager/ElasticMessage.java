/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bml.util.elasticmessager;

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
 *
 * @author Brian M. Lima
 * @param <T>
 */
public class ElasticMessage<T> {

    private boolean hasBeenWriten = false;
    private long createdAt = -1;
    private long writenAt = -1;

    private T content;

    public void setHasBeenWriten(boolean value) {
        this.hasBeenWriten = value;
        if (value) {
            writenAt = System.currentTimeMillis();
        } else {
            writenAt = -1;
        }
    }

    /**
     * Creates a new Elastic Message with a generic typed content object.
     *
     * @param content
     */
    public ElasticMessage(T content) {
        this.content = content;
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * @return the hasBeenWriten
     */
    public boolean getHasBeenWriten() {
        return hasBeenWriten;
    }

    /**
     * @return the createdAt
     */
    public long getCreatedAt() {
        return createdAt;
    }

    /**
     * @return the writenAt
     */
    public long getWritenAt() {
        return writenAt;
    }

    /**
     * @return the content
     */
    public T getContent() {
        return content;
    }
}
