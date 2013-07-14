/**
 *   This file is part of org.bml.
 *
 *   org.bml is free software: you can redistribute it and/or modify it under the
 *   terms of the GNU General Public License as published by the Free Software
 *   Foundation, either version 3 of the License, or (at your option) any later
 *   version.
 *
 *   org.bml is distributed in the hope that it will be useful, but WITHOUT ANY
 *   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 *   A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License along with
 *   org.bml. If not, see <http://www.gnu.org/licenses/>.
 */


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