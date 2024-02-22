package org.dockit.dockitserver.config;

/**
 * An enum containing various constants which are used for the {@link Config} object.
 */
public enum ConfigConstants {
    MAX_AGENT_AMOUNT,
    DEFAULT_MAX_AGENT_AMOUNT,
    AGENT_CACHE_SIZE,
    DEFAULT_AGENT_CACHE_SIZE,
    AUDIT_CACHE_SIZE,
    DEFAULT_AUDIT_CACHE_SIZE,
    ADMIN_CACHE_SIZE,
    DEFAULT_ADMIN_CACHE_SIZE,
    API_KEY_CACHE_SIZE,
    DEFAULT_API_KEY_CACHE_SIZE,
    KEYSTORE_PASSWORD,
    DEFAULT_INSTALL_LOCATION,
    DEFAULT_KEYSTORE_NAME,
    DEFAULT_KEYSTORE_PASSWORD,
    DEFAULT_PROPERTIES_NAME,
    JWT_EXPIRATION_TIME,
    JWT_SECRET_ALIAS,
    JWT_ISSUER,
    DEFAULT_JWT_EXPIRATION_TIME,
    DEFAULT_JWT_SECRET_ALIAS,
    DEFAULT_JWT_ISSUER,
    IMPORTANCE,
    DEFAULT_IMPORTANCE,
    SENDING_MAIL_ADDRESS,
    DEFAULT_SENDING_MAIL_ADDRESS;

    public String toString() {
        return switch (this) {
            case MAX_AGENT_AMOUNT -> "max_agent";
            case DEFAULT_MAX_AGENT_AMOUNT -> "5";
            case AGENT_CACHE_SIZE -> "agent_cache_size";
            case DEFAULT_AGENT_CACHE_SIZE -> "50";
            case AUDIT_CACHE_SIZE -> "audit_cache_size";
            case DEFAULT_AUDIT_CACHE_SIZE -> "5000";
            case ADMIN_CACHE_SIZE -> "admin_cache_size";
            case DEFAULT_ADMIN_CACHE_SIZE -> "50";
            case API_KEY_CACHE_SIZE -> "api_key_cache_size";
            case DEFAULT_API_KEY_CACHE_SIZE -> "50";
            case KEYSTORE_PASSWORD -> "keystore_password";
            case DEFAULT_INSTALL_LOCATION -> ".dockit";
            case DEFAULT_KEYSTORE_NAME -> "keystore.jceks";
            case DEFAULT_KEYSTORE_PASSWORD -> "changeit";
            case DEFAULT_PROPERTIES_NAME -> "config.properties";
            case JWT_EXPIRATION_TIME -> "jwt_expiration_time";
            case JWT_SECRET_ALIAS -> "jwt_secret_alias";
            case JWT_ISSUER -> "jwt_issuer";
            case DEFAULT_JWT_EXPIRATION_TIME -> "60";
            case DEFAULT_JWT_SECRET_ALIAS -> "jwtsecret";
            case DEFAULT_JWT_ISSUER -> "http://dockit.server.io";
            case IMPORTANCE -> "importance";
            case DEFAULT_IMPORTANCE -> "LOW";
            case SENDING_MAIL_ADDRESS -> "sending_mail_address";
            case DEFAULT_SENDING_MAIL_ADDRESS -> "";
        };
    }
}
