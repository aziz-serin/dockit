package org.dockit.performance.utils;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class PerformanceTokenObtainer {
    private static final String AUTHENTICATE_ENDPOINT = "http://localhost:8080/api/authenticate/jwt";
    private static final Map<String, ?> jsonMap = Map.of(
      "username", "azizserin",
      "password", "changeit"
    );

    public static String getAdminToken() {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        HttpRequest request = HttpRequest.newBuilder(URI.create(AUTHENTICATE_ENDPOINT))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(jsonMap)))
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
}
