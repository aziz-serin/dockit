package org.dockit.dockitserver.config;

import java.util.Properties;

public class Config {
    private final Long maxAgentCacheSize;
    private final Long maxAuditCacheSize;
    private final Long maxAdminCacheSize;
    private final Long maxAccessTokenCacheSize;
    private final Integer maxAgentSize;
    private final String keyStorePassword;

    public Long getMaxAgentCacheSize() {
        return maxAgentCacheSize;
    }

    public Long getMaxAuditCacheSize() {
        return maxAuditCacheSize;
    }

    public Long getMaxAdminCacheSize() {
        return maxAdminCacheSize;
    }

    public Long getMaxAccessTokenCacheSize() {
        return maxAccessTokenCacheSize;
    }

    public Integer getMaxAgentSize() {
        return maxAgentSize;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public Properties toProperties() {
        Properties properties = new Properties();
        properties.put(ConfigConstants.AGENT_CACHE_SIZE.toString(), maxAgentCacheSize.toString());
        properties.put(ConfigConstants.AUDIT_CACHE_SIZE.toString(), maxAuditCacheSize.toString());
        properties.put(ConfigConstants.ADMIN_CACHE_SIZE.toString(), maxAdminCacheSize.toString());
        properties.put(ConfigConstants.API_KEY_CACHE_SIZE.toString(), maxAccessTokenCacheSize.toString());
        properties.put(ConfigConstants.MAX_AGENT_AMOUNT.toString(), maxAgentSize.toString());
        properties.put(ConfigConstants.KEYSTORE_PASSWORD.toString(), keyStorePassword);
        return properties;
    }

    protected Config(Long maxAgentCacheSize, Long maxAuditCacheSize, Long maxAdminCacheSize, Long maxAccessTokenCacheSize,
                   Integer maxAgentSize, String keyStorePassword) {
        this.maxAgentCacheSize = maxAgentCacheSize;
        this.maxAuditCacheSize = maxAuditCacheSize;
        this.maxAdminCacheSize = maxAdminCacheSize;
        this.maxAccessTokenCacheSize = maxAccessTokenCacheSize;
        this.maxAgentSize = maxAgentSize;
        this.keyStorePassword = keyStorePassword;
    }
}
