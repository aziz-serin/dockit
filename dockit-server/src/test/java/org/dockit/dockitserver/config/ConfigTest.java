package org.dockit.dockitserver.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;


public class ConfigTest {
    static Config config;
    static final Integer MAX_AGENT_SIZE = 5;
    static final Long MAX_CACHE_SIZE = 128L;
    static final String KEYSTORE_PASSWORD = "password";

    @BeforeAll
    public static void setup() {
        config = ConfigBuilder.newBuilder()
                .maxAgentCacheSize(MAX_CACHE_SIZE)
                .maxAuditCacheSize(MAX_CACHE_SIZE)
                .maxAdminCacheSize(MAX_CACHE_SIZE)
                .maxAccessTokenCacheSize(MAX_CACHE_SIZE)
                .maxAgentSize(MAX_AGENT_SIZE)
                .keyStorePassword(KEYSTORE_PASSWORD)
                .build();
    }

    @Test
    public void getMaxAgentCacheSizeReturnsRightCacheSize() {
        assertThat(MAX_CACHE_SIZE).isEqualTo(config.getMaxAgentCacheSize());
    }

    @Test
    public void getMaxAuditCacheSizeReturnsRightCacheSize() {
        assertThat(MAX_CACHE_SIZE).isEqualTo(config.getMaxAuditCacheSize());
    }

    @Test
    public void getMaxAdminCacheSizeReturnsRightCacheSize() {
        assertThat(MAX_CACHE_SIZE).isEqualTo(config.getMaxAdminCacheSize());
    }

    @Test
    public void getMaxAccessTokenCacheSizeReturnsRightCacheSize() {
        assertThat(MAX_CACHE_SIZE).isEqualTo(config.getMaxAccessTokenCacheSize());
    }


    @Test
    public void getMaxAgentSizeReturnAgentSize() {
        assertThat(MAX_AGENT_SIZE).isEqualTo(config.getMaxAgentSize());
    }

    @Test
    public void getKeyStorePasswordReturnsPassword() {
        assertThat(KEYSTORE_PASSWORD).isEqualTo(config.getKeyStorePassword());
    }

    @Test
    public void toPropertiesReturnsProperties() {
        assertThat(config.toProperties()).isInstanceOf(Properties.class);
    }

    @Test
    public void toPropertiesReturnsTruePropertyValues() {
        Properties properties = config.toProperties();
        assertThat(properties.getProperty(ConfigConstants.KEYSTORE_PASSWORD.toString())).isEqualTo(KEYSTORE_PASSWORD);
        assertThat(properties.getProperty(ConfigConstants.MAX_AGENT_AMOUNT.toString()))
                .isEqualTo(MAX_AGENT_SIZE.toString());
        assertThat(properties.getProperty(ConfigConstants.AGENT_CACHE_SIZE.toString()))
                .isEqualTo(MAX_CACHE_SIZE.toString());
        assertThat(properties.getProperty(ConfigConstants.ADMIN_CACHE_SIZE.toString()))
                .isEqualTo(MAX_CACHE_SIZE.toString());
        assertThat(properties.getProperty(ConfigConstants.AUDIT_CACHE_SIZE.toString()))
                .isEqualTo(MAX_CACHE_SIZE.toString());
        assertThat(properties.getProperty(ConfigConstants.ACCESS_TOKEN_CACHE_SIZE.toString()))
                .isEqualTo(MAX_CACHE_SIZE.toString());
    }
}
