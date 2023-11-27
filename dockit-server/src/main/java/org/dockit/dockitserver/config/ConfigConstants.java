package org.dockit.dockitserver.config;

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
    DEFAULT_PROPERTIES_NAME;

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
            case DEFAULT_KEYSTORE_NAME -> "keystore.jks";
            case DEFAULT_KEYSTORE_PASSWORD -> "changeit";
            case DEFAULT_PROPERTIES_NAME -> "sample_config.properties";
        };
    }
}
