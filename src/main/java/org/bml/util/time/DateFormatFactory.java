
package org.bml.util.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.apache.commons.pool.BasePoolableObjectFactory;

/**
 * The Java Specification states clearly that DateFormat is not thread safe. As
 * a result we see odd behavior from DateFormat in a threaded environment. The
 * higher concurrency the greater the effect. As a result we use a pool whenever
 * we are in a threaded environment.
 *
 * @author Brian M. Lima
 */
public class DateFormatFactory extends BasePoolableObjectFactory<DateFormat> {

    /**
     * Store the origin format definition.
     */
    private String stringFormat = null;
    /**
     * The default time zone
     */
    private TimeZone defaultTimeZone = null;

    /**
     * @param stringFormat a SimpleDateFormat string
     */
    public DateFormatFactory(String stringFormat, TimeZone defaultTimeZone) {
        super();
        this.stringFormat = stringFormat;
        this.defaultTimeZone = defaultTimeZone;
    }

    @Override
    public DateFormat makeObject() {
        DateFormat df = new SimpleDateFormat(stringFormat);
        df.setTimeZone(defaultTimeZone);
        return df;
    }

    @Override
    public void passivateObject(DateFormat aDateFormat) {
    }
}
