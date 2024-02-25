package org.dockit.dockitagent.rest.routes;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Route for the /ping endpoint
 */
public class PingRoute implements Route {


    /**
     * Dummy endpoint which indicates that the server is running
     */
    @Override
    public Object handle(Request request, Response response) {
        response.status(200);
        return "Alive";
    }
}
