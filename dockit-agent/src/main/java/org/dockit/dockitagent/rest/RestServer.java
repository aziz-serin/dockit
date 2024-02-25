package org.dockit.dockitagent.rest;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.dockit.dockitagent.command.Command;
import org.dockit.dockitagent.command.CommandExecutor;
import org.dockit.dockitagent.command.CommandTranslator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;
import java.util.Optional;

import static spark.Spark.after;
import static spark.Spark.post;
import static spark.Spark.get;

@Singleton
public class RestServer {

    private static CommandTranslator commandTranslator;

    @Inject
    public RestServer(CommandTranslator commandTranslator) {
        RestServer.commandTranslator = commandTranslator;
    }

    public void setupEndpoints() {
        post("/command", (request, response) -> {
            Optional<Command> command = commandTranslator.getCommand(request.body());
            Gson gson = new Gson();
            if (command.isEmpty()) {
                response.status(400);
                return gson.toJson(Map.of(
                        "message", "Invalid request!"
                ));
            }
            CommandExecutor commandExecutor = new CommandExecutor();
            if (commandExecutor.execute(command.get())) {
                response.status(200);
                return gson.toJson(Map.of(
                        "message", "Successful"
                ));
            } else {
                response.status(500);
                return gson.toJson(Map.of(
                        "message", "Could not execute the command!"
                ));
            }
        });

        // Dummy endpoint to indicate the server is alive
        get("/ping", RestServer.ping);

        after((request, response) -> {
            response.type("application/json");
        });
    }

    public static Route ping = (Request req, Response res) -> {
        res.status(200);
        return "Alive";
    };
}
