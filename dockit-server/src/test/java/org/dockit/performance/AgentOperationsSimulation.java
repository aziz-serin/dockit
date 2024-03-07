package org.dockit.performance;

import com.google.gson.Gson;
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
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class AgentOperationsSimulation extends Simulation {

    private static final HttpProtocolBuilder HTTP_PROTOCOL_BUILDER = setupProtocolForSimulation();

    private static final Iterator<Map<String, Object>> FEED_DATA = feedData();

    private static final ScenarioBuilder POST_SCENARIO_BUILDER = buildPostScenario();

    public AgentOperationsSimulation() {
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
                "agentName", UUID.randomUUID().toString(),
                "password", UUID.randomUUID().toString(),
                "allowedUsers", "testuser",
                "agentUrl", "http://someagenturl.com"
        )).iterator();
        return iterator;
    }

    private static ScenarioBuilder buildPostScenario() {
        return CoreDsl.scenario("Load Agent Endpoints")
                .feed(FEED_DATA)
                .exec(http("create-agent-request").post("/api/agent")
                        .header("Content-Type", "application/json")
                        .header("Authorization", Objects.requireNonNull(PerformanceTokenObtainer.getAdminToken()))
                        .body(StringBody("""
                                {
                                "agentName": "${agentName}",
                                "password": "${password}",
                                "allowedUsers": "${allowedUsers}",
                                "agentUrl": "${agentUrl}"
                                }
                                """))
                        .check(status().is(200))
                        .check(bodyString().saveAs("response")))
                .exec(http("get-agent-request").get(session -> {
                    // Get previously created agentId and query that specific agent for
                            // the get request instead of querying all of them
                    Gson gson = new Gson();
                    Map<String, ?> response = gson.fromJson((String) session.get("response"), Map.class);
                    return "/api/agent?id=" + response.get("id");
                })
                        .header("Content-Type", "application/json")
                        .header("Authorization", Objects.requireNonNull(PerformanceTokenObtainer.getAdminToken()))
                        .check(status().is(200)));
    }

    private OpenInjectionStep.RampRate.RampRateOpenInjectionStep postEndpointInjectionProfile() {
        int totalDesiredUserCount = 10;
        double userRampUpPerInterval = 1;
        double rampUpIntervalSeconds = 10;
        int totalRampUptimeSeconds = 30;
        int steadyStateDurationSeconds = 30;

        return rampUsersPerSec(userRampUpPerInterval / (rampUpIntervalSeconds / 60)).to(totalDesiredUserCount)
                .during(Duration.ofSeconds(totalRampUptimeSeconds + steadyStateDurationSeconds));
    }
}
