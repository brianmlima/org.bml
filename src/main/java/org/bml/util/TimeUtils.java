package org.bml.util;

import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class should not exist. It will survive until I find a better place for
 * time constants or an easy non-error prone way of building them.
 *
 * If anything it is a place for time constants so we can get an idea of what
 * this utility should do in the future.
 *
 * @author Brian M. Lima
 */
public class TimeUtils {

    private static final Log LOG = LogFactory.getLog(TimeUtils.class);
    //Arbitrary date for windowing dates
    public static final long UTC_LONG_2005 = 1104537600000l;
    //Arbitrary Date for windowing dates
    public static final Date UTC_DATE_2005 = new Date(UTC_LONG_2005);

    /**
     * Conversion utility for getting a print friendly string representation of
     * a day of the week from a numeric between 1 and 7. Sunday is 1.
     *
     * @param theDay an integer between 1 and 7 denoting the day of the week. 1
     * being Sunday.
     * @return a print friendly string representation of a day of the week. 1
     * being Sunday.
     * @throws IllegalArgumentException if argument theDay is not in range of 1 to 7
     */
    public static String dayOfWeekFromInt(final int theDay) throws IllegalArgumentException {
        if (theDay > 7 || theDay < 1) {
            throw new IllegalArgumentException("Argument theDay is not in range of 1 to 7");
        }
        switch (theDay) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default: //unreachable but the compiler wants it!
                return null;
        }
    }

    /**Returns the hour of the week in the range of 1 to 168. 1 being the first hour of 
     * Sunday, 168 being the last hour of Saturday.
     * 
     * @param dateIn the date to extract the hour of the week.
     * @return an integer in the range of 1 to 168. 1 being the first hour of 
     * Sunday.
     * @throws IllegalArgumentException if the parameter dateIn is null.
     */
    public static int getHourOfWeek(Date dateIn) {
        if(dateIn==null){
            throw new IllegalArgumentException("Can not operate on a null Date object.");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateIn);
        return ((cal.get(Calendar.DAY_OF_WEEK) - 1) * 24) + cal.get(Calendar.HOUR_OF_DAY);
        
    }

    /**Truncates a Date object to the week. Truncation is based on Sunday being 
     * the first day of the calendar week.
     * 
     * @param dateIn a date to truncate to the week.
     * @return a truncated to the week version of the dateIn parameter.
     * @throws IllegalArgumentException if the parameter dateIn is null.
     */
    public static Date truncateToWeek(Date dateIn) throws IllegalArgumentException {
        if(dateIn==null){
            throw new IllegalArgumentException("Can not operate on a null Date object.");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateIn);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        return DateUtils.truncate(cal.getTime(), Calendar.DATE);
    }
}
