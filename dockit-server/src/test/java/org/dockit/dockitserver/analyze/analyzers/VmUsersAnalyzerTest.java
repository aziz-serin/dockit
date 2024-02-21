package org.dockit.dockitserver.analyze.analyzers;

import com.nimbusds.jose.shaded.gson.Gson;
import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.analyze.AuditCategories;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VmUsersAnalyzerTest {
    private static String ALLOWED_USER = "allowed_user";

    @Autowired
    private VmUsersAnalyzer vmUsersAnalyzer;

    private Audit alertAudit;
    private Audit noAlertAudit;

    @BeforeAll
    public void setup() {
        Agent agent = new Agent();
        agent.setAllowedUsers(List.of(ALLOWED_USER));
        Gson gson = new Gson();

        Map<String, ?> alertJson = Map.of(
                "username", "unallowed_user",
                "host", "some_host",
                // expects in epoch milliseconds
                "login_time", Instant.now().toEpochMilli()
        );
        String alertData = gson.toJson(alertJson);
        alertAudit = EntityCreator.createAudit("vmId", AuditCategories.VM_USERS,
                LocalDateTime.now(), alertData, agent).get();

        Map<String, ?> noAlertJson = Map.of(
                "username", ALLOWED_USER,
                "host", "some_host",
                // expects in epoch milliseconds
                "login_time", Instant.now().toEpochMilli()
        );
        String noAlertData = gson.toJson(noAlertJson);
        noAlertAudit = EntityCreator.createAudit("vmId", AuditCategories.VM_USERS,
                LocalDateTime.now(), noAlertData, agent).get();
    }

    @Test
    public void analyzeReturnsAlertGivenImportantCpuLoad() {
        List<Alert> alerts = vmUsersAnalyzer.analyze(alertAudit);

        assertThat(alerts.size()).isEqualTo(1);
        assertThat(alerts.get(0).getImportance()).isEqualTo(Alert.Importance.CRITICAL);
        assertThat(alerts.get(0).getMessage()).containsIgnoringCase("intrusion");
    }

    @Test
    public void analyzeDoesNotReturnAlertGivenUnImportantCpuLoad() {
        List<Alert> alerts = vmUsersAnalyzer.analyze(noAlertAudit);

        assertThat(alerts.size()).isEqualTo(0);
    }
}
