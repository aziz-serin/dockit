package org.dockit.dockitserver.config;

import org.dockit.dockitserver.exceptions.config.InvalidPropertyException;
import org.dockit.dockitserver.testUtils.PropertiesTestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class PropertiesManagerTest {

    final static String fileName = "test.properties";
    final static String wrongFileName = "testX.properties";
    final static String AGENT_CACHE_SIZE_KEY = ConfigConstants.AGENT_CACHE_SIZE.toString();
    final static String AUDIT_CACHE_SIZE_KEY = ConfigConstants.AUDIT_CACHE_SIZE.toString();
    final static String ADMIN_CACHE_SIZE_KEY = ConfigConstants.ADMIN_CACHE_SIZE.toString();
    final static String TOKEN_CACHE_SIZE_KEY = ConfigConstants.API_KEY_CACHE_SIZE.toString();
    final static String KEY_STORE_PASSWORD_KEY = ConfigConstants.KEYSTORE_PASSWORD.toString();
    final static String MAX_AGENT_KEY = ConfigConstants.MAX_AGENT_AMOUNT.toString();
    final static String JWT_ISSUER_KEY = ConfigConstants.JWT_ISSUER.toString();
    final static String JWT_SECRET_ALIAS_KEY = ConfigConstants.JWT_SECRET_ALIAS.toString();
    final static String JWT_EXPIRATION_TIME_KEY = ConfigConstants.JWT_EXPIRATION_TIME.toString();

    final static String CACHE_SIZE_VALUE = "15";
    final static String PASSWORD_VALUE = "password";
    final static String MAX_AGENT_SIZE_VALUE = "12";
    final static String JWT_ISSUER_VALUE = "http://dockit.server.io";
    final static String JWT_SECRET_ALIAS_VALUE = "jwtsecret";
    final static String JWT_EXPIRATION_TIME_VALUE = "60";
    final static String EXCEPTION_MESSAGE = "Invalid property type provided!";
    static String path;

    @TempDir
    static Path tempDir;

    static Properties properties;
    static PropertiesTestUtils propertiesTestUtils;

    @BeforeAll
    public static void setup() {
        properties = new Properties();
        properties.put(AGENT_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
        properties.put(ADMIN_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
        properties.put(AUDIT_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
        properties.put(TOKEN_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
        properties.put(KEY_STORE_PASSWORD_KEY, PASSWORD_VALUE);
        properties.put(MAX_AGENT_KEY, MAX_AGENT_SIZE_VALUE);
        properties.put(JWT_ISSUER_KEY, JWT_ISSUER_VALUE);
        properties.put(JWT_SECRET_ALIAS_KEY, JWT_SECRET_ALIAS_VALUE);
        properties.put(JWT_EXPIRATION_TIME_KEY, JWT_EXPIRATION_TIME_VALUE);
        propertiesTestUtils = new PropertiesTestUtils();
        path = tempDir.toString() + File.pathSeparator;
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
                .hasSize(9)
                .containsEntry(AGENT_CACHE_SIZE_KEY, CACHE_SIZE_VALUE)
                .containsEntry(ADMIN_CACHE_SIZE_KEY, CACHE_SIZE_VALUE)
                .containsEntry(AUDIT_CACHE_SIZE_KEY, CACHE_SIZE_VALUE)
                .containsEntry(TOKEN_CACHE_SIZE_KEY, CACHE_SIZE_VALUE)
                .containsEntry(KEY_STORE_PASSWORD_KEY, PASSWORD_VALUE)
                .containsEntry(MAX_AGENT_KEY, MAX_AGENT_SIZE_VALUE)
                .containsEntry(JWT_ISSUER_KEY, JWT_ISSUER_VALUE)
                .containsEntry(JWT_SECRET_ALIAS_KEY, JWT_SECRET_ALIAS_VALUE)
                .containsEntry(JWT_EXPIRATION_TIME_KEY, JWT_EXPIRATION_TIME_VALUE);
    }

    @Test
    public void generatesConfigFromProperties() {
        Config config = PropertiesManager.generateConfigFromProperties(properties);
        assertThat(config).isInstanceOf(Config.class);
        assertThat(config.getMaxAgentCacheSize().toString()).isEqualTo(CACHE_SIZE_VALUE);
        assertThat(config.getKeyStorePassword()).isEqualTo(PASSWORD_VALUE);
        assertThat(config.getMaxAgentSize().toString()).isEqualTo(MAX_AGENT_SIZE_VALUE);
        assertThat(config.getJwtIssuer()).isEqualTo(JWT_ISSUER_VALUE);
        assertThat(config.getJwtSecretAlias()).isEqualTo(JWT_SECRET_ALIAS_VALUE);
        assertThat(config.getJwtExpirationTime().toString()).isEqualTo(JWT_EXPIRATION_TIME_VALUE);
    }

    @Test
    public void generateConfigFromPropertiesThrowsExceptionGivenNonIntMaxAgentSize() {
        Exception exception = assertThrows(InvalidPropertyException.class, () -> {
            Properties exceptionProperties = new Properties();
            exceptionProperties.put(AGENT_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
            exceptionProperties.put(ADMIN_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
            exceptionProperties.put(AUDIT_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
            exceptionProperties.put(TOKEN_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
            exceptionProperties.put(KEY_STORE_PASSWORD_KEY, PASSWORD_VALUE);
            exceptionProperties.put(MAX_AGENT_KEY, "str");
            exceptionProperties.put(JWT_ISSUER_KEY, JWT_ISSUER_VALUE);
            exceptionProperties.put(JWT_SECRET_ALIAS_KEY, JWT_SECRET_ALIAS_VALUE);
            exceptionProperties.put(JWT_EXPIRATION_TIME_KEY, JWT_EXPIRATION_TIME_VALUE);
            PropertiesManager.generateConfigFromProperties(exceptionProperties);
        });
        assertEquals(EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    public void generateConfigFromPropertiesThrowsExceptionGivenNonIntDBCacheSize() {
        Exception exception = assertThrows(InvalidPropertyException.class, () -> {
            Properties exceptionProperties = new Properties();
            exceptionProperties.put(AGENT_CACHE_SIZE_KEY, "str");
            exceptionProperties.put(ADMIN_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
            exceptionProperties.put(AUDIT_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
            exceptionProperties.put(TOKEN_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
            exceptionProperties.put(KEY_STORE_PASSWORD_KEY, PASSWORD_VALUE);
            exceptionProperties.put(MAX_AGENT_KEY, MAX_AGENT_SIZE_VALUE);
            exceptionProperties.put(JWT_ISSUER_KEY, JWT_ISSUER_VALUE);
            exceptionProperties.put(JWT_SECRET_ALIAS_KEY, JWT_SECRET_ALIAS_VALUE);
            exceptionProperties.put(JWT_EXPIRATION_TIME_KEY, JWT_EXPIRATION_TIME_VALUE);
            PropertiesManager.generateConfigFromProperties(exceptionProperties);
        });
        assertEquals(EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    public void generateConfigFromPropertiesThrowsExceptionGivenNonIntJwtExpiryTime() {
        Exception exception = assertThrows(InvalidPropertyException.class, () -> {
            Properties exceptionProperties = new Properties();
            exceptionProperties.put(AGENT_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
            exceptionProperties.put(ADMIN_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
            exceptionProperties.put(AUDIT_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
            exceptionProperties.put(TOKEN_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
            exceptionProperties.put(KEY_STORE_PASSWORD_KEY, PASSWORD_VALUE);
            exceptionProperties.put(MAX_AGENT_KEY, MAX_AGENT_SIZE_VALUE);
            exceptionProperties.put(JWT_ISSUER_KEY, JWT_ISSUER_VALUE);
            exceptionProperties.put(JWT_SECRET_ALIAS_KEY, JWT_SECRET_ALIAS_VALUE);
            exceptionProperties.put(JWT_EXPIRATION_TIME_KEY, "str");
            PropertiesManager.generateConfigFromProperties(exceptionProperties);
        });
        assertEquals(EXCEPTION_MESSAGE, exception.getMessage());
    }

}
