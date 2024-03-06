package org.dockit.performance;

import com.nimbusds.jose.shaded.gson.Gson;
import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.dockit.dockitserver.security.encryption.AESGCMEncryptor;
import org.dockit.performance.utils.PerformanceTokenObtainer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class WriteSimulation extends Simulation {
    private static final HttpProtocolBuilder HTTP_PROTOCOL_BUILDER = setupProtocolForSimulation();

    private static final ScenarioBuilder POST_SCENARIO_BUILDER = buildPostScenario();

    public WriteSimulation() {
        this.setUp(POST_SCENARIO_BUILDER.injectOpen(postEndpointInjectionProfile())
                .protocols(HTTP_PROTOCOL_BUILDER));
    }

    private static HttpProtocolBuilder setupProtocolForSimulation() {
        return HttpDsl.http.baseUrl("http://localhost:8080")
                .acceptHeader("application/json")
                .maxConnectionsPerHost(10)
                .userAgentHeader("Gatling/Performance Test");
    }

    private static Iterator<Map<String, Object>> feedData(String agentKey, String agentId) {
        byte[] decodedKey = Base64.getDecoder().decode(agentKey);
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        Iterator<Map<String, Object>> iterator;
        iterator = Stream.generate(() -> {
            try {
                return Map.<String, Object>of(
                        "vmId", UUID.randomUUID().toString(),
                        "category", "vm_cpu",
                        "timeStamp", LocalDateTime.now().toString(),
                        "data", getRandomData(key, agentId)
                );
            } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                     NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        }).iterator();
        return iterator;
    }

    private static ScenarioBuilder buildPostScenario() {
        Map<String, String> parameters = PerformanceTokenObtainer.getAPIToken();
        return CoreDsl.scenario("Load Write Endpoint")
                .feed(feedData(parameters.get("agentKey"), parameters.get("agentId")))
                .exec(http("write-request").post("/api/write?id=" + parameters.get("agentId"))
                        .header("Content-Type", "application/json")
                        .header("X-API-KEY", parameters.get("key"))
                        .body(StringBody("""
                                {
                                "vmId": "${vmId}",
                                "category": "${category}",
                                "timeStamp": "${timeStamp}",
                                "data": "${data}"
                                }
                                """))
                        .check(status().is(200)));
    }

    private static String getRandomData(SecretKey secretKey, String agentId) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        int max = 100;
        int min = 0;
        double result = Math.random() * ((max - min) + min);
        Map<String, ?> cpuUsageData = Map.of(
                "cpu_load", result
        );
        Gson gson = new Gson();
        return AESGCMEncryptor.encrypt(gson.toJson(cpuUsageData), agentId, secretKey);
    }

    private OpenInjectionStep.RampRate.RampRateOpenInjectionStep postEndpointInjectionProfile() {
        int totalDesiredUserCount = 15;
        double userRampUpPerInterval = 3;
        double rampUpIntervalSeconds = 5;
        int totalRampUptimeSeconds = 30;
        int steadyStateDurationSeconds = 30;

        return rampUsersPerSec(userRampUpPerInterval / (rampUpIntervalSeconds / 60)).to(totalDesiredUserCount)
                .during(Duration.ofSeconds(totalRampUptimeSeconds + steadyStateDurationSeconds));
    }
}
