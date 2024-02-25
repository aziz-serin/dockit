package org.dockit.dockitagent.command;

/**
 * Record instance to hold information about a given command
 *
 * @param alias alias for the given command
 * @param argument arguments to be used for the given command
 */
public record Command(String alias, String argument) {
}
