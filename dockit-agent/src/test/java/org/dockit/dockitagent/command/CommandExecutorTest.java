package org.dockit.dockitagent.command;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CommandExecutorTest {
    private static final String VALID_COMMAND = "ls";
    private static final String INVALID_COMMAND = "ls -V";

    @Test
    public void executeReturnsFalseGivenInvalidCommand() {
        CommandExecutor commandExecutor = new CommandExecutor();
        Command command = new Command(INVALID_COMMAND, "");

        assertFalse(commandExecutor.execute(command));
    }

    @Test
    public void executeReturnsFalseGivenNullExecutable() {
        CommandExecutor commandExecutor = new CommandExecutor();
        Command command = new Command(null, "");

        assertFalse(commandExecutor.execute(command));
    }

    @Test
    public void executeReturnsFalseGivenNullArguments() {
        CommandExecutor commandExecutor = new CommandExecutor();
        Command command = new Command(null, "");

        assertFalse(commandExecutor.execute(command));
    }

    @Test
    public void executeReturnsTrueGivenValidCommand() {
        CommandExecutor commandExecutor = new CommandExecutor();
        Command command = new Command(VALID_COMMAND, "");

        assertTrue(commandExecutor.execute(command));
    }
}
