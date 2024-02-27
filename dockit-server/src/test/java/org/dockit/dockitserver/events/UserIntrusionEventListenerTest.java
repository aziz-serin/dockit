package org.dockit.dockitserver.events;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.analyze.AuditCategories;
import org.dockit.dockitserver.config.ConfigContainer;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.exceptions.security.key.KeyStoreException;
import org.dockit.dockitserver.mail.MailConstants;
import org.dockit.dockitserver.security.encryption.AESCBCEncryptor;
import org.dockit.dockitserver.security.key.KeyConstants;
import org.dockit.dockitserver.security.key.KeyHandler;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.dockit.dockitserver.services.templates.AgentService;
import org.dockit.dockitserver.services.templates.AuditService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserIntrusionEventListenerTest {
    static final String DUMMY_URL_STRING = "http://someurl.com";
    static final String AGENT_URL = "http://localhost:4567";

    @Autowired
    private AuditService auditService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private KeyStoreHandler keyStoreHandler;

    @Autowired
    private KeyHandler keyHandler;

    @Autowired
    private ConfigContainer configContainer;

    private Agent agent;
    private Audit alertAudit;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("emailUser", "password"))
            .withPerMethodLifecycle(true);

    @BeforeAll
    public void setup() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, MalformedURLException, KeyStoreException {
        Map<String, ?> alertJson = Map.of(
                "username", "unallowed_user",
                "host", "some_host",
                // expects in epoch milliseconds
                "login_time", Instant.now().toEpochMilli()
        );
        Gson gson = new Gson();
        String auditAlertData = gson.toJson(alertJson);


        SecretKey secretKey = (SecretKey) keyStoreHandler.getKey(KeyConstants.DB_KEY_ALIAS, "".toCharArray()).get();
        String encryptedAuditAlert = AESCBCEncryptor.encrypt(auditAlertData, secretKey);

        List<String> allowedUsers = new ArrayList<>();
        allowedUsers.add("allowed_user");

        URL url = new URL(DUMMY_URL_STRING);
        agent = EntityCreator.createAgent("agent", "password",
                LocalDateTime.now(), LocalDateTime.now(), allowedUsers, url).get();
        agentService.save(agent);

        keyHandler.generateKeyForAgentAndSave(agent.getId().toString(), agent.getPassword());

        alertAudit = EntityCreator.createAudit("vmId", AuditCategories.VM_USERS,
                LocalDateTime.now(), encryptedAuditAlert, agent).get();
    }

    @Test
    public void userIntrusionAlertSentWhenAgentServerIsNotAlive() throws MessagingException, IOException {
        auditService.save(alertAudit);

        assertThat(greenMail.getReceivedMessages()).hasSize(2);

        MimeMessage intrusionMail = greenMail.getReceivedMessages()[0];
        MimeMessage alertMail = greenMail.getReceivedMessages()[1];

        assertThat(alertMail.getFrom()).hasSize(1);
        assertThat(alertMail.getFrom()[0].toString()).isEqualTo(MailConstants.FROM);

        assertThat(intrusionMail.getFrom()).hasSize(1);
        assertThat(intrusionMail.getFrom()[0].toString()).isEqualTo(MailConstants.FROM);
        assertThat(intrusionMail.getSubject()).containsIgnoringCase(agent.getId().toString());
        assertThat((String) intrusionMail.getContent()).containsIgnoringCase("agent is not responding");
        assertThat(intrusionMail.getAllRecipients()).hasSize(1);
        assertThat(intrusionMail.getAllRecipients()[0].toString())
                .isEqualTo(configContainer.getConfig().getSendingEmailAddress());
        // Delete to cleanup
        auditService.deleteById(alertAudit.getId());
    }

    @Test
    @Disabled("Enable if there is an agent running!")
    public void userIntrusionAlertSentWhenServerIsAlive() throws MessagingException, IOException {
        agentService.updateAgentUrl(agent.getId(), new URL(AGENT_URL));
        auditService.save(alertAudit);

        assertThat(greenMail.getReceivedMessages()).hasSize(2);

        MimeMessage intrusionMail = greenMail.getReceivedMessages()[0];
        MimeMessage alertMail = greenMail.getReceivedMessages()[1];

        assertThat(alertMail.getFrom()).hasSize(1);
        assertThat(alertMail.getFrom()[0].toString()).isEqualTo(MailConstants.FROM);

        assertThat(intrusionMail.getFrom()).hasSize(1);
        assertThat(intrusionMail.getFrom()[0].toString()).isEqualTo(MailConstants.FROM);
        assertThat(intrusionMail.getSubject()).containsIgnoringCase(agent.getId().toString());
        assertThat((String) intrusionMail.getContent())
                .containsIgnoringCase("could not kill all the processes");
        assertThat(intrusionMail.getAllRecipients()).hasSize(1);
        assertThat(intrusionMail.getAllRecipients()[0].toString())
                .isEqualTo(configContainer.getConfig().getSendingEmailAddress());
    }
}
