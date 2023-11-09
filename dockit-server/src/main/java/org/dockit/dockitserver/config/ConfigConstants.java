package org.dockit.dockitserver.config;

import java.util.Arrays;
import java.util.List;

public enum ConfigConstants {
    MAX_AGENT_AMOUNT,
    AGENT_CACHE_SIZE,
    AUDIT_CACHE_SIZE,
    ADMIN_CACHE_SIZE,
    ACCESS_TOKEN_CACHE_SIZE,
    KEYSTORE_PASSWORD,
    DEFAULT_INSTALL_LOCATION,
    DEFAULT_KEYSTORE_NAME,
    DEFAULT_KEYSTORE_PASSWORD,
    DEFAULT_PROPERTIES_NAME;

    public String toString() {
        return switch (this) {
            case MAX_AGENT_AMOUNT -> "max_agent";
            case AGENT_CACHE_SIZE -> "agent_cache_size";
            case AUDIT_CACHE_SIZE -> "audit_cache_size";
            case ADMIN_CACHE_SIZE -> "admin_cache_size";
            case ACCESS_TOKEN_CACHE_SIZE -> "access_token_cache_size";
            case KEYSTORE_PASSWORD -> "keystore_password";
            case DEFAULT_INSTALL_LOCATION -> ".dockit";
            case DEFAULT_KEYSTORE_NAME -> "keyStore.jks";
            case DEFAULT_KEYSTORE_PASSWORD -> "changeit";
            case DEFAULT_PROPERTIES_NAME -> "config.properties";
        };
    }
}
