package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.services.templates.AdminService;
import org.dockit.dockitserver.services.templates.AgentService;
import org.dockit.dockitserver.services.templates.AlertService;
import org.dockit.dockitserver.testUtils.TokenObtain;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlertControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    AgentService agentService;
    @Autowired
    AdminService adminService;
    @Autowired
    AlertService alertService;

    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "password";
    static final String VIEWER_ADMIN_USERNAME = "viewer";
    static final String AGENT_NAME = "agent";
    static final String AGENT_PASSWORD = "password";
    static final String VM_ID = "vmId";
    static final Alert.Importance IMPORTANCE = Alert.Importance.CRITICAL;

    WebTestClient client;
    Agent agent;
    Alert alert;

    @BeforeAll
    public void setup() {
        Admin admin = EntityCreator.createAdmin(ADMIN_USERNAME, ADMIN_PASSWORD, Admin.Role.SUPER).get();
        adminService.save(admin);

        Admin viewer = EntityCreator.createAdmin(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, Admin.Role.VIEWER).get();
        adminService.save(viewer);

        agent = EntityCreator.createAgent(AGENT_NAME, AGENT_PASSWORD, LocalDateTime.now(), LocalDateTime.now(),
                List.of("")).get();
        agentService.save(agent);

        generateAlert();

        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void getWithSameVmIdReturnsAlerts() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/alert/vmId?vmId=" + VM_ID)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res -> {
                    List<Alert> alerts = res.getResponseBody();
                    assertThat(alerts).hasSize(1);
                });
    }

    @Test
    public void getWithSameAgentReturnsNotFoundGivenAgentNotFound() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/alert/agentId?id=" + UUID.randomUUID().toString())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void getWithSameAgentReturnsBadRequestGivenInvalidId() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/alert/agentId?id=" + "someId")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void getWithSameAgentSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/alert/agentId?id=" + agent.getId().toString())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res -> {
                    List<Alert> alerts = res.getResponseBody();
                    assertThat(alerts).hasSize(1);
                });
    }

    @Test
    public void getWithSameImportanceFailsGivenInvalidImportance() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/alert/importance?importance=" + "importance")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void getWithSameImportanceSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/alert/importance?importance=" + IMPORTANCE)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res -> {
                    List<Alert> alerts = res.getResponseBody();
                    assertThat(alerts).hasSize(1);
                });
    }

    @Test
    public void getWithSameImportanceWithSameVmIdFailsGivenInvalidImportance() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/alert/importanceVmId?importance=" + "importance" + "&vmId=" + VM_ID)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void getWithSameImportanceWithSameVmIdSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/alert/importanceVmId?importance=" + IMPORTANCE + "&vmId=" + VM_ID)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res -> {
                    List<Alert> alerts = res.getResponseBody();
                    assertThat(alerts).hasSize(1);
                });
    }

    @Test
    public void getMostRecentWithVmFailsGivenInvalidCount() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/alert/recentVmId?count=" + 0 + "&vmId=" + VM_ID)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void getMostRecentWithVmSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/alert/recentVmId?count=" + 5 + "&vmId=" + VM_ID)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res -> {
                    List<Alert> alerts = res.getResponseBody();
                    assertThat(alerts).hasSize(1);
                });
    }

    @Test
    public void deleteFailsGivenInsufficientPermission() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.delete().uri("/api/alert?id=" + alert.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    public void deleteFailsGivenIdCannotBeParsed() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.delete().uri("/api/alert?id=" + "someId")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void deleteSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.delete().uri("/api/alert?id=" + alert.getId().toString())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus()
                .isOk();

        // Generate it again to be used by other tests
        generateAlert();
    }

    private void generateAlert() {
        alert = EntityCreator.createAlert(VM_ID, agent,
                IMPORTANCE, LocalDateTime.now(), "message").get();
        alertService.save(alert);
    }
}
