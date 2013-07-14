
package org.bml.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
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
    public static final long UTC_LONG_2005 = 1104537600000l;
    public static final Date UTC_DATE_2005 = new Date(UTC_LONG_2005);

    public static String dayOfWeekFromInt(int day) {
        if (day > 7 || day < 1) {
            return null;
        }
        switch (day) {
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
            default:
                return null;
        }
    }

    
    public static int getHourOfWeek(Date dateIn){
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateIn);
        int hour =((cal.get(Calendar.DAY_OF_WEEK)-1)*24)+cal.get(Calendar.HOUR_OF_DAY) ;
        return hour;
    } 
    
    public static Date truncateToWeek(Date dateIn){
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateIn);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        return DateUtils.truncate(cal.getTime(), Calendar.DATE);
    }
    
        public static long timeUnitsToMilliseconds(final TimeUnit theTimeUnit, final long unitCount ){        
        switch(theTimeUnit){
            case MILLISECONDS : return unitCount ; 
            case SECONDS : return unitCount*1000 ; 
            case MINUTES : return (unitCount*60)*1000 ; 
        }
        return -1;
    }
    
}
