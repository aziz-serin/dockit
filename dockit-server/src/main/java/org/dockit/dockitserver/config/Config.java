package org.dockit.dockitserver.config;

import java.util.Properties;

public class Config {
    private final Integer maxDBCacheSize;
    private final Integer maxAgentSize;
    private final String keyStorePassword;

    public Integer getMaxDBCacheSize() {
        return maxDBCacheSize;
    }

    public Integer getMaxAgentSize() {
        return maxAgentSize;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public Properties toProperties() {
        Properties properties = new Properties();
        properties.put(ConfigConstants.DB_CONNECTION_CACHE_SIZE.toString(), maxDBCacheSize.toString());
        properties.put(ConfigConstants.MAX_AGENT_AMOUNT.toString(), maxAgentSize.toString());
        properties.put(ConfigConstants.KEYSTORE_PASSWORD.toString(), keyStorePassword);
        return properties;
    }

    private Config(ConfigBuilder builder) {
        this.maxDBCacheSize = builder.maxDBCacheSize;
        this.maxAgentSize = builder.maxAgentSize;
        this.keyStorePassword = builder.keyStorePassword;
    }

    public static class ConfigBuilder {
        private Integer maxDBCacheSize;
        private Integer maxAgentSize;
        private String keyStorePassword;

        public ConfigBuilder(Integer maxDBCacheSize, Integer maxAgentSize, String keyStorePassword) {
            this.maxDBCacheSize = maxDBCacheSize;
            this.maxAgentSize = maxAgentSize;
            this.keyStorePassword = keyStorePassword;
        }

        public ConfigBuilder() {}

        public ConfigBuilder setMaxDBCacheSize(Integer size) {
            this.maxDBCacheSize = size;
            return this;
        }

        public ConfigBuilder setMaxAgentSize(Integer size) {
            this.maxAgentSize = size;
            return this;
        }

        public ConfigBuilder setKeyStorePassword(String password) {
            this.keyStorePassword = password;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }
}
