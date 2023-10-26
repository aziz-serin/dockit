package org.dockit.dockitserver.exceptions.config;

public class ConfigWriterException extends RuntimeException {
    public ConfigWriterException(String message, Throwable exception) {
        super(message, exception);
    }

    public ConfigWriterException(String message) {
        super(message);
    }
}
