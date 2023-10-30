package org.dockit.dockitserver.config;

import org.dockit.dockitserver.config.Config;
import org.dockit.dockitserver.config.ConfigConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;


public class ConfigTest {
    static Config config;
    static final Integer MAX_AGENT_SIZE = 5;
    static final Integer MAX_DB_CACHE_SIZE = 128;
    static final String KEYSTORE_PASSWORD = "password";

    @BeforeAll
    public static void setup() {
        config = new Config.ConfigBuilder().setKeyStorePassword(KEYSTORE_PASSWORD)
                .setMaxAgentSize(MAX_AGENT_SIZE)
                .setMaxDBCacheSize(MAX_DB_CACHE_SIZE)
                .build();
    }

    @Test
    public void getCacheSizeReturnsRightCacheSize() {
        assertThat(MAX_DB_CACHE_SIZE).isEqualTo(config.getMaxDBCacheSize());
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
        assertThat(properties.getProperty(ConfigConstants.DB_CONNECTION_CACHE_SIZE.toString()))
                .isEqualTo(MAX_DB_CACHE_SIZE.toString());
    }
}
