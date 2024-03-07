package org.dockit.performance;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.dockit.performance.utils.PerformanceTokenObtainer;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class AdminOperationsSimulation extends Simulation {
    private static final HttpProtocolBuilder HTTP_PROTOCOL_BUILDER = setupProtocolForSimulation();

    private static final Iterator<Map<String, Object>> FEED_DATA = feedData();

    private static final ScenarioBuilder POST_SCENARIO_BUILDER = buildPostScenario();

    public AdminOperationsSimulation() {
        this.setUp(POST_SCENARIO_BUILDER.injectOpen(postEndpointInjectionProfile())
                .protocols(HTTP_PROTOCOL_BUILDER));
    }

    private static HttpProtocolBuilder setupProtocolForSimulation() {
        return HttpDsl.http.baseUrl("http://localhost:8080")
                .acceptHeader("application/json")
                .maxConnectionsPerHost(10)
                .userAgentHeader("Gatling/Performance Test");
    }

    private static Iterator<Map<String, Object>> feedData() {
        Iterator<Map<String, Object>> iterator;
        iterator = Stream.generate(() -> Map.<String, Object>of(
                "username", UUID.randomUUID().toString(),
                "password", UUID.randomUUID().toString(),
                "role", "EDITOR"
        )).iterator();
        return iterator;
    }

    private static ScenarioBuilder buildPostScenario() {
        return CoreDsl.scenario("Load Admin Endpoints")
                .feed(FEED_DATA)
                .exec(http("create-admin-request").post("/api/admin")
                        .header("Content-Type", "application/json")
                        .header("Authorization", Objects.requireNonNull(PerformanceTokenObtainer.getAdminToken()))
                        .body(StringBody("{ \"username\": \"${username}\", \"password\": \"${password}\", \"role\": \"${role}\"}"))
                        .check(status().is(200)))
                .exec(http("get-admin-request").get("/api/admin")
                        .header("Content-Type", "application/json")
                        .header("Authorization", Objects.requireNonNull(PerformanceTokenObtainer.getAdminToken()))
                        .check(status().is(200)));
    }

    private OpenInjectionStep.RampRate.RampRateOpenInjectionStep postEndpointInjectionProfile() {
        int totalDesiredUserCount = 30;
        double userRampUpPerInterval = 5;
        double rampUpIntervalSeconds = 12;
        int totalRampUptimeSeconds = 60;
        int steadyStateDurationSeconds = 120;

        return rampUsersPerSec(userRampUpPerInterval / (rampUpIntervalSeconds / 60)).to(totalDesiredUserCount)
                .during(Duration.ofSeconds(totalRampUptimeSeconds + steadyStateDurationSeconds));
    }
}
