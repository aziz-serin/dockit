package org.dockit.dockitserver.utils;

import org.dockit.dockitserver.config.Config;
import org.dockit.dockitserver.config.ConfigConstants;
import org.dockit.dockitserver.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesManager.class);

    public static Properties readProperties(String path, String fileName) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = new FileInputStream(path + fileName);
            properties.load(inputStream);

        } catch(IOException e) {
            logger.error("Could not find a file {} in path {} containing the properties!", fileName, path);
        }
        return properties;
    }

    public static Config generateConfigFromProperties(Properties properties) {
        Config.ConfigBuilder configBuilder = new Config.ConfigBuilder();
        return configBuilder
                .setMaxAgentSize(Integer.parseInt(PropertiesManager
                        .getProperty(ConfigConstants.MAX_AGENT_AMOUNT.toString(), properties)))
                .setMaxDBCacheSize(Integer.parseInt(PropertiesManager
                        .getProperty(ConfigConstants.DB_CONNECTION_CACHE_SIZE.toString(), properties)))
                .setKeyStorePassword(PropertiesManager.getProperty(ConfigConstants.KEYSTORE_PASSWORD.toString(), properties))
                .build();
    }

    public static String getProperty(String propertyName, Properties properties) {
        return properties.getProperty(propertyName);
    }
}