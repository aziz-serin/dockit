package org.dockit.dockitserver.testUtils;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Map;
import java.util.UUID;

public class TokenObtain {

    private static String jwtToken;
    private static String apiKey;

    public static String getJwt(String username, String password, WebTestClient client) {
        Map<String, ?> json = Map.of(
                "username", username,
                "password", password
        );

        client.post().uri("/api/authenticate/jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(res -> jwtToken = (String) res.getResponseBody().get("token"));
        return jwtToken;
    }

    public static String getAPIToken(String userName, String password, UUID agentId, WebTestClient client) {
        Map<String, ?> json = Map.of(
                "username", userName,
                "password", password,
                "agentId", agentId
        );

        client.post().uri("/api/authenticate/apiKey")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(json))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(res -> apiKey = (String) res.getResponseBody().get("key"));
        return apiKey;
    }
}
