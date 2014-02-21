
package org.bml.util.rt.telemetry.track;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian M. Lima
 */
public class AtomicIntegerTelemetry implements AtomicIntegerInterface {

  @Override
  public final int get() {
    return atomicInteger.get();
  }

  @Override
  public final void set(int newValue) {
    atomicInteger.set(newValue);
  }

  @Override
  public final void lazySet(int newValue) {
    atomicInteger.lazySet(newValue);
  }

  @Override
  public final int getAndSet(int newValue) {
    return atomicInteger.getAndSet(newValue);
  }

  @Override
  public final boolean compareAndSet(int expect, int update) {
    return atomicInteger.compareAndSet(expect, update);
  }

  @Override
  public final boolean weakCompareAndSet(int expect, int update) {
    return atomicInteger.weakCompareAndSet(expect, update);
  }

  @Override
  public final int getAndIncrement() {
    return atomicInteger.getAndIncrement();
  }

  @Override
  public final int getAndDecrement() {
    return atomicInteger.getAndDecrement();
  }

  @Override
  public final int getAndAdd(int delta) {
    return atomicInteger.getAndAdd(delta);
  }

  @Override
  public final int incrementAndGet() {
    return atomicInteger.incrementAndGet();
  }

  @Override
  public final int decrementAndGet() {
    return atomicInteger.decrementAndGet();
  }

  @Override
  public final int addAndGet(int delta) {
    return atomicInteger.addAndGet(delta);
  }

  @Override
  public String toString() {
    return atomicInteger.toString();
  }

  @Override
  public int intValue() {
    return atomicInteger.intValue();
  }

  @Override
  public long longValue() {
    return atomicInteger.longValue();
  }

  @Override
  public float floatValue() {
    return atomicInteger.floatValue();
  }

  @Override
  public double doubleValue() {
    return atomicInteger.doubleValue();
  }

  @Override
  public byte byteValue() {
    return atomicInteger.byteValue();
  }

  @Override
  public short shortValue() {
    return atomicInteger.shortValue();
  }

  /**
   * standard commons Logging
   */
  private static final Log LOG = LogFactory.getLog(AtomicIntegerTelemetry.class);

  private AtomicInteger atomicInteger = new AtomicInteger();
}
