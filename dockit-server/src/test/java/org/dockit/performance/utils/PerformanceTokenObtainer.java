package org.dockit.performance.utils;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class PerformanceTokenObtainer {
    private static final String JWT_AUTHENTICATE_ENDPOINT = "http://localhost:8080/api/authenticate/jwt";
    private static final String API_TOKEN_AUTHENTICATE_ENDPOINT = "http://localhost:8080/api/authenticate/apiKey";

    private static final Map<String, String> adminJsonMap = Map.of(
      "username", "admin",
      "password", "changeit"
    );

    private static final Map<String, String> agentJsonMap = Map.of(
            "agentName", "admin",
            "password", "changeit",
            "allowedUsers", "testuser",
            "agentUrl", "http://someagent.com"
    );

    public static String getAdminToken() {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        HttpRequest request = HttpRequest.newBuilder(URI.create(JWT_AUTHENTICATE_ENDPOINT))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(adminJsonMap)))
                .headers("accept", "application/json",
                        "Content-Type", "application/json")
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, String> responseMap = gson.fromJson(response.body(), Map.class);
            return responseMap.get("token");
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    public static Map<String, String> getAPIToken() {
        String adminToken = getAdminToken();
        Map<String, String> agentInformation = createAgent(adminToken);

        String agentId = agentInformation.get("id");
        String agentKey = agentInformation.get("key");

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        Map<String, String> keyMap = Map.of(
                "username", adminJsonMap.get("username"),
                "password", adminJsonMap.get("password"),
                "agentId", agentId
        );

        HttpRequest request = HttpRequest.newBuilder(URI.create(API_TOKEN_AUTHENTICATE_ENDPOINT))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(keyMap)))
                .headers("accept", "application/json",
                        "Content-Type", "application/json",
                        "Authorization", adminToken)
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, String> returnMap = gson.fromJson(response.body(), Map.class);
            returnMap.put("agentKey", agentKey);
            returnMap.put("agentId", agentId);
            return returnMap;
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    private static Map<String, String> createAgent(String adminToken) {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/api/agent"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(agentJsonMap)))
                .headers("accept", "application/json",
                        "Content-Type", "application/json",
                        "Authorization", adminToken)
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), Map.class);
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }
}
