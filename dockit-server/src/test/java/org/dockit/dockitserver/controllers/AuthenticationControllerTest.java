package org.dockit.dockitserver.controllers;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTParser;
import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.APIKey;
import org.dockit.dockitserver.entities.Admin;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.dockit.dockitserver.security.jwt.JWTGenerator;
import org.dockit.dockitserver.security.jwt.JWTValidator;
import org.dockit.dockitserver.services.templates.APIKeyService;
import org.dockit.dockitserver.services.templates.AdminService;
import org.dockit.dockitserver.services.templates.AgentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    AdminService adminService;
    @Autowired
    AgentService agentService;
    @Autowired
    APIKeyService apiKeyService;
    @Autowired
    JWTValidator jwtValidator;
    @Autowired
    JWTGenerator jwtGenerator;

    static final String USERNAME = "username";
    static final String PASSWORD = "password";
    Agent agent;

    private WebTestClient client;

    @BeforeAll
    public void setup() {
        Admin admin = EntityCreator.createAdmin(USERNAME, PASSWORD, Admin.Role.SUPER).get();
        adminService.save(admin);

        agent = EntityCreator.createAgent("agent1", "password1",
                LocalDateTime.now(), LocalDateTime.now(), List.of("")).get();
        agentService.save(agent);

        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void issueApiKeyReturnsInvalidRequestGivenInvalidBody() {
        Map<String, ?> json = Map.of(
                "userame", USERNAME,
                "password", PASSWORD,
                "agentId", agent.getId()
        );

        client.post().uri("/api/authenticate/apiKey")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void issueApiKeyReturnsEmptyGivenInvalidUser() {
        Map<String, ?> json = Map.of(
                "username", "wrong_user",
                "password", PASSWORD,
                "agentId", agent.getId()
        );

        client.post().uri("/api/authenticate/apiKey")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void issueApiKeyReturnsEmptyGivenInvalidAgent() {
        Map<String, ?> json = Map.of(
                "username", USERNAME,
                "password", PASSWORD,
                "agentId", 999
        );

        client.post().uri("/api/authenticate/apiKey")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void issueApiKeyReturnsApiKey() {
        Map<String, ?> json = Map.of(
                "username", USERNAME,
                "password", PASSWORD,
                "agentId", agent.getId()
        );

        client.post().uri("/api/authenticate/apiKey")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(response -> {
                            String apiKey = (String) Objects.requireNonNull(response.getResponseBody()).get("key");
                            PasswordEncoder encoder = new BCryptPasswordEncoder();
                            assertTrue(
                                    apiKeyService.findAll().stream()
                                            .anyMatch(key -> encoder.matches(apiKey, key.getToken()))
                            );
                });
    }

    @Test
    public void issueJwtReturnsInvalidRequestGivenInvalidBody() {
        Map<String, ?> json = Map.of(
                "userame", USERNAME,
                "password", PASSWORD
        );

        client.post().uri("/api/authenticate/jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void issueJwtReturnsInvalidRequestGivenInvalidUser() {
        Map<String, ?> json = Map.of(
                "username", "wrong_user",
                "password", PASSWORD
        );

        client.post().uri("/api/authenticate/jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void issueJwtReturnsJwt() {
        Map<String, ?> json = Map.of(
                "username", USERNAME,
                "password", PASSWORD
        );

        client.post().uri("/api/authenticate/jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(res -> {
                    try {
                        assertTrue(jwtValidator.validateJwtToken((String) Objects.requireNonNull(res.getResponseBody()).get("token")));
                    } catch (ParseException e) {
                        //fail
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void introspectJwtReturnsInvalidRequestGivenInvalidBody() throws JOSEException {
        String token = jwtGenerator.generateToken(USERNAME);
        Map<String, ?> json = Map.of(
                "jw", token
        );

        client.post().uri("/api/authenticate/jwt/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
        ;
    }

    @Test
    public void introspectJwtReturnsJwtDetails() throws JOSEException, ParseException {
        String token = jwtGenerator.generateToken(USERNAME);
        Map<String, ?> json = Map.of(
                "jwt", token
        );
        Map<String, Object> obj = JWTParser.parse(token).getJWTClaimsSet().toJSONObject();

        client.post().uri("/api/authenticate/jwt/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(res ->
                {
                    Map<String, Object> map = res.getResponseBody();
                    assertThat(obj).containsEntry("sub", map.get("sub"));
                    assertThat(obj).containsEntry("iss", map.get("iss"));
                    assertThat(obj.get("exp")).isEqualTo(Long.valueOf(String.valueOf(map.get("exp"))));
                });
    }
}