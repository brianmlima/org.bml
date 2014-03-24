/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bml.util.elasticmessager;

/**
 *
 * @author Brian M. Lima
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
