
package org.bml.util.rt.telemetry.track;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;

/**
 * The integer Track class is used to replace an AtomicInteger when you want to
 * track the telemetry of the AtomicInteger.
 *
 * Note: this class blocks in order to accurately track the integers values. As
 * a result it is not ment for high speed implementations and should only be
 * used in testing or in situations where you need the Atomic Integer and wish
 * to track values.
 *
 * @author Brian M. Lima
 */
public class AtomicIntegerTrack implements AtomicIntegerInterface {

  /**
   * standard commons Logging
   */
  private Log LOG = LogFactory.getLog(AtomicIntegerTrack.class);
  private final TRACK_TYPE trackType;

  /**
   *
   */
  public static enum TRACK_TYPE {

    DESCRIPTIVE,
    SUMMARY
  }

  /**
   * Maps a Key to a byte value and a string for use in tracking method call
   * frequency.
   */
  public static enum FUNCTION_KEY {

    /**
     * Function id for @see java.util.concurrent.atomic.AtomicInteger#get() *
     */
    GET((byte) 1, "get"),
    /**
     * Function id for @see java.util.concurrent.atomic.AtomicInteger#set(int
     * newValue) *
     */
    SET((byte) 2, "set"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#lazySet(int newValue) *
     */
    LAZYSET((byte) 3, "lazySet"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#getAndSet(int newValue) *
     */
    GETANDSET((byte) 4, "getAndSet"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#compareAndSet(int expect, int
     * update) *
     */
    COMPAREANDSET((byte) 5, "compareAndSet"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#weakCompareAndSet(int expect,
     * int update) *
     */
    WEAKCOMPAREANDSET((byte) 6, "weakCompareAndSet"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#getAndIncrement() *
     */
    GETANDINCREMENT((byte) 7, "getAndIncrement"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#getAndDecrement() *
     */
    GETANDDECREMENT((byte) 8, "getAndDecrement"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#getAndAdd(int delta) *
     */
    GETANDADD((byte) 9, "getAndAdd"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#incrementAndGet() *
     */
    INCREMENTANDGET((byte) 10, "incrementAndGet"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#decrementAndGet() *
     */
    DECREMENTANDGET((byte) 11, "decrementAndGet"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#addAndGet(int delta) *
     */
    ADDANDGET((byte) 12, "addAndGet"),
    /**
     * Function id for @see java.util.concurrent.atomic.AtomicInteger#toString() *
     */
    TOSTRING((byte) 13, "toString"),
    /**
     * Function id for @see java.util.concurrent.atomic.AtomicInteger#intValue() *
     */
    INTVALUE((byte) 14, "intValue"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#longValue() *
     */
    LONGVALUE((byte) 15, "longValue"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#floatValue() *
     */
    FLOATVALUE((byte) 16, "floatValue"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#doubleValue() *
     */
    DOUBLEVALUE((byte) 17, "doubleValue"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#byteValue() *
     */
    BYTEVALUE((byte) 18, "byteValue"),
    /**
     * Function id for @see
     * java.util.concurrent.atomic.AtomicInteger#shortValue() *
     */
    SHORTVALUE((byte) 19, "shortValue");
    private byte value;
    private String sValue;

    FUNCTION_KEY(byte value, String sValue) {
      this.value = value;
      this.sValue = sValue;
    }

    public byte value() {
      return value;
    }

    @Override
    public String toString() {
      return sValue;
    }
  }

  private SummaryStatistics sStats = null;
  private DescriptiveStatistics dStats = null;
  private Frequency frequency = null;
  private final AtomicInteger atomicInteger;

  public AtomicIntegerTrack(TRACK_TYPE trackType) {
    this.trackType = trackType;
    this.atomicInteger = new AtomicInteger();
    initTrack(trackType);
  }

  private void track(AtomicIntegerTrack integerTrack) {

  }

  private void initTrack(TRACK_TYPE trackType) {
    switch (trackType) {
      case DESCRIPTIVE:
        dStats = new SynchronizedDescriptiveStatistics();
        break;
      case SUMMARY:
        sStats = new SummaryStatistics();
        break;
    }
    frequency = new Frequency();
  }

  /**
   * Gets the current value.
   *
   * @return the current value
   */
  public final int get() {
    frequency.addValue(FUNCTION_KEY.GET.value());
    return atomicInteger.get();
  }

  /**
   * Sets to the given value.
   *
   * @param newValue the new value
   */
  public final void set(int newValue) {
    frequency.addValue(FUNCTION_KEY.SET.value());
    atomicInteger.set(newValue);
  }

  /**
   * Eventually sets to the given value.
   *
   * @param newValue the new value
   * @since 1.6
   */
  public final void lazySet(int newValue) {
    frequency.addValue(FUNCTION_KEY.LAZYSET.value());
    atomicInteger.lazySet(newValue);
  }

  /**
   * Atomically sets to the given value and returns the old value.
   *
   * @param newValue the new value
   * @return the previous value
   */
  public final int getAndSet(int newValue) {
    frequency.addValue(FUNCTION_KEY.GETANDSET.value());
    return atomicInteger.getAndSet(newValue);
  }

  /**
   * Atomically sets the value to the given updated value if the current value
   * {@code ==} the expected value.
   *
   * @param expect the expected value
   * @param update the new value
   * @return true if successful. False return indicates that the actual value
   * was not equal to the expected value.
   */
  public final boolean compareAndSet(int expect, int update) {
    frequency.addValue(FUNCTION_KEY.COMPAREANDSET.value());
    return atomicInteger.compareAndSet(expect, update);
  }

  /**
   * Atomically sets the value to the given updated value if the current value
   * {@code ==} the expected value.
   *
   * <p>
   * May <a href="package-summary.html#Spurious">fail spuriously</a>
   * and does not provide ordering guarantees, so is only rarely an appropriate
   * alternative to {@code compareAndSet}.
   *
   * @param expect the expected value
   * @param update the new value
   * @return true if successful.
   */
  public final boolean weakCompareAndSet(int expect, int update) {
    frequency.addValue(FUNCTION_KEY.WEAKCOMPAREANDSET.value());
    return atomicInteger.weakCompareAndSet(expect, update);
  }

  /**
   * Atomically increments by one the current value.
   *
   * @return the previous value
   */
  public final int getAndIncrement() {
    frequency.addValue(FUNCTION_KEY.GETANDINCREMENT.value());
    return atomicInteger.getAndIncrement();
  }

  /**
   * Atomically decrements by one the current value.
   *
   * @return the previous value
   */
  public final int getAndDecrement() {
    frequency.addValue(FUNCTION_KEY.GETANDDECREMENT.value());
    return atomicInteger.getAndDecrement();
  }

  /**
   * Atomically adds the given value to the current value.
   *
   * @param delta the value to add
   * @return the previous value
   */
  public final int getAndAdd(int delta) {
    frequency.addValue(FUNCTION_KEY.GETANDADD.value());
    return atomicInteger.getAndAdd(delta);
  }

  /**
   * Atomically increments by one the current value.
   *
   * @return the updated value
   */
  public final int incrementAndGet() {
    frequency.addValue(FUNCTION_KEY.INCREMENTANDGET.value());
    return atomicInteger.incrementAndGet();
  }

  /**
   * Atomically decrements by one the current value.
   *
   * @return the updated value
   */
  public final int decrementAndGet() {
    return atomicInteger.decrementAndGet();
  }

  /**
   * Atomically adds the given value to the current value.
   *
   * @param delta the value to add
   * @return the updated value
   */
  public final int addAndGet(int delta) {
    frequency.addValue(FUNCTION_KEY.ADDANDGET.value());
    return atomicInteger.addAndGet(delta);
  }

  /**
   * Returns the String representation of the current value.
   *
   * @return the String representation of the current value.
   */
  public String toString() {
    frequency.addValue(FUNCTION_KEY.TOSTRING.value());
    return atomicInteger.toString();
  }

  public int intValue() {
    frequency.addValue(FUNCTION_KEY.INTVALUE.value());
    return atomicInteger.intValue();
  }

  public long longValue() {
    frequency.addValue(FUNCTION_KEY.LONGVALUE.value());
    return atomicInteger.longValue();
  }

  public float floatValue() {
    frequency.addValue(FUNCTION_KEY.FLOATVALUE.value());
    return atomicInteger.floatValue();
  }

  public double doubleValue() {
    frequency.addValue(FUNCTION_KEY.DOUBLEVALUE.value());
    return atomicInteger.doubleValue();
  }

  public byte byteValue() {
    frequency.addValue(FUNCTION_KEY.BYTEVALUE.value());
    return atomicInteger.byteValue();
  }

  public short shortValue() {
    frequency.addValue(FUNCTION_KEY.SHORTVALUE.value());
    return atomicInteger.shortValue();
  }

}
