package org.dockit.dockitserver.exceptions.config;

/**
 * Exception to be thrown when there is an error with an invalid property
 */
public class InvalidPropertyException extends RuntimeException {
    public InvalidPropertyException(String message) {
        super(message);
    }
}
