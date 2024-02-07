package org.dockit.dockitserver.config;

import org.dockit.dockitserver.exceptions.config.ConfigWriterException;
import org.dockit.dockitserver.security.keystore.KeyStoreManager;
import org.dockit.dockitserver.config.utils.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.security.KeyStore;
import java.util.Properties;

import static org.dockit.dockitserver.config.PropertiesManager.generateConfigFromProperties;

/**
 * Manager class for the config of the application
 */
public final class ConfigManager {
    private Config config;
    private KeyStore keyStore;
    private static String PATH_SEPARATOR;

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    /**
     * Checks the OS and sets the path separator
     */
    public ConfigManager() {
        if (OSUtils.OSDetector.isWindows()) {
            PATH_SEPARATOR = "\\";
        } else {
            PATH_SEPARATOR = "/";
        }

    }

    /**
     * @return Previously set path separator from {@link ConfigManager} constructor
     */
    public static String getPathSeparator() {
        return PATH_SEPARATOR;
    }

    /**
     * @return {@link KeyStore} object
     */
    public KeyStore getKeyStore() {
        return keyStore;
    }

    /**
     * @return {@link Config} object
     */
    public Config getConfig() {
        return config;
    }

    /**
     * @param path Root folder path containing the config files
     * @param configFileName Name of the config file
     * @param keyStoreName Name of the keyStore file
     * @return true if a complete config exists, false otherwise
     */
    public boolean configExists(String path, String configFileName, String keyStoreName) {
        File rootDirectory = new File(path);
        if (!rootDirectory.isDirectory()) {
            logger.debug("Root directory {} does not exist", path);
            return false;
        }
        String propertiesPath = path + PATH_SEPARATOR + configFileName;
        String keyStorePath = path + PATH_SEPARATOR + keyStoreName;

        File propertiesFile = new File(propertiesPath);
        if (propertiesFile.isDirectory() || !propertiesFile.isFile()) {
            logger.debug("Properties file {} does not exist/wrong in path {}", configFileName, path);
            return false;
        }
        File keyStoreFile = new File(keyStorePath);
        if (keyStoreFile.isDirectory() || !keyStoreFile.isFile()) {
            logger.debug("KeyStore file {} does not exist/wrong in path {}", keyStoreName, path);
            return false;
        }
        return true;
    }

    private Properties loadPropertiesConfig(String path, String fileName) {
        return PropertiesManager.readProperties(path + PATH_SEPARATOR, fileName);
    }

    private KeyStore loadKeystoreConfig(String keyStoreName, String path, String keyStorePassword) {
        return KeyStoreManager.loadKeyStore(keyStoreName, path + PATH_SEPARATOR, keyStorePassword);
    }

    /**
     * Loads the config file and keystore file from the given path
     *
     * @param path Root folder path containing the config files
     * @param configFileName Name of the config file
     * @param keyStoreName Name of the keyStore file
     */
    public void loadConfig(String path, String configFileName, String keyStoreName) {
        Properties properties = loadPropertiesConfig(path, configFileName);
        config = generateConfigFromProperties(properties);
        keyStore = loadKeystoreConfig(keyStoreName, path, config.getKeyStorePassword());
    }

    /**
     * @param rootPath Root folder path containing the config files
     * @param configFileName Name of the config file
     * @param keyStoreName Name of the keyStore file
     * @param keyStorePassword Password to set for the generated keystore
     * @param properties {@link Properties} containing the config of the application
     * @throws ConfigWriterException if creation of the config fails
     */
    public void createConfig(String rootPath, String configFileName, String keyStoreName,
                             String keyStorePassword, Properties properties) throws ConfigWriterException {
        ConfigWriter configWriter = new ConfigWriter();
        String path = configWriter.createRootDirectory(rootPath);
        Properties savedProperties = configWriter.createProperties(path + PATH_SEPARATOR, configFileName, properties);
        KeyStore ks = configWriter.createKeyStore(path + PATH_SEPARATOR, keyStoreName, keyStorePassword);
        if (savedProperties != null && ks != null) {
            config = generateConfigFromProperties(properties);
            keyStore = ks;
        } else {
            throw new ConfigWriterException("Could not create keystore and/or properties config");
        }
    }
}
