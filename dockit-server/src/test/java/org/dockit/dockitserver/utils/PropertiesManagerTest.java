package org.dockit.dockitserver.utils;

import org.dockit.dockitserver.config.Config;
import org.dockit.dockitserver.config.ConfigConstants;
import org.dockit.dockitserver.testUtils.PropertiesTestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesManagerTest {

    final static String fileName = "test.properties";
    final static String wrongFileName = "testX.properties";
    // Replace path separator when executing tests in windows
    final static String PATH_SEPARATOR = "/";
    final static String KEY_1 = ConfigConstants.DB_CONNECTION_CACHE_SIZE.toString();
    final static String KEY_2 = ConfigConstants.KEYSTORE_PASSWORD.toString();
    final static String KEY_3 = ConfigConstants.MAX_AGENT_AMOUNT.toString();
    final static String VALUE_1 = "15";
    final static String VALUE_2 = "password";
    final static String VALUE_3 = "12";
    static String path;

    @TempDir
    static Path tempDir;

    static Properties properties;
    static PropertiesTestUtils propertiesTestUtils;

    @BeforeAll
    public static void setup() {
        properties = new Properties();
        properties.put(KEY_1, VALUE_1);
        properties.put(KEY_2, VALUE_2);
        properties.put(KEY_3, VALUE_3);
        propertiesTestUtils = new PropertiesTestUtils();
        path = tempDir.toString() + PATH_SEPARATOR;
        propertiesTestUtils.createPropertiesFile(path, fileName, properties);
    }

    @Test
    public void readPropertiesReturnEmptyPropertiesIfItDoesNotExist() {
        assertThat(PropertiesManager.readProperties(path, wrongFileName))
                .isInstanceOf(Properties.class)
                .hasSize(0);
    }

    @Test
    public void readPropertiesReturnPropertiesIfItExists() {
        assertThat(PropertiesManager.readProperties(path, fileName))
                .isInstanceOf(Properties.class)
                .hasSize(3)
                .containsEntry(KEY_1, VALUE_1)
                .containsEntry(KEY_2, VALUE_2)
                .containsEntry(KEY_3, VALUE_3);
    }

    @Test
    public void generatesConfigFromProperties() {
        Config config = PropertiesManager.generateConfigFromProperties(properties);
        assertThat(config).isInstanceOf(Config.class);
        assertThat(config.getMaxDBCacheSize().toString()).isEqualTo(VALUE_1);
        assertThat(config.getKeyStorePassword()).isEqualTo(VALUE_2);
        assertThat(config.getMaxAgentSize().toString()).isEqualTo(VALUE_3);
    }
}
