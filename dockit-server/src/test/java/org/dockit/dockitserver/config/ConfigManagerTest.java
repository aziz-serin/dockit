package org.dockit.dockitserver.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigManagerTest {

    static final String CONFIG_FILE_NAME = "test.properties";
    static final String KEY_STORE_NAME = "keystore.jceks";
    static final String KEY_STORE_PASSWORD = "changeit";
    static final String ROOT_FOLDER_NAME = ".dockit";
    final static String AGENT_CACHE_SIZE_KEY = ConfigConstants.AGENT_CACHE_SIZE.toString();
    final static String AUDIT_CACHE_SIZE_KEY = ConfigConstants.AUDIT_CACHE_SIZE.toString();
    final static String ADMIN_CACHE_SIZE_KEY = ConfigConstants.ADMIN_CACHE_SIZE.toString();
    final static String TOKEN_CACHE_SIZE_KEY = ConfigConstants.API_KEY_CACHE_SIZE.toString();
    final static String KEY_STORE_PASSWORD_KEY = ConfigConstants.KEYSTORE_PASSWORD.toString();
    final static String MAX_AGENT_KEY = ConfigConstants.MAX_AGENT_AMOUNT.toString();
    final static String JWT_ISSUER_KEY = ConfigConstants.JWT_ISSUER.toString();
    final static String JWT_SECRET_ALIAS_KEY = ConfigConstants.JWT_SECRET_ALIAS.toString();
    final static String JWT_EXPIRATION_TIME_KEY = ConfigConstants.JWT_EXPIRATION_TIME.toString();
    final static String IMPORTANCE_KEY = ConfigConstants.IMPORTANCE.toString();
    final static String SENDING_MAIL_KEY = ConfigConstants.SENDING_MAIL_ADDRESS.toString();

    final static String CACHE_SIZE_VALUE = "15";
    final static String MAX_AGENT_SIZE_VALUE = "12";
    final static String JWT_ISSUER_VALUE = "http://dockit.server.io";
    final static String JWT_SECRET_ALIAS_VALUE = "jwtsecret";
    final static String JWT_EXPIRATION_TIME_VALUE = "60";
    final static String IMPORTANCE = "LOW";
    final static String SENDING_MAIL = "";
    static final String SEPARATOR = File.separator;

    static ConfigManager configManager;
    static ConfigWriter configWriter;
    static Properties properties;

    @TempDir
    Path tempDir;

    @BeforeAll
    public static void setup() {
        configManager = new ConfigManager();
        configWriter = new ConfigWriter();
        properties = new Properties();
        properties.put(AGENT_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
        properties.put(ADMIN_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
        properties.put(AUDIT_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
        properties.put(TOKEN_CACHE_SIZE_KEY, CACHE_SIZE_VALUE);
        properties.put(KEY_STORE_PASSWORD_KEY, KEY_STORE_PASSWORD);
        properties.put(MAX_AGENT_KEY, MAX_AGENT_SIZE_VALUE);
        properties.put(JWT_ISSUER_KEY, JWT_ISSUER_VALUE);
        properties.put(JWT_SECRET_ALIAS_KEY, JWT_SECRET_ALIAS_VALUE);
        properties.put(JWT_EXPIRATION_TIME_KEY, JWT_EXPIRATION_TIME_VALUE);
        properties.put(IMPORTANCE_KEY, IMPORTANCE);
        properties.put(SENDING_MAIL_KEY, SENDING_MAIL);
    }

    @Test
    public void configManagerInitialisesSeparator() {
        assertThat(ConfigManager.getPathSeparator()).isEqualTo(SEPARATOR);
    }

    @Test
    public void configExistsReturnsFalseGivenRootDirectoryDoesNotExist() {
        String path = tempDir.toString() + SEPARATOR;
        assertFalse(configManager.configExists(path, CONFIG_FILE_NAME, KEY_STORE_NAME));
    }

    @Test
    public void configExistsReturnsFalseGivenPropertiesDoesNotExist() {
        String path = tempDir.toString() + SEPARATOR;
        String rootPath = path + ROOT_FOLDER_NAME;
        File rootDirectory = new File(rootPath);
        boolean result = rootDirectory.mkdir();

        assertTrue(result);
        assertFalse(configManager.configExists(rootPath, CONFIG_FILE_NAME, KEY_STORE_NAME));
    }

    @Test
    public void configExistsReturnsFalseGivenPropertiesIsDirectory() {
        String path = tempDir.toString() + SEPARATOR;
        String rootPath = path + ROOT_FOLDER_NAME;
        File rootDirectory = new File(rootPath);
        boolean rootResult = rootDirectory.mkdir();
        File propertiesDirectory = new File(rootPath + SEPARATOR + CONFIG_FILE_NAME);
        boolean propertiesResult = propertiesDirectory.mkdir();

        assertTrue(rootResult);
        assertTrue(propertiesResult);
        assertFalse(configManager.configExists(rootPath, CONFIG_FILE_NAME, KEY_STORE_NAME));
    }

    @Test
    public void configExistsReturnsFalseGivenKeyStoreDoesNotExist() throws IOException {
        String path = tempDir.toString() + SEPARATOR;
        String rootPath = path + ROOT_FOLDER_NAME;
        File rootDirectory = new File(rootPath);
        boolean rootResult = rootDirectory.mkdir();
        File propertiesFile = new File(rootPath + CONFIG_FILE_NAME);
        boolean propertiesResult = propertiesFile.createNewFile();

        assertTrue(rootResult);
        assertTrue(propertiesResult);
        assertFalse(configManager.configExists(rootPath, CONFIG_FILE_NAME, KEY_STORE_NAME));
    }

    @Test
    public void configExistsReturnsFalseGivenKeyStoreIsDirectory() throws IOException {
        String path = tempDir.toString() + SEPARATOR;
        String rootPath = path + ROOT_FOLDER_NAME;
        File rootDirectory = new File(rootPath);
        boolean rootResult = rootDirectory.mkdir();
        File propertiesFile = new File(rootPath + File.separator + CONFIG_FILE_NAME);
        boolean propertiesResult = propertiesFile.createNewFile();
        File keyStoreDirectory = new File(rootPath + File.separator + KEY_STORE_NAME);
        boolean keyStoreResult = keyStoreDirectory.mkdir();

        assertTrue(rootResult);
        assertTrue(propertiesResult);
        assertTrue(keyStoreResult);
        assertFalse(configManager.configExists(rootPath, CONFIG_FILE_NAME, KEY_STORE_NAME));
    }

    @Test
    public void configExistsReturnsTrue() throws IOException {
        String path = tempDir.toString() + SEPARATOR;
        String rootPath = path + ROOT_FOLDER_NAME;
        File rootDirectory = new File(rootPath);
        boolean rootResult = rootDirectory.mkdir();
        File propertiesFile = new File(rootPath + File.separator + CONFIG_FILE_NAME);
        boolean propertiesResult = propertiesFile.createNewFile();
        File keyStoreFile = new File(rootPath + File.separator + KEY_STORE_NAME);
        boolean keyStoreResult = keyStoreFile.createNewFile();

        assertTrue(rootResult);
        assertTrue(propertiesResult);
        assertTrue(keyStoreResult);
        assertTrue(configManager.configExists(rootPath, CONFIG_FILE_NAME, KEY_STORE_NAME));
    }

    @Test
    public void loadConfigLoadsConfigAndProperties() {
        String path = tempDir.toString() + SEPARATOR  + ROOT_FOLDER_NAME;
        String rootCreationResult = configWriter.createRootDirectory(path);
        assertThat(rootCreationResult).isEqualTo(path);

        KeyStore ks = configWriter
                .createKeyStore(rootCreationResult + File.separator, KEY_STORE_NAME, KEY_STORE_PASSWORD);
        assertThat(ks).isNotNull();

        Properties returnedProperties = configWriter
                .createProperties(rootCreationResult + File.separator, CONFIG_FILE_NAME, properties);
        assertThat(returnedProperties).isNotNull();
        assertThat(returnedProperties).hasSize(11);

        configManager.loadConfig(rootCreationResult + File.separator, CONFIG_FILE_NAME, KEY_STORE_NAME);

        assertThat(configManager.getConfig()).isInstanceOf(Config.class);
        assertThat(configManager.getKeyStore()).isInstanceOf(KeyStore.class);
    }

    @Test
    public void createConfigCreatesConfig() {
        String path = tempDir.toString() + SEPARATOR;
        configManager
                .createConfig(path + ROOT_FOLDER_NAME, CONFIG_FILE_NAME, KEY_STORE_NAME, KEY_STORE_PASSWORD, properties);

        assertThat(configManager.getConfig()).isInstanceOf(Config.class);
        assertThat(configManager.getKeyStore()).isInstanceOf(KeyStore.class);
    }
}
