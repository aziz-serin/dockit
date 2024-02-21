package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.services.templates.AdminService;
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
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class APIKeyControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    AdminService adminService;

    static final String AGENT_NAME = "agent";
    static final String AGENT_PASSWORD = "password";
    static final String ADMIN_USERNAME = "admin";
    static final String VIEWER_ADMIN_USERNAME = "viewer";
    static final String ADMIN_PASSWORD = "password";

    WebTestClient client;
    UUID agentId;

    @BeforeAll
    public void setup() {
        Admin admin = EntityCreator.createAdmin(ADMIN_USERNAME, ADMIN_PASSWORD, Admin.Role.SUPER).get();
        adminService.save(admin);

        Admin viewer = EntityCreator.createAdmin(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, Admin.Role.SUPER).get();
        adminService.save(viewer);
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        createAgent();
        TokenObtain.getAPIToken(ADMIN_USERNAME, ADMIN_PASSWORD, agentId, client);
    }

    @Test
    public void getReturns404IfAgentDoesNotExist() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/apikey?agentId=" + UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void getReturnsOk() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/apikey?agentId=" + agentId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void revokeReturnsOk() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.delete().uri("/api/apikey?agentId=" + agentId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void revokeReturnsNotFound() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.delete().uri("/api/apikey?agentId=" + UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void revokeFailsGivenInsufficientPermission() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.delete().uri("/api/apikey?agentId=" + UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", jwt)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    private void createAgent() {
        // Create agent this way to have the generated key for data encryption/decryption
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);
        Map<String, Object> json = Map.of(
                "agentName", AGENT_NAME,
                "password", AGENT_PASSWORD,
                "allowedUsers", "user"
        );

        client.post().uri("/api/agent")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(res -> {
                    Map<String, Object> body = res.getResponseBody();
                    agentId = UUID.fromString((String) Objects.requireNonNull(body).get("id"));
                });
    }
}
