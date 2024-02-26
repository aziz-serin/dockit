package org.dockit.dockitagent.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Utility class to execute shell commands on the running machine
 */
public class CommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

    /**
     * Method to execute a given command and return an indication about the execution
     *
     * @param command {@link Command} to be executed
     * @return true if successfully executed, false otherwise
     */
    public boolean execute(Command command) {
        String executable = command.alias();
        String arguments = command.argument();
        if (executable == null) {
            return false;
        }
        return execute(executable, arguments);
    }

    private boolean execute(String executable, String arguments) {
        String fullCommand = executable.formatted(arguments);
        try {
            Process process = Runtime.getRuntime().exec(fullCommand);
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
