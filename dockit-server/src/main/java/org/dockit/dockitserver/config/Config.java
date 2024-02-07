package org.dockit.dockitserver.config;

import java.util.Properties;

/**
 * Class containing configuration for the application.
 */
public class Config {

    private final Long maxAgentCacheSize;
    private final Long maxAuditCacheSize;
    private final Long maxAdminCacheSize;
    private final Long maxAccessTokenCacheSize;
    private final Integer maxAgentSize;
    private final String keyStorePassword;
    private final String jwtIssuer;
    private final String jwtSecretAlias;
    private final Integer jwtExpirationTime;

    /**
     * @param maxAgentCacheSize Agent cache size from the config file
     * @param maxAuditCacheSize Audit cache size from the config file
     * @param maxAdminCacheSize Admin cache size from the config file
     * @param maxAccessTokenCacheSize Access token cache size from the config file
     * @param maxAgentSize Max simultaneously operating agent size
     * @param keyStorePassword Default keystore password
     * @param jwtIssuer Jwt issuer
     * @param jwtSecretAlias Jwt secret alias
     * @param jwtExpirationTime Jwt expiration period in minutes
     */
    protected Config(Long maxAgentCacheSize, Long maxAuditCacheSize, Long maxAdminCacheSize, Long maxAccessTokenCacheSize,
                     Integer maxAgentSize, String keyStorePassword, String jwtIssuer, String jwtSecretAlias,
                     Integer jwtExpirationTime) {
        this.maxAgentCacheSize = maxAgentCacheSize;
        this.maxAuditCacheSize = maxAuditCacheSize;
        this.maxAdminCacheSize = maxAdminCacheSize;
        this.maxAccessTokenCacheSize = maxAccessTokenCacheSize;
        this.maxAgentSize = maxAgentSize;
        this.keyStorePassword = keyStorePassword;
        this.jwtIssuer = jwtIssuer;
        this.jwtSecretAlias = jwtSecretAlias;
        this.jwtExpirationTime = jwtExpirationTime;
    }

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

    public String getJwtIssuer() {
        return jwtIssuer;
    }

    public String getJwtSecretAlias() {
        return jwtSecretAlias;
    }

    public Integer getJwtExpirationTime() {
        return jwtExpirationTime;
    }

    /**
     * @return {@link Properties} object generated from individual properties
     */
    public Properties toProperties() {
        Properties properties = new Properties();
        properties.put(ConfigConstants.AGENT_CACHE_SIZE.toString(), maxAgentCacheSize.toString());
        properties.put(ConfigConstants.AUDIT_CACHE_SIZE.toString(), maxAuditCacheSize.toString());
        properties.put(ConfigConstants.ADMIN_CACHE_SIZE.toString(), maxAdminCacheSize.toString());
        properties.put(ConfigConstants.API_KEY_CACHE_SIZE.toString(), maxAccessTokenCacheSize.toString());
        properties.put(ConfigConstants.MAX_AGENT_AMOUNT.toString(), maxAgentSize.toString());
        properties.put(ConfigConstants.KEYSTORE_PASSWORD.toString(), keyStorePassword);
        properties.put(ConfigConstants.JWT_ISSUER.toString(), jwtIssuer);
        properties.put(ConfigConstants.JWT_SECRET_ALIAS.toString(), jwtSecretAlias);
        properties.put(ConfigConstants.JWT_EXPIRATION_TIME.toString(), jwtExpirationTime.toString());
        return properties;
    }
}
