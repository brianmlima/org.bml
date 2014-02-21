
package org.bml.util.zoo;

/**
 * Other classes use the DataMonitor by implementing this method
 */
public interface DataMonitorListener {
    /**
     * The existence status of the node has changed.
     * @param data
     */
    void exists(byte data[]);

    /**
     * The ZooKeeper session is no longer valid.
     * @param rc the ZooKeeper reason code
     */
    void closing(int rc);
}
