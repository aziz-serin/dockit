package org.dockit.dockitserver.exceptions.config;

public class InvalidPropertyException extends RuntimeException {
    public InvalidPropertyException(String message) {
        super(message);
    }

    public InvalidPropertyException(String message, Throwable e) {
        super(message, e);
    }

}
