
package org.bml.util.alert;

import java.util.Date;
import org.bml.util.exception.DisabledException;
import org.bml.util.ratelimitor.RateExceededException;

/**Interface all Alert Handlers must implement
 * @author Brian M. Lima
 */
public interface AlertHandler {
    
    public Date trigger(String subject, String body)throws DisabledException, RateExceededException;

    public void disable();

    public void enable();

    public boolean isEnabled();

    public Date lastTriggered();

    public long getMinTriggerInterval();

    public void setMinTriggerInterval(long interval);
}
