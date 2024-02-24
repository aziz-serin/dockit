package org.dockit.dockitserver.mail;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GmailSenderTest {
    private static final String USER = "emailUser";
    private static final String PASSWORD = "password";
    private static final String EMAIL_MESSAGE = "This is the message to be sent as an email";
    private static final String VM_ID = "vmId";
    private static final String TO = "to@mail.com";


    @Autowired
    private GmailEmailService gmailEmailService;

    private Alert alert;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser(USER, PASSWORD))
            .withPerMethodLifecycle(false);

    @BeforeAll
    public void setup() {
        Agent agent = new Agent();
        alert = EntityCreator.createAlert(VM_ID, agent, Alert.Importance.MEDIUM,
                LocalDateTime.now(), EMAIL_MESSAGE).get();

    }

    @Test
    public void mailSenderSendsMailGivenAlertShouldBeSent() throws MessagingException {
        gmailEmailService.sendEmail(alert, TO, Alert.Importance.LOW);
        Date alertDate = Date.from(alert.getAuditTimeStamp().atZone(ZoneId.systemDefault()).toInstant());

        assertThat(greenMail.getReceivedMessages()).hasSize(1);

        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];

        assertThat(receivedMessage.getFrom()).hasSize(1);
        assertThat(receivedMessage.getFrom()[0].toString()).isEqualTo(MailConstants.FROM);
        assertThat(receivedMessage.getSubject()).isEqualTo(MailConstants.ALERT_SUBJECT.formatted(VM_ID));
        assertThat(receivedMessage.getAllRecipients()).hasSize(1);
        assertThat(receivedMessage.getAllRecipients()[0].toString()).isEqualTo(TO);
        assertThat(receivedMessage.getSentDate())
                .isCloseTo(alertDate, 1000);
        assertThat(EMAIL_MESSAGE).isEqualTo(GreenMailUtil.getBody(receivedMessage));
    }

    @Test
    public void mailSendDoesNotSendMailGivenAlertShouldNotBeSent() {
        gmailEmailService.sendEmail(alert, TO, Alert.Importance.CRITICAL);

        assertThat(greenMail.getReceivedMessages()).hasSize(0);
    }
}
