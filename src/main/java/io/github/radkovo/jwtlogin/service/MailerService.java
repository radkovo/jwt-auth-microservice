/**
 * MailerService.java
 *
 * Created on 5. 10. 2021, 20:07:22 by burgetr
 */
package io.github.radkovo.jwtlogin.service;

import java.util.Date;
import java.util.Optional;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.github.radkovo.jwtlogin.data.PasswordChallenge;

/**
 * 
 * @author burgetr
 */
@Stateless
public class MailerService
{
    @Inject
    @ConfigProperty(name = "jwtauth.smtp.host", defaultValue = "localhost")
    String smtpHost;
    
    @Inject
    @ConfigProperty(name = "jwtauth.smtp.port", defaultValue = "25")
    String smtpPort;
    
    @Inject
    @ConfigProperty(name = "jwtauth.smtp.username")
    Optional<String> smtpUsername;
    
    @Inject
    @ConfigProperty(name = "jwtauth.smtp.password")
    Optional<String> smtpPassword;
    
    @Inject
    @ConfigProperty(name = "jwtauth.mail.sender")
    String fromEmail;
    
    @Inject
    @ConfigProperty(name = "jwtauth.reset.url")
    String resetUrl;
    
    @Inject
    @ConfigProperty(name = "jwtauth.product")
    String productName;
    

    public void sendPasswordReset(PasswordChallenge challenge) throws MessagingException
    {
        final String subject = productName + " password reset";
        final String link = resetUrl.replace("{}", challenge.getHash());
        final String message = 
                "Dear " + productName + " user,\n\n" +
                "someone (probably you) has requested a password reset for " + productName + ". " +
                "If you wish to reset your password, please click the following link:\n\n" +
                link + "\n\n" +
                "Your user ID is " + challenge.getUser().getUsername() + ".\n\n" +
                "If it was not you, you may safely ignore this message.\n\n" +
                "Best regards\n" + 
                "Your " + productName + " team";
        sendEmail(challenge.getUser().getEmail(), subject, message);
    }
    
    public void sendEmail(String toEmail, String subject, String text) throws MessagingException
    {
        final Message msg = new MimeMessage(createSession());
        msg.setFrom(new InternetAddress(fromEmail));
        InternetAddress[] toAddresses = { new InternetAddress(toEmail) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(text);
        Transport.send(msg);
    }
    
    private Session createSession()
    {
        Authenticator authenticator = null;
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", Integer.valueOf(smtpPort));
        if (!smtpUsername.isPresent() || !smtpPassword.isPresent())
        {
            properties.put("mail.smtp.auth", false);
        }
        else
        {
            properties.put("mail.smtp.auth", true);
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
            //properties.put("mail.smtp.starttls.enable", false);
            authenticator = new Authenticator() {
                private PasswordAuthentication pa = new PasswordAuthentication(smtpUsername.get(), smtpPassword.get());
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
        }
        Session session = Session.getInstance(properties, authenticator);
        return session;
    }
    
}
