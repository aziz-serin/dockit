package org.dockit.dockitserver.exceptions.config;

/**
 * Exception to be thrown when there is an error writing the config
 */
public class ConfigWriterException extends RuntimeException {
    public ConfigWriterException(String message) {
        super(message);
    }
}
