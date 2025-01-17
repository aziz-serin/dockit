package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.authentication.issuer.APIKeyIssuer;
import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.dockit.dockitserver.services.templates.APIKeyService;
import org.dockit.dockitserver.services.templates.AdminService;
import org.dockit.dockitserver.services.templates.AgentService;
import org.dockit.dockitserver.testUtils.TokenObtain;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AgentControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    AgentService agentService;
    @Autowired
    AdminService adminService;
    @Autowired
    APIKeyService apiKeyService;
    @Autowired
    KeyStoreHandler keyStoreHandler;
    @Autowired
    APIKeyIssuer apiKeyIssuer;

    static final String AGENT_NAME = "agent";
    static final String AGENT_PASSWORD = "password";
    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "password";
    static final String VIEWER_ADMIN_USERNAME = "viewer";
    static final String DUMMY_URL_STRING = "http://someurl.com";

    WebTestClient client;
    Agent agent;

    @BeforeAll
    public void setup() throws MalformedURLException {
        URL url = new URL(DUMMY_URL_STRING);
        agent = EntityCreator.createAgent(AGENT_NAME, AGENT_PASSWORD, LocalDateTime.now(), LocalDateTime.now(),
                List.of(""), url).get();
        agentService.save(agent);

        Admin admin = EntityCreator.createAdmin(ADMIN_USERNAME, ADMIN_PASSWORD, Admin.Role.SUPER).get();
        adminService.save(admin);

        Admin viewerAdmin = EntityCreator.createAdmin(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, Admin.Role.VIEWER).get();
        adminService.save(viewerAdmin);

        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void getAgentReturnsAgent() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/agent?id=" + agent.getId())
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(res ->
                        assertThat(res.getResponseBody().get("agentName")).isEqualTo(agent.getAgentName()));
    }

    @Test
    public void getAllAgentsReturnsAgents() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/agent/all")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res ->
                        assertThat(res.getResponseBody().size()).isEqualTo(agentService.findAll().size()));
    }

    @Test
    public void getAllAgentsByCreationDateReturnsAgents() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/agent/creationDate?isAscending=true")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res ->
                        assertThat(res.getResponseBody().size()).isEqualTo(agentService.findAll().size()));
    }

    @Test
    public void getAllAgentsLastActiveInGivenMinutesReturnsAgents() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.get().uri("/api/agent/active?time=" + 0)
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .consumeWith(res ->
                        assertThat(res.getResponseBody().size()).isEqualTo(0));
    }

    @Test
    public void createAgentFailsGivenInsufficientPermissions() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.post().uri("/api/agent")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void createAgentFailsGivenInvalidBody() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);
        Map<String, Object> json = Map.of(
                "agentNme", AGENT_NAME,
                "password", AGENT_PASSWORD
        );

        client.post().uri("/api/agent")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void createAgentCreatesAgent() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);
        Map<String, Object> json = Map.of(
                "agentName", AGENT_NAME,
                "password", AGENT_PASSWORD,
                "allowedUsers", "user",
                "agentUrl", DUMMY_URL_STRING
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
                    UUID id = UUID.fromString((String) Objects.requireNonNull(body).get("id"));
                    assertTrue(keyStoreHandler.keyExists(id.toString()));
                });
    }

    @Test
    public void deleteFailsGivenInsufficientPermissions() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.delete().uri("/api/agent?id=999")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatusCode.valueOf(401));
    }

    @Test
    public void deleteSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);

        // Create an agent with post request to generate the API key for it
        Map<String, Object> json = Map.of(
                "agentName", AGENT_NAME,
                "password", AGENT_PASSWORD,
                "allowedUsers", "user",
                "agentUrl", "http://someurl.com"
        );
        AtomicReference<UUID> id = new AtomicReference<>(); 

        client.post().uri("/api/agent")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(res -> {
                            Map<String, Object> body = res.getResponseBody();
                            id.set(UUID.fromString((String) Objects.requireNonNull(body).get("id")));
                        });

        // Create api key to observe it getting deleted
        String apiKey = apiKeyIssuer.issueKey(ADMIN_USERNAME, ADMIN_PASSWORD, id.get()).get();

        client.delete().uri("/api/agent?id=" + id.get())
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        // Check that apiKey and secretKey is deleted
        assertThat(apiKeyService.findAll()).hasSize(0);
        assertFalse(keyStoreHandler.keyExists(String.valueOf(id.get())));
    }

    @Test
    public void updateAgentNameFailsGivenInsufficientPermission() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.put().uri("/api/agent/name?id=999")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateAgentNameFailsGivenInvalidBody() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);
        Map<String, Object> json = Map.of(
                "agentNme", AGENT_NAME
        );

        client.put().uri("/api/agent/name?id=999")
                .header("Authorization", jwt)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateAgentNameSucceeds() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);
        Map<String, Object> json = Map.of(
                "agentName", AGENT_NAME
        );

        client.put().uri("/api/agent/name?id=" + agent.getId())
                .header("Authorization", jwt)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void updateAllowedUsersFailsGivenInsufficientPermission() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.put().uri("/api/agent/allowedUsers?id=999")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateAllowedUsersFailsGivenInvalidBody() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);
        Map<String, Object> json = Map.of(
                "allowedUsrs", "newUser"
        );

        client.put().uri("/api/agent/allowedUsers?id=999")
                .header("Authorization", jwt)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateAllowedUsersSucceed() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);
        Map<String, Object> json = Map.of(
                "allowedUsers", "newUser"
        );

        client.put().uri("/api/agent/allowedUsers?id=" + agent.getId())
                .header("Authorization", jwt)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void updateAgentUrlFailsGivenInsufficientPermission() {
        String jwt = TokenObtain.getJwt(VIEWER_ADMIN_USERNAME, ADMIN_PASSWORD, client);

        client.put().uri("/api/agent/agentUrl?id=999")
                .header("Authorization", jwt)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateAgentUrlFailsGivenInvalidBody() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);
        Map<String, Object> json = Map.of(
                "agenUrl", "http://newurl.com"
        );

        client.put().uri("/api/agent/agentUrl?id=999")
                .header("Authorization", jwt)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateAgentUrlFailsGivenInvalidURLString() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);
        Map<String, Object> json = Map.of(
                "agentUrl", "htt://newurl.com"
        );

        client.put().uri("/api/agent/agentUrl?id=" + agent.getId())
                .header("Authorization", jwt)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void updateAgentUrlSucceed() {
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);
        Map<String, Object> json = Map.of(
                "agentUrl", "http://newurl.com"
        );

        client.put().uri("/api/agent/agentUrl?id=" + agent.getId())
                .header("Authorization", jwt)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
}
