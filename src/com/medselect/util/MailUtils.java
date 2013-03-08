package com.medselect.util;
import com.medselect.audit.AuditLogManager;
import com.medselect.config.ConfigManager;
import com.medselect.config.SimpleConfigValue;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Mike Gordon
 */
public class MailUtils {
  protected static final Logger LOGGER = Logger.getLogger(MailUtils.class.getName());
  public static final String EMAIL_FROM = "info@rheumatologypatienteducationtool.com";
  public static final String EMAIL_FROM_DISPLAY = "Rheumatology Patient Education Tool";

  //*** Stores the application code for this application for config/audit purposes.
  private String appCode;
  public MailUtils() {
    // no-op.
  }
  
  public MailUtils(String app) {
    appCode = app;
  }

  public String sendMail (String to, String toDisplay, String subject, String message) {
    String response = "SUCCESS";
    if (appCode != null) {
      ConfigManager cManager = new ConfigManager();
      SimpleConfigValue fromValue = cManager.getSimpleConfigValue(appCode, Constants.NO_REPLY_EMAIL);
      SimpleConfigValue displayValue =
          cManager.getSimpleConfigValue(appCode, Constants.NO_REPLY_DISPLAY);
      response = sendMail(
          fromValue.getConfigValue(),
          displayValue.getConfigValue(),
          to,
          toDisplay,
          subject,
          message);
    } else {
      response = "FAILURE";
    }
    
    return response;
  }
  public String sendMail(String from,
                         String fromDisplay,
                         String to,
                         String toDisplay,
                         String subject,
                         String message) {
    return sendMultiPartMail(from, fromDisplay, to, toDisplay, null, subject, message, message, null);
  }

  public String sendMultiPartMail(String from,
                         String fromDisplay,
                         String to,
                         String toDisplay,
                         String cc,
                         String subject,
                         String message,
                         String htmlMessage,
                         Collection<MimeBodyPart> attachments) {

    String response = "SUCCESS";
    LOGGER.info("Sending e-mail. To:" + to + ", Subject: " + subject);

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Multipart mp = new MimeMultipart();
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(from, fromDisplay));
      msg.addRecipient(Message.RecipientType.TO,
                       new InternetAddress(to, toDisplay));
      //*** If there is a cc: e-mail address, add it.
      if (cc != null && !cc.isEmpty()) {
        msg.addRecipient(Message.RecipientType.CC,
                         new InternetAddress(cc, cc));
      }
      msg.setSubject(subject);
      msg.setText(message);
      //*** Add the HTML part of the message, if it exists.
      if (htmlMessage != null) {
        MimeBodyPart htmlBody = new MimeBodyPart();
        htmlBody.setContent(htmlMessage, "text/html");
        mp.addBodyPart(htmlBody);
      }

      //*** Add the attachments, if they exist.
      if (attachments != null) {
        LOGGER.info("E-mail has " + attachments.size() + " attachments.");
        for (MimeBodyPart attachment: attachments) {
          mp.addBodyPart(attachment);
        }
      }
      msg.setContent(mp);
      Transport.send(msg);
      //*** Add the audit value.
      AuditLogManager aManager = new AuditLogManager();
      aManager.logAudit(
        Constants.COMMON_APP,
        Constants.SEND_USER_EMAIL,
        "E-mail successfully sent to " + to,
        "N/A",
        "N/A");
    } catch (AddressException ex) {
      response = "E-mail send failed: " + ex.toString();
      LOGGER.warning(response);
      response = "FAILURE";
    } catch (MessagingException ex) {
      response = "E-mail send failed: " + ex.toString();
      LOGGER.warning(response);
      response = "FAILURE";
    } catch (UnsupportedEncodingException ex) {
      response = "E-mail send failed: " + ex.toString();
      LOGGER.warning(response);
      response = "FAILURE";
    }

    return response;
  }
}
