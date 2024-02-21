package org.dockit.dockitserver.services;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.services.templates.AgentService;
import org.dockit.dockitserver.services.templates.AlertService;
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

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlertServiceTest {
    private static final String VM_ID = "vmId";

    @Autowired
    private AgentService agentService;

    @Autowired
    private AlertService alertService;

    private Agent agent;

    private Alert alert1;

    @BeforeAll
    public void setup() {
        agent = EntityCreator.createAgent("agent", "password",
                LocalDateTime.now(), LocalDateTime.now()).get();
        agentService.save(agent);

        alert1 = EntityCreator.createAlert(VM_ID, agent, Alert.Importance.CRITICAL, LocalDateTime.now(),
                "message").get();
        alertService.save(alert1);

        Alert alert2 = EntityCreator.createAlert(VM_ID, agent, Alert.Importance.MEDIUM, LocalDateTime.now(),
                "message").get();
        alertService.save(alert2);

        Alert alert3 = EntityCreator.createAlert("differentVmId", agent, Alert.Importance.LOW,
                LocalDateTime.now(), "message").get();
        alertService.save(alert3);
    }

    @Test
    public void findByAgentReturnsAlertsWithSameAgent() {
        List<Alert> alerts = alertService.findByAgent(agent);
        alerts.forEach(
                alert -> {assertThat(alert.getAgent().getId()).isEqualTo(agent.getId());}
        );
    }

    @Test
    public void findByVmIdReturnsAlertsWithSameVmId() {
        List<Alert> alerts = alertService.findByVmId(VM_ID);
        alerts.forEach(
                alert -> {assertThat(alert.getVmId()).isEqualTo(VM_ID);}
        );
    }

    @Test
    public void findMostRecentWithVmId() {
        List<Alert> alerts = alertService.findMostRecentWithVmId(VM_ID, 1);

        assertThat(alerts.size()).isEqualTo(1);
        assertThat(alerts.get(0).getVmId()).isEqualTo(VM_ID);
        assertThat(alerts.get(0).getAuditTimeStamp()).isAfter(alert1.getAuditTimeStamp());
    }

    @Test
    public void findByImportanceReturnsSameImportance() {
        List<Alert> alerts = alertService.findByImportance(Alert.Importance.CRITICAL);
        alerts.forEach(
                alert -> {assertThat(alert.getImportance()).isEqualTo(Alert.Importance.CRITICAL);}
        );
    }

    @Test
    public void findByImportanceWithSameVmReturnsSameImportanceAndVm() {
        List<Alert> alerts = alertService.findByImportanceWithSameVmId(Alert.Importance.CRITICAL, VM_ID);
        alerts.forEach(
                alert -> {
                    assertThat(alert.getImportance()).isEqualTo(Alert.Importance.CRITICAL);
                    assertThat(alert.getVmId()).isEqualTo(VM_ID);
                }
        );
    }
}
