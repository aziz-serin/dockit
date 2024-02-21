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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VmMemoryUsageAnalyzerTest {
    @Autowired
    private VmMemoryUsageAnalyzer vmMemoryUsageAnalyzer;

    private Audit alertAudit;
    private Audit noAlertAudit;

    @BeforeAll
    public void setup() {
        Agent agent = new Agent();
        Gson gson = new Gson();

        Map<String, ?> alertJson = Map.of(
                "total", 100,
                "available", 20
        );
        String alertData = gson.toJson(alertJson);
        alertAudit = EntityCreator.createAudit("vmId", AuditCategories.VM_CPU,
                LocalDateTime.now(), alertData, agent).get();

        Map<String, ?> noAlertJson = Map.of(
                "total", 100,
                "available", 90
        );
        String noAlertData = gson.toJson(noAlertJson);
        noAlertAudit = EntityCreator.createAudit("vmId", AuditCategories.VM_CPU,
                LocalDateTime.now(), noAlertData, agent).get();
    }

    @Test
    public void analyzeReturnsAlertGivenImportantCpuLoad() {
        List<Alert> alerts = vmMemoryUsageAnalyzer.analyze(alertAudit);

        assertThat(alerts.size()).isEqualTo(1);
        assertThat(alerts.get(0).getImportance()).isEqualTo(Alert.Importance.MEDIUM);
        assertThat(alerts.get(0).getMessage()).containsIgnoringCase("cpu");
    }

    @Test
    public void analyzeDoesNotReturnAlertGivenUnImportantCpuLoad() {
        List<Alert> alerts = vmMemoryUsageAnalyzer.analyze(noAlertAudit);

        assertThat(alerts.size()).isEqualTo(0);
    }
}
