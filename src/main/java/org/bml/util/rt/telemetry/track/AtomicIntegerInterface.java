
package org.bml.util.rt.telemetry.track;

/**
 * @author Brian M. Lima
 */
public interface AtomicIntegerInterface {

  int addAndGet(int delta);

  byte byteValue();

  boolean compareAndSet(int expect, int update);

  int decrementAndGet();

  double doubleValue();

  float floatValue();

  int get();

  int getAndAdd(int delta);

  int getAndDecrement();

  int getAndIncrement();

  int getAndSet(int newValue);

  int incrementAndGet();

  int intValue();

  void lazySet(int newValue);

  long longValue();

  void set(int newValue);

  short shortValue();

  boolean weakCompareAndSet(int expect, int update);
  
}
