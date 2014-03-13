package org.bml.util.alert.impl;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2014 Brian M. Lima
 * %%
 * This file is part of ORG.BML.
 * 
 *     ORG.BML is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     ORG.BML is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with ORG.BML.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import java.util.Arrays;
import java.util.Date;
import org.bml.util.args.ArgumentUtils;
import org.bml.util.alert.AlertHandler;
import org.bml.util.exception.DisabledException;
import org.bml.util.ratelimitor.RateExceededException;
import org.bml.util.mail.MailUtils;

/**
 * Simple alert handler for email. Can be used as a plugin to the Alert
 * framework has controls for rate limiting and generic trigger functionality.
 *
 * @author Brian M. Lima
 */
public class EmailAlertHandler implements AlertHandler {
    /**
     * if true then this handler is enabled.
     */
    private boolean isEnabled = false;
    /**
     * the long mills this handler was last triggered
     */
    private long lastTriggeredMills = -1;
    //
    private long minTriggerIntervalMills = -1;
    private String host = null;
    private String sender = null;
    private String password = null;
    private int port;
    private String[] recipients = null;

    private final Object STATE_LOCK = new Object();

    
    /**
     * @param host
     * @param sender
     * @param password
     * @param port the port the smtp server can be found on (between 0 and 65535) 
     * @param recipients The 
     * @throws IllegalArgumentException if any of the pre-conditions are not met.
     * 
     * @pre host != null && !host.isEmpty()
     * @pre sender != null && !sender.isEmpty()
     * @pre GenericValidator.isInRange(port, NetworkUtils.MIN_IPV4_NETWORK_PORT, NetworkUtils.MAX_IPV4_NETWORK_PORT)
     * @pre password != null
     * @pre recipients != null && recipients.length > 0
     */
    public EmailAlertHandler(final String host, final String sender, final String password, final int port, final String recipients[]) throws IllegalArgumentException{
        ArgumentUtils.checkStringArg(host, "host parameter", false, false);
        ArgumentUtils.checkStringArg(sender, "sender parameter", false, false);
        ArgumentUtils.checkStringArg(password, "password parameter", false, true);
        ArgumentUtils.checkIPV4Port(port);
        
        this.host = host;
        this.sender = sender;
        this.password = password;
        this.port = port;
        this.recipients = Arrays.copyOf(recipients, recipients.length);
    }

    /**
     * Attempts to trigger an e-mail alert.
     *
     * @param subject the {@link String} subject line for the email alert.
     * @param body the {@link String} email body for the email alert.
     * @return Date UTC representation of the time the alert was actually
     * triggered NOTE this is the time the alert was completed and confirmed
     * sent not the time it was sent.
     * @throws DisabledException if this {AlertHandler} implementation is not enabled.
     * @throws RateExceededException If the rate of email alerts exceeds the minTriggerIntervalMills.
     * @pre <code>subject != null;</code>
     * @pre <code>body !=null;</code>
     */
    public Date trigger(final String subject, final String body) throws DisabledException, RateExceededException {
        synchronized (STATE_LOCK) {
            if (!this.isEnabled) {
                throw new DisabledException("This AlertHandler is not enabled and will not be fired.");
            }
            boolean trigger = false;
            if (this.lastTriggeredMills == -1) {
                trigger = true;
            } else if (this.minTriggerIntervalMills == -1) {
                trigger = true;
            } else if ((System.currentTimeMillis() - this.lastTriggeredMills) < this.minTriggerIntervalMills) {
                throw new RateExceededException("This AlertHandler has exceeded its rate limit and will not be fired.", 0);
            } else {
                trigger = true;
            }
            if (!trigger) {
                return null;
            }
            MailUtils.sendMail(recipients, subject, body, host, sender, password, port);
            this.lastTriggeredMills = System.currentTimeMillis();
            return this.lastTriggered();//use method to avoid exposing internal member
        }
    }

    /**
     * Disable this alert handler
     */
    public void disable() {
        synchronized (STATE_LOCK) {
            isEnabled = false;
        }
    }

    /**
     * Enable this alert handler
     */
    public void enable() {
        synchronized (STATE_LOCK) {
            isEnabled = true;
        }
    }

    /**
     * @return true if this handler is enabled, false otherwise
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * <p>
     * Gets a Date representation of the last time this handler was triggered 
     * or null if this handler has never been triggered.
     * </p>
     * 
     * @return the Date this handler was last triggered in Milliseconds.
     */
    public Date lastTriggered() {
        synchronized (STATE_LOCK) {
            if (this.lastTriggeredMills == -1) {
                return null;
            }
            return new Date(lastTriggeredMills);
        }
    }

    /**
     * <p>
     * Gets the minimum interval between allowed trigger actions in milliseconds
     * </p>
     * @return the min number of milliseconds between allowed triggers. Will 
     * be -1 if this handler has never been triggered
     */
    public long getMinTriggerInterval() {
        return minTriggerIntervalMills;
    }

    /**
     * <p>
     * Sets the minimum interval between allowed trigger actions in milliseconds.
     * </p>
     * @param minTriggerIntervalMills the minimum milliseconds between allowed trigger actions.
     */
    public void setMinTriggerInterval(long minTriggerIntervalMills) {
        this.minTriggerIntervalMills = minTriggerIntervalMills;
    }
}
