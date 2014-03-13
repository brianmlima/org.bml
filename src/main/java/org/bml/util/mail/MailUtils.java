
package org.bml.util.mail;

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

import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian M. Lima
 */
public class MailUtils {

        /**
     * Standard Logging. All logging should be funneled through this log so we
     * can use adaptors for ELB and in house with the same results.
     */
    private static final Log LOG = LogFactory.getLog(MailUtils.class);
    /**
     * Use to avoid calling .class.getName() in high throughput situations
     */
    private static final String CLASS_NAME = MailUtils.class.getName();
    /**
     * Use to avoid calling .class.getSimpleName() in high throughput situations
     */
    private static final String SIMPLE_CLASS_NAME = MailUtils.class.getSimpleName();

    public static enum MAIL_STATE{
        SENT,
        SENT_FAIL_NOTICE,
        SENT_RECOVERY_NOTICE;
    }
    
    
    /**
     * Simple mail utility
     * @param sendToAdresses email addresses to send the mail to
     * @param emailSubjectLine the subject of the email.
     * @param emailBody The body of the mail
     * @param smtpHost the smtp host
     * @param sender the mail address that is the sender
     * @param smtpPassword the password for the sender
     * @param smtpPort the port to contact the smtp server on
     * @return boolean true on success and false on error
     */
    public static boolean sendMail(final String[] sendToAdresses, final String emailSubjectLine, final String emailBody,final String smtpHost,String sender,String smtpPassword,final int smtpPort) {
        if ((sendToAdresses == null) || (sendToAdresses.length == 0)) {
            return false;
        }

        if ((emailSubjectLine == null) || (emailBody == null)) {
            return false;
        }

        try {
            Address[] addresses = new Address[sendToAdresses.length];
            for (int i = 0; i < sendToAdresses.length; i++) {
                addresses[i] = new InternetAddress(sendToAdresses[i]);
            }

            Properties props = System.getProperties();
            props.setProperty("mail.smtp.host", smtpHost);
            props.setProperty("mail.smtp.localhost", smtpHost);
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.port", String.valueOf(smtpPort));
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getDefaultInstance(props, null);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(emailSubjectLine);
            message.setContent(emailBody, "text/plain");
            message.saveChanges();

            Transport transport = session.getTransport("smtp");
            transport.connect(smtpHost, sender, smtpPassword);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Throwable t) {
            if(LOG.isErrorEnabled()){
                LOG.error("Error occured while sending mail.",t);
            }
            return false;
        }
        return true;
    }
}
