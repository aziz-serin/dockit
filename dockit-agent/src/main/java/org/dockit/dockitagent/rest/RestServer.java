package org.dockit.dockitagent.rest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.dockit.dockitagent.rest.routes.CommandRoute;
import org.dockit.dockitagent.rest.routes.PingRoute;

import static spark.Spark.after;
import static spark.Spark.post;
import static spark.Spark.get;

/**
 * Server to be started to accept requests
 */
@Singleton
public class RestServer {

    private final CommandRoute commandRoute;
    private final PingRoute pingRoute;

    /**
     * Inject any route to the constructor and then create the appropriate endpoint in setupEndpoints() method
     *
     * @param commandRoute {@link CommandRoute} instance to be injected
     * @param pingRoute {@link PingRoute} instance to be injected
     */
    @Inject
    public RestServer(CommandRoute commandRoute, PingRoute pingRoute) {
        this.commandRoute = commandRoute;
        this.pingRoute = pingRoute;
    }

    /**
     * Setup all the injected endpoints in this method
     */
    public void setupEndpoints() {
        post("/command", commandRoute);

        // Dummy endpoint to indicate the server is alive
        get("/ping", pingRoute);

        after((request, response) -> response.type("application/json"));
    }
}
