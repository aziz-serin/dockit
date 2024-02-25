package org.dockit.dockitagent.command;

/**
 * Class containing all the constants for command execution
 */
public final class CommandConstants {
    // A template is returned. Format it with the userName
    public static final String KILL_USER = "sudo pkill -U %s";

    public static final String COMMAND = "command";
    public static final String ARGUMENTS = "arguments";
    public static final String INTRUSION = "intrusion";
    public static final String DATA = "data";
}
