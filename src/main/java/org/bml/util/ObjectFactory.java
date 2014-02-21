
package org.bml.util;

/** This class is just a holder so people do not have to use
 * PoolableObjectFactory or import commons pooling in core java projects.
 * @author Brian M. Lima
 */
public interface ObjectFactory<T> {

    public T makeObject();

    public boolean destroyObject(T obj);
}
