package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.security.encryption.AESGCMEncryptor;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WriteControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    AgentService agentService;
    @Autowired
    AdminService adminService;
    @Autowired
    KeyStoreHandler keyStoreHandler;

    static final String AGENT_NAME = "agent";
    static final String AGENT_PASSWORD = "password";
    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "password";
    static final String DATA = "some audit data";
    static final String VM_ID = "some id for vm";
    static final String CATEGORY = "audit category";
    static final LocalDateTime TIME_STAMP = LocalDateTime.now();

    String encryptedData;

    WebTestClient client;
    UUID agentId;

    String apiToken;

    @BeforeAll
    public void setup() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Admin admin = EntityCreator.createAdmin(ADMIN_USERNAME, ADMIN_PASSWORD, Admin.Role.SUPER).get();
        adminService.save(admin);

        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        createAgent();

        Agent agent = agentService.findById(agentId).get();

        Key key = keyStoreHandler.getKey(agentId.toString(), agent.getPassword().toCharArray()).get();
        encryptedData = AESGCMEncryptor.encrypt(DATA, agentId.toString(), (SecretKey) key);

        apiToken = TokenObtain.getAPIToken(ADMIN_USERNAME, ADMIN_PASSWORD, agentId, client);
    }

    @Test
    public void writeControllerFailsGivenInvalidBody() {
        Map<String, Object> json = Map.of(
                "vmId", VM_ID,
                "categor", CATEGORY,
                "timeStamp", TIME_STAMP.toString(),
                "data", encryptedData
        );

        client.post().uri("/api/write?id=" + agentId.toString())
                .header("X-API-KEY", apiToken)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void writeControllerFailsGivenInvalidTimeStamp() {
        Map<String, Object> json = Map.of(
                "vmId", VM_ID,
                "category", CATEGORY,
                "timeStamp", "randomm",
                "data", encryptedData
        );

        client.post().uri("/api/write?id=" + agentId.toString())
                .header("X-API-KEY", apiToken)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void writeControllerFailsGivenInvalidAgent() {
        Map<String, Object> json = Map.of(
                "vmId", VM_ID,
                "category", CATEGORY,
                "timeStamp", TIME_STAMP.toString(),
                "data", encryptedData
        );

        client.post().uri("/api/write?id=" + UUID.randomUUID())
                .header("X-API-KEY", apiToken)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void writeControllerFailsGivenUnencryptedData() {
        Map<String, Object> json = Map.of(
                "vmId", VM_ID,
                "category", CATEGORY,
                "timeStamp", TIME_STAMP.toString(),
                "data", DATA
        );

        client.post().uri("/api/write?id=" + agentId)
                .header("X-API-KEY", apiToken)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    public void writeControllerSucceeds() {
        Map<String, Object> json = Map.of(
                "vmId", VM_ID,
                "category", CATEGORY,
                "timeStamp", TIME_STAMP.toString(),
                "data", encryptedData
        );

        client.post().uri("/api/write?id=" + agentId)
                .header("X-API-KEY", apiToken)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .exchange()
                .expectStatus().isOk();
    }

    private void createAgent() {
        // Create agent this way to have the generated key for data encryption/decryption
        String jwt = TokenObtain.getJwt(ADMIN_USERNAME, ADMIN_PASSWORD, client);
        Map<String, Object> json = Map.of(
                "agentName", AGENT_NAME,
                "password", AGENT_PASSWORD
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
