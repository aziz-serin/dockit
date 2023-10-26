package org.dockit.dockitserver.config;

import org.dockit.dockitserver.exceptions.config.ConfigWriterException;
import org.dockit.dockitserver.security.keystore.KeyStoreManager;
import org.dockit.dockitserver.utils.OSUtils;
import org.dockit.dockitserver.utils.PropertiesManager;

import java.io.File;
import java.security.KeyStore;
import java.util.Properties;

import static org.dockit.dockitserver.utils.PropertiesManager.generateConfigFromProperties;

public final class ConfigManager {

    private static Config config;
    private static KeyStore keyStore;
    private static String PATH_SEPARATOR;

    public ConfigManager() {
        if (OSUtils.OSDetector.isWindows()) {
            PATH_SEPARATOR = "\\";
        } else {
            PATH_SEPARATOR = "/";
        }
    }

    public static String getPathSeparator() {
        return PATH_SEPARATOR;
    }

    public static KeyStore getKeyStore() {
        return keyStore;
    }

    public static Config getConfig() {
        return config;
    }

    public boolean configExists(String path, String configFileName, String keyStoreName) {
        File rootDirectory = new File(path);
        if (!rootDirectory.isDirectory()) {
            return false;
        }
        String propertiesPath = path + PATH_SEPARATOR + configFileName;
        String keyStorePath = path + PATH_SEPARATOR + keyStoreName;

        File propertiesFile = new File(propertiesPath);
        if (propertiesFile.isDirectory() || !propertiesFile.isFile()) {
            return false;
        }
        File keyStoreFile = new File(keyStorePath);
        if (keyStoreFile.isDirectory() || !keyStoreFile.isFile()) {
            return false;
        }
        return true;
    }

    private Properties loadPropertiesConfig(String path, String fileName) {
        return PropertiesManager.readProperties(path, fileName);
    }

    private KeyStore loadKeystoreConfig(String path, String keyStorePassword) {
        return KeyStoreManager.loadKeyStore(path, keyStorePassword);
    }

    public void loadConfig(String path, String configFileName, String keyStoreName) {
        Properties properties = loadPropertiesConfig(path, configFileName);
        config = generateConfigFromProperties(properties);
        keyStore = loadKeystoreConfig(path + PATH_SEPARATOR + keyStoreName, config.getKeyStorePassword());
    }

    public void createConfig(String directoryName, String configFileName, String keyStoreName,
                             String keyStorePassword, Properties properties) throws ConfigWriterException {
        ConfigWriter configWriter = new ConfigWriter();
        String path = configWriter.createRootDirectory(directoryName);
        Properties savedProperties = createPropertiesConfig(configWriter, path, configFileName, properties);
        KeyStore ks = createKeyStoreConfig(configWriter, path, keyStoreName, keyStorePassword);
        if (savedProperties != null && ks != null) {
            config = generateConfigFromProperties(properties);
            keyStore = ks;
        } else {
            throw new ConfigWriterException("Could not create keystore and/or properties config");
        }
    }

    private Properties createPropertiesConfig(ConfigWriter configWriter, String path, String configFileName, Properties properties) {
        return configWriter.createProperties(path, configFileName, properties);
    }

    private KeyStore createKeyStoreConfig(ConfigWriter configWriter, String path, String keyStoreName, String keyStorePassword) {
        return configWriter.createKeyStore(path, keyStoreName, keyStorePassword);
    }
}
