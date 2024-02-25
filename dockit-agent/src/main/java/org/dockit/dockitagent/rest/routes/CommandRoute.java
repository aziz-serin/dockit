package org.dockit.dockitagent.rest.routes;

import com.google.gson.Gson;
import com.google.inject.Inject;
import org.dockit.dockitagent.command.Command;
import org.dockit.dockitagent.command.CommandExecutor;
import org.dockit.dockitagent.command.CommandTranslator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;
import java.util.Optional;

public class CommandRoute implements Route {

    private final CommandTranslator commandTranslator;

    @Inject
    public CommandRoute(CommandTranslator commandTranslator) {
        this.commandTranslator = commandTranslator;
    }

    @Override
    public Object handle(Request request, Response response) {
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
    }
}
