package org.dockit.dockitserver.config;

import org.dockit.dockitserver.exceptions.config.ConfigWriterException;
import org.dockit.dockitserver.security.keystore.KeyStoreManager;
import org.dockit.dockitserver.utils.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.security.KeyStore;
import java.util.Properties;

import static org.dockit.dockitserver.config.PropertiesManager.generateConfigFromProperties;

public final class ConfigManager {
    private Config config;
    private KeyStore keyStore;
    private static String PATH_SEPARATOR;

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

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

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public Config getConfig() {
        return config;
    }

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
        return KeyStoreManager.loadKeyStore(keyStoreName, path, keyStorePassword);
    }

    public void loadConfig(String path, String configFileName, String keyStoreName) {
        Properties properties = loadPropertiesConfig(path, configFileName);
        config = generateConfigFromProperties(properties);
        keyStore = loadKeystoreConfig(keyStoreName, path, config.getKeyStorePassword());
    }

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
