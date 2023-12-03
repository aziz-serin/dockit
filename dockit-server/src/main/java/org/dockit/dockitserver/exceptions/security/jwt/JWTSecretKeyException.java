package org.dockit.dockitserver.exceptions.security.jwt;

public class JWTSecretKeyException extends RuntimeException {
    public JWTSecretKeyException(String message) {
        super(message);
    }
}
