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
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.RateLimiter;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.validator.routines.EmailValidator;
import org.bml.util.alert.AlertHandler;
import org.bml.util.args.ArgumentUtils;
import org.bml.util.exception.DisabledException;
import org.bml.util.io.net.NetworkUtils;
import org.bml.util.mail.MailUtils;
import org.bml.util.exception.RateExceededException;

/**
 * Simple alert handler for email. Can be used as a plugin to the Alert
 * framework has controls for rate limiting and generic trigger functionality.
 *
 * @author Brian M. Lima
 */
public class EmailAlertHandler implements AlertHandler {

    public static boolean CHECKED = true;

    /**
     * if true then this handler is enabled.
     */
    private boolean isEnabled = false;

    private RateLimiter theRateLimiter;

    /**
     * the long mills this handler was last triggered
     */
    private long lastTriggeredMills = -1;

    private int overRateWaitInSeconds = 2;

    //
    private String host = null;
    private String sender = null;
    private String password = null;
    private int smtpPort;
    private String[] recipients = null;

    /**
     * @param host
     * @param sender
     * @param password
     * @param smtpPort the port the smtp server can be found on (between 0 and 65535)
     * @param recipients The email recipiants
     * @param maxRatePerSecond the maximum number of times per second this AlertHandler can be triggered.
     * @throws IllegalArgumentException if any of the pre-conditions are not met.
     *
     * @pre host != null && !host.isEmpty()
     * @pre sender != null && !sender.isEmpty()
     * @pre GenericValidator.isInRange(port, NetworkUtils.MIN_IPV4_NETWORK_PORT, NetworkUtils.MAX_IPV4_NETWORK_PORT)
     * @pre password != null
     * @pre recipients != null && recipients.length > 0
     * @pre maxRatePerSecond > 0
     */
    public EmailAlertHandler(final String host, final String sender, final String password, final int smtpPort, final String recipients[], double maxRatePerSecond) throws IllegalArgumentException {
        if (CHECKED) {
            Preconditions.checkNotNull(host, "host parameter can not be null.");
            Preconditions.checkArgument(!host.isEmpty(), "host parameter can not be empty.");

            Preconditions.checkNotNull(sender, "sender parameter can not be null. host=%s", host);
            Preconditions.checkArgument(!sender.isEmpty(), "sender parameter can not be empty. host=%s", host);

            Preconditions.checkNotNull(password, "password parameter can not be null. host=%s sender=%s", host, sender);

            //check e-mail and allow local adresses
            EmailValidator validator = EmailValidator.getInstance(true);
            Preconditions.checkArgument(validator.isValid(sender), "sender parameter %s is not a valid e-mail. host=%s", sender, host);

            Preconditions.checkNotNull(recipients, "recipients parameter can not be null. host=%s sender=%s", host, sender);
            Preconditions.checkArgument(recipients.length > 0, "recipients parameter can not be zero length. host=%s sender=%s", host, sender);
            for (int c = 0; c < recipients.length; c++) {
                Preconditions.checkArgument(validator.isValid(recipients[c]), "recipients entry %s value %s is not a valid e-mail.  host=%s sender=%s", c, recipients[c], host, sender);
            }
            //check i4 port range... in theory
            Preconditions.checkArgument((smtpPort >= NetworkUtils.MIN_IPV4_NETWORK_PORT && smtpPort <= NetworkUtils.MAX_IPV4_NETWORK_PORT), "smtpPort %s is out of range %s - %s", smtpPort, NetworkUtils.MIN_IPV4_NETWORK_PORT, NetworkUtils.MAX_IPV4_NETWORK_PORT);
            //check rate for RateLimitor
            Preconditions.checkArgument(maxRatePerSecond > 0, "the maxRatePerSecond parameter must be greater than zero for this AlertHandler implementation to function.");

        }
        this.host = host;
        this.sender = sender;
        this.password = password;
        this.smtpPort = smtpPort;
        this.recipients = Arrays.copyOf(recipients, recipients.length);
        this.theRateLimiter = RateLimiter.create(maxRatePerSecond);
    }

    /**
     * Attempts to trigger an e-mail alert.
     *
     * @param subject the {@link String} subject line for the email alert.
     * @param body the {@link String} email body for the email alert.
     * @return long UTC representation of the time the alert was actually
     * triggered NOTE this is the time the alert was completed and confirmed
     * sent not the time it was sent.
     * @throws DisabledException if this {AlertHandler} implementation is not enabled.
     * @throws RateExceededException If the rate of email alerts exceeds the minTriggerIntervalMills.
     * @pre <code>subject != null;</code>
     * @pre <code>body !=null;</code>
     * @todo Retool the Exception messages to be more expressive.
     */
    public long trigger(final String subject, final String body) throws DisabledException, RateExceededException {
        //Throw if disabled
        if (!this.isEnabled) {
            throw new DisabledException("This AlertHandler is not enabled and will not be fired.");
        }
        //Throw if unable to get permit to trigger
        if (!this.theRateLimiter.tryAcquire(overRateWaitInSeconds, TimeUnit.SECONDS)) {
            throw new RateExceededException("This AlertHandler has exceeded its rate limit and will not be fired.", 0, 0);
        }
        //Mail and set triggered
        MailUtils.sendMail(recipients, subject, body, host, sender, password, smtpPort);
        final Date triggeredAt = new Date(System.currentTimeMillis());
        this.lastTriggeredMills = triggeredAt.getTime();
        return triggeredAt.getTime();
    }

    /**
     * Disable this alert handler
     */
    public void disable() {
        isEnabled = false;
    }

    /**
     * Enable this alert handler
     */
    public void enable() {
        isEnabled = true;
    }

    /**
     * @return true if this handler is enabled, false otherwise
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * <p>
     * Gets a long representation of the last time this handler was triggered in mills.
     * </p>
     *
     * @return the time this handler was last triggered in Milliseconds.
     */
    public long lastTriggered() {
        return lastTriggeredMills;
    }

    /**
     * <p>
     * Gets the maximum allowed trigger actions per second
     * </p>
     *
     * @return the maximum allowed trigger actions per second.
     */
    public double getMaxTriggersPerSecond() {
        return this.theRateLimiter.getRate();
    }

    /**
     * <p>
     * Sets the maximum allowed trigger actions per second.
     * </p>
     *
     * @param permitPerSecond the maximum allowed triggers per second.
     */
    public void setMaxTriggersPerSecond(final double permitPerSecond) {
        theRateLimiter.setRate(permitPerSecond);
    }
}
