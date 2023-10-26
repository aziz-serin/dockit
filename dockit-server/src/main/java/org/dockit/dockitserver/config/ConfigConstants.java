package org.dockit.dockitserver.config;

public enum ConfigConstants {
    MAX_AGENT_AMOUNT,
    DB_CONNECTION_CACHE_SIZE,
    KEYSTORE_PASSWORD,
    DEFAULT_INSTALL_LOCATION,
    DEFAULT_KEYSTORE_NAME,
    DEFAULT_KEYSTORE_PASSWORD,
    DEFAULT_PROPERTIES_NAME;

    public String toString() {
        return switch (this) {
            case MAX_AGENT_AMOUNT -> "max_agent";
            case DB_CONNECTION_CACHE_SIZE -> "db_cache_size";
            case KEYSTORE_PASSWORD -> "keystore_password";
            case DEFAULT_INSTALL_LOCATION -> ".dockit";
            case DEFAULT_KEYSTORE_NAME -> "keyStore.jks";
            case DEFAULT_KEYSTORE_PASSWORD -> "changeit";
            case DEFAULT_PROPERTIES_NAME -> "config.properties";
        };
    }
}
