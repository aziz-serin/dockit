package org.dockit.dockitserver.exceptions.security.key;

/**
 * Exception to be thrown when there is a problem with a keystore operation
 */
public class KeyStoreException extends Exception {
    public KeyStoreException(String message) {
        super(message);
    }
}
