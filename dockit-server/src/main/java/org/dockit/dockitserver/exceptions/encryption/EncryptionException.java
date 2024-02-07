package org.dockit.dockitserver.exceptions.encryption;

/**
 * Exception to be thrown when there is an issue with encryption/decryption
 */
public class EncryptionException extends Exception {
    public EncryptionException(String message) {
        super(message);
    }
}
