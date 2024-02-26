package org.dockit.dockitagent.rest.routes;

import org.dockit.dockitagent.command.Command;
import org.dockit.dockitagent.command.CommandTranslator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;
import spark.Response;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandRouteTest {
    private static final String VALID_COMMAND = "ls";
    private static final String INVALID_COMMAND = "ls -V";
    @Mock
    private Request request;
    @Mock
    private Response response;
    @Mock
    private CommandTranslator commandTranslator;

    @Test
    public void handleReturns400GivenEmptyCommand() {
        when(commandTranslator.getCommand(any())).thenReturn(Optional.empty());

        CommandRoute route = new CommandRoute(commandTranslator);

        String responseMessage = (String) route.handle(request, response);

        assertThat(responseMessage).containsIgnoringCase("invalid request!");
    }

    @Test
    public void handleReturns500GivenCommandCouldBeExecuted() {
        Command command = new Command(INVALID_COMMAND, "");
        when(commandTranslator.getCommand(any())).thenReturn(Optional.of(command));

        CommandRoute route = new CommandRoute(commandTranslator);

        String responseMessage = (String) route.handle(request, response);

        assertThat(responseMessage).containsIgnoringCase("could not execute the command!");
    }

    @Test
    public void handleReturns200GivenCommandCouldBeExecuted() {
        Command command = new Command(VALID_COMMAND, "");
        when(commandTranslator.getCommand(any())).thenReturn(Optional.of(command));

        CommandRoute route = new CommandRoute(commandTranslator);

        String responseMessage = (String) route.handle(request, response);

        assertThat(responseMessage).containsIgnoringCase("successful");
    }

}
