package org.bml.util.time;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * Pool for StopWatch objects. Good for use when tracking telemetry at scale.
 *
 * @author Brian M. Lima
 */
public class StopWatchPool extends GenericObjectPool<StopWatch> {

    /**
     * Standard logging
     */
    private final Log LOG = LogFactory.getLog(StopWatchPool.class);

    /**
     * If true this object will act internally as a pool, if false it will act 
     * like an object factory and returned instances will eventually be GC'd.
     */
    private boolean usePool = true;
    private AtomicInteger stopWatchBorrowCount, stopWatchReturnCount;

    /**
     * Creates an extension of <code>GenericObjectPool<StopWatch></code> using
     * the passed configuration argument.
     *
     * @param config
     * {@link org.apache.commons.pool.impl.GenericObjectPool.Config}
     */
    public StopWatchPool(GenericObjectPool.Config config) {
        super(new StopWatchFactory(), config);
        this.stopWatchBorrowCount = new AtomicInteger(0);
        this.stopWatchReturnCount = new AtomicInteger(0);
    }

    /**
     * A wrapper for
     * {@link org.apache.commons.pool.impl.GenericObjectPool#borrowObject()}
     * this method handles potential exceptions and falls back to new object
     * creation if the pool is broken.
     *
     * @return An instance of StopWatch.
     */
    public StopWatch borrowStopWatch() {
        StopWatch watch = null;
        if (this.usePool) {
            try {
                watch = borrowObject();
                this.stopWatchBorrowCount.incrementAndGet();
                return watch;
            } catch (Exception ex) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("An error was encountered while trying to borrow from the StopWatch pool. The pool will be disabled.", ex);
                }
                this.usePool = false;
            }
        }
        if (!this.usePool) {
            return new StopWatch();
        }
        return null;
    }

    /**
     * A wrapper for
     * {@link org.apache.commons.pool.impl.GenericObjectPool#returnObject(java.lang.Object) }
     * this method handles potential exceptions.
     *
     * @param stopWatch
     */
    public void returnStopWatch(StopWatch stopWatch) {
        if (stopWatch == null) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Attempts to return a null StopWatch are being made. Check edge cases.");
            }
            return;
        }
        if (this.usePool) {
            try {
                this.returnObject(stopWatch);
                this.stopWatchReturnCount.incrementAndGet();
            } catch (Exception ex) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("An error was encountered while trying to return an object to the StopWatch pool. The pool will be disabled.", ex);
                }
                //Disable Pool.
                this.usePool = false;
            }
        }
    }
}

/**
 * 
 * @author Brian M. Lima
 */
class StopWatchFactory implements PoolableObjectFactory<StopWatch> {

    @Override
    public StopWatch makeObject() throws Exception {
        return new StopWatch();
    }

    @Override
    public void destroyObject(StopWatch obj) throws Exception {
        obj.reset();
    }

    @Override
    public boolean validateObject(StopWatch obj) {
        return true;
    }

    @Override
    public void activateObject(StopWatch obj) throws Exception {

    }

    @Override
    public void passivateObject(StopWatch obj) throws Exception {
        obj.reset();
    }

}
