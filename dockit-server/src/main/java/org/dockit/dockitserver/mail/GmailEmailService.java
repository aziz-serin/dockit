package org.dockit.dockitserver.mail;

import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.utils.AlertImportanceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;

/**
 * {@link EmailService} implementation for sending mails using Gmail
 */
@Component
public class GmailEmailService implements EmailService {
    private final JavaMailSender mailSender;

    /**
     * @param mailSender {@link JavaMailSender} instance to be injected
     */
    @Autowired
    public GmailEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Method to send alert emails
     *
     * @param alert {@link Alert} to be sent as an email
     * @param to Address to send the mail to
     * @param defaultImportance {@link Alert.Importance} to check if the supplied alert should be sent or not
     */
    @Override
    public void sendEmail(Alert alert, String to, Alert.Importance defaultImportance) {
        if (shouldSend(alert.getImportance(), defaultImportance)) {
            SimpleMailMessage mail = new SimpleMailMessage();
            String formattedSubject = MailConstants.ALERT_SUBJECT.formatted(alert.getVmId());
            Date sentDate = Date.from(alert.getAuditTimeStamp().atZone(ZoneId.systemDefault()).toInstant());

            mail.setTo(to);
            mail.setSubject(formattedSubject);
            mail.setText(alert.getMessage());
            mail.setFrom(MailConstants.FROM);
            mail.setSentDate(sentDate);

            mailSender.send(mail);
        }

    }

    private boolean shouldSend(Alert.Importance alertImportance, Alert.Importance defaultImportance) {
        return AlertImportanceUtils.getImportanceValue(alertImportance) >=
                AlertImportanceUtils.getImportanceValue(defaultImportance);
    }
}
