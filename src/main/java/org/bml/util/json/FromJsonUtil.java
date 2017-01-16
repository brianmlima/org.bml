/*
 */
package org.bml.util.json;

/**
 * A simple interface user for deserialization from raw json.
 *
 * @author Brian M. Lima
 * @param <T>
 */
public interface FromJsonUtil<T> {

    public T fromJson(final String rawJson) throws Exception;
}
