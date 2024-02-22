package org.dockit.dockitserver.events;

import com.nimbusds.jose.shaded.gson.Gson;
import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.analyze.AuditCategories;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.security.encryption.AESCBCEncryptor;
import org.dockit.dockitserver.security.key.KeyConstants;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.dockit.dockitserver.services.templates.AgentService;
import org.dockit.dockitserver.services.templates.AlertService;
import org.dockit.dockitserver.services.templates.AuditService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
public class AuditCreatedEventTest {

    @Autowired
    private AuditService auditService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private KeyStoreHandler keyStoreHandler;

    private Agent agent;
    private Audit alertAudit;
    private Audit noAlertAudit;
    private Audit invalidCategortyAudit;

    @BeforeAll
    public void setup() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Map<String, ?> alertJson = Map.of(
                "cpu_load", 85
        );
        Gson gson = new Gson();
        String auditAlertData = gson.toJson(alertJson);

        Map<String, ?> noAlertJson = Map.of(
                "cpu_load", 15
        );
        String auditNoAlertData = gson.toJson(noAlertJson);

        SecretKey secretKey = (SecretKey) keyStoreHandler.getKey(KeyConstants.DB_KEY_ALIAS, "".toCharArray()).get();
        String encryptedAuditAlert = AESCBCEncryptor.encrypt(auditAlertData, secretKey);
        String encryptedNoAlertAudit = AESCBCEncryptor.encrypt(auditNoAlertData, secretKey);

        List<String> allowedUsers = new ArrayList<>();
        allowedUsers.add("allowed_user");

        agent = EntityCreator.createAgent("agent", "password",
                LocalDateTime.now(), LocalDateTime.now(), allowedUsers).get();
        agentService.save(agent);

        alertAudit = EntityCreator.createAudit("vmId", AuditCategories.VM_CPU,
                LocalDateTime.now(), encryptedAuditAlert, agent).get();
        noAlertAudit = EntityCreator.createAudit("vmId", AuditCategories.VM_CPU,
                LocalDateTime.now(), encryptedNoAlertAudit, agent).get();
        invalidCategortyAudit = EntityCreator.createAudit("vmId", "some_category",
                LocalDateTime.now(), encryptedNoAlertAudit, agent).get();
    }

    @Test
    public void listenerGeneratesAlertIfItShould() {
        auditService.save(alertAudit);

        List<Alert> alert = alertService.findByAgent(agent);

        assertThat(alert).hasSize(1);

        // Delete to clean up for next tests
        alertService.deleteById(alert.get(0).getId());
    }

    @Test
    public void listenerDoesNotGenerateAlertGivenInsufficientUsage() {
        auditService.save(noAlertAudit);

        List<Alert> alert = alertService.findByAgent(agent);

        assertThat(alert).hasSize(0);
    }

    @Test
    public void listenerDoesNotGenerateAlertGivenUnknownCategory() {
        auditService.save(invalidCategortyAudit);

        List<Alert> alert = alertService.findByAgent(agent);

        assertThat(alert).hasSize(0);

        // restore this audit for other tests
        alertAudit.setCategory(AuditCategories.VM_CPU);
    }

}
