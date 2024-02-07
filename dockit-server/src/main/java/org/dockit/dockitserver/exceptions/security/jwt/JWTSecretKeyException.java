package org.dockit.dockitserver.exceptions.security.jwt;

/**
 * Exception to be thrown when jwt secret key is not found in the application's keystore
 */
public class JWTSecretKeyException extends RuntimeException {
    public JWTSecretKeyException(String message) {
        super(message);
    }
}
