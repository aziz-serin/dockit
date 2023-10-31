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
    static final String KEY_STORE_NAME = "keystore.jks";
    static final String KEY_STORE_PASSWORD = "changeit";
    static final String ROOT_FOLDER_NAME = ".dockit";
    final static String KEY_1 = ConfigConstants.DB_CONNECTION_CACHE_SIZE.toString();
    final static String KEY_2 = ConfigConstants.KEYSTORE_PASSWORD.toString();
    final static String KEY_3 = ConfigConstants.MAX_AGENT_AMOUNT.toString();
    final static String VALUE_1 = "15";
    final static String VALUE_2 = "changeit";
    final static String VALUE_3 = "12";
    static final String SEPARATOR = File.separator;

    static ConfigManager configManager;
    static ConfigWriter configWriter;
    static String path;
    static Properties properties;

    @TempDir
    static Path tempdir;

    @BeforeAll
    public static void setup() {
        path = tempdir.toString();
        configManager = new ConfigManager();
        configWriter = new ConfigWriter();
        properties = new Properties();
        properties.put(KEY_1, VALUE_1);
        properties.put(KEY_2, VALUE_2);
        properties.put(KEY_3, VALUE_3);
    }

    @Test
    public void configManagerInitialisesSeparator() {
        assertThat(ConfigManager.getPathSeparator()).isEqualTo(SEPARATOR);
    }

    @Test
    public void configExistsReturnsFalseGivenRootDirectoryDoesNotExist() {
        assertFalse(configManager.configExists(path, CONFIG_FILE_NAME, KEY_STORE_NAME));
    }

    @Test
    public void configExistsReturnsFalseGivenPropertiesDoesNotExist() {
        String rootPath = path + File.separator + ROOT_FOLDER_NAME;
        File rootDirectory = new File(rootPath);
        boolean result = rootDirectory.mkdir();

        assertTrue(result);
        assertFalse(configManager.configExists(rootPath, CONFIG_FILE_NAME, KEY_STORE_NAME));
    }

    @Test
    public void configExistsReturnsFalseGivenPropertiesIsDirectory() {
        String rootPath = path + File.separator + ROOT_FOLDER_NAME;
        File rootDirectory = new File(rootPath);
        boolean rootResult = rootDirectory.mkdir();
        File propertiesDirectory = new File(rootPath + File.separator + CONFIG_FILE_NAME);
        boolean propertiesResult = propertiesDirectory.mkdir();

        assertTrue(rootResult);
        assertTrue(propertiesResult);
        assertFalse(configManager.configExists(rootPath, CONFIG_FILE_NAME, KEY_STORE_NAME));
    }

    @Test
    public void configExistsReturnsFalseGivenKeyStoreDoesNotExist() throws IOException {
        String rootPath = path + File.separator + ROOT_FOLDER_NAME;
        File rootDirectory = new File(rootPath);
        boolean rootResult = rootDirectory.mkdir();
        File propertiesFile = new File(rootPath + File.separator + CONFIG_FILE_NAME);
        boolean propertiesResult = propertiesFile.createNewFile();

        assertTrue(rootResult);
        assertTrue(propertiesResult);
        assertFalse(configManager.configExists(rootPath, CONFIG_FILE_NAME, KEY_STORE_NAME));
    }

    @Test
    public void configExistsReturnsFalseGivenKeyStoreIsDirectory() throws IOException {
        String rootPath = path + File.separator + ROOT_FOLDER_NAME;
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
        String rootPath = path + File.separator + ROOT_FOLDER_NAME;
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
        String rootCreationResult = configWriter.createRootDirectory(path, ROOT_FOLDER_NAME);
        assertThat(rootCreationResult).isEqualTo(path + File.separator + ROOT_FOLDER_NAME);

        KeyStore ks = configWriter
                .createKeyStore(KEY_STORE_NAME, rootCreationResult + File.separator, KEY_STORE_PASSWORD);
        assertThat(ks).isNotNull();

        Properties returnedProperties = configWriter
                .createProperties(rootCreationResult + File.separator, CONFIG_FILE_NAME, properties);
        assertThat(returnedProperties).isNotNull();
        assertThat(returnedProperties).hasSize(3);

        configManager.loadConfig(rootCreationResult + File.separator, CONFIG_FILE_NAME, KEY_STORE_NAME);

        assertThat(ConfigManager.getConfig()).isInstanceOf(Config.class);
        assertThat(ConfigManager.getKeyStore()).isInstanceOf(KeyStore.class);
    }

    @Test
    public void createConfigCreatesConfig() {
        configManager
                .createConfig(path, ROOT_FOLDER_NAME, CONFIG_FILE_NAME, KEY_STORE_NAME, KEY_STORE_PASSWORD, properties);

        assertThat(ConfigManager.getConfig()).isInstanceOf(Config.class);
        assertThat(ConfigManager.getKeyStore()).isInstanceOf(KeyStore.class);
    }
}
