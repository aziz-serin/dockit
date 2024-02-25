package org.dockit.dockitagent.rest.routes;

import spark.Request;
import spark.Response;
import spark.Route;

public class PingRoute implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.status(200);
        return "Alive";
    }
}
