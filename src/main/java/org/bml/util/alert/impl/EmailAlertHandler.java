
package org.bml.util.alert.impl;

import java.util.Date;
import org.bml.util.alert.AlertHandler;
import org.bml.util.exception.DisabledException;
import org.bml.util.ratelimitor.RateExceededException;
import org.bml.util.mail.MailUtils;

/**Simple alert handler for email. Can be used as a plugin to the Alert 
 * framework has controls for rate limiting and generic trigger functionality.
 * 
 * @author Brian M. Lima
 */
public class EmailAlertHandler implements AlertHandler {

    private boolean isEnabled = false;
    private Date lastTriggered = null;
    private long minTriggerInterval = -1;
    private String host = null;
    private String sender = null;
    private String password = null;
    private String port = null;
    private String[] recipients = null;

    /**
     * @param host
     * @param sender
     * @param password
     * @param port
     * @param recipients
     */
    public EmailAlertHandler(String host, String sender, String password, String port, String recipients[]) {
        this.host = host;
        this.sender = sender;
        this.password = password;
        this.port = port;
        this.recipients = recipients;
    }

    /**
     *
     * @param subject
     * @param body
     * @return Date UTC representation of the time the alert was actually 
     * triggered NOTE this is the time the alert was completed and confirmed 
     * sent not the time it was sent.
     * @throws DisabledException
     * @throws RateExceededException
     */
    public synchronized Date trigger(String subject, String body) throws DisabledException, RateExceededException {
        if (!this.isEnabled) {
            throw new DisabledException("This AlertHandler is not enabled and will not be fired.");
        }
        boolean trigger = false;
        if (this.lastTriggered != null) {
            this.lastTriggered = new Date();
            trigger = true;
        }else if (this.minTriggerInterval == -1) {
            trigger = true;
        } else if ((System.currentTimeMillis() - this.lastTriggered.getTime()) < this.minTriggerInterval) {
            throw new RateExceededException("This AlertHandler has exceeded its rate limit and will not be fired.",0);
        } else {
            trigger = true;
        }
        MailUtils.sendMail(recipients, subject, body, host, sender, password, port);
        this.lastTriggered = new Date();
        return this.lastTriggered;
    }
    /**
     * Disable this alert handler
     */
    public synchronized void disable() {
        isEnabled = false;
    }

    /**
     *Enable this alert handler
     */
    public synchronized void enable() {
        isEnabled = true;
    }

    /**
     * @return true if this handler is enabled, false otherwise
     */
    public synchronized boolean isEnabled() {
        return isEnabled;
    }

    /**
     * @return the Date this handler was last triggered.
     */
    public Date lastTriggered() {
        return lastTriggered;
    }

    /**
     * @return the min number of milliseconds between allowed triggers.
     */
    public long getMinTriggerInterval() {
        return minTriggerInterval;
    }

    /**
     * @param minTriggerInterval the minimum 
     */
    public void setMinTriggerInterval(long minTriggerInterval) {
        this.minTriggerInterval = minTriggerInterval;
    }
}
