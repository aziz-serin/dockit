package org.dockit.dockitserver.exceptions.security;

public class KeyStoreManagerException extends RuntimeException{
    public KeyStoreManagerException(String message, Throwable exception) {
        super(message, exception);
    }

    public KeyStoreManagerException(Throwable exception) {
        super(exception);
    }

    public KeyStoreManagerException(String message) {
        super(message);
    }
}