package org.dockit.dockitserver.utils;

import org.dockit.dockitserver.config.Config;
import org.dockit.dockitserver.config.ConfigConstants;
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

    // TODO: Reject wrong type of config e.g. string for max agent size, it needs to be numbers, and throw exception
    public static Config generateConfigFromProperties(Properties properties) {
        Config.ConfigBuilder configBuilder = new Config.ConfigBuilder();
        return configBuilder
                .setMaxAgentSize(Integer.parseInt(properties.getProperty(ConfigConstants.MAX_AGENT_AMOUNT.toString())))
                .setMaxDBCacheSize(Integer.parseInt(properties.getProperty(ConfigConstants.DB_CONNECTION_CACHE_SIZE.toString())))
                .setKeyStorePassword(properties.getProperty(ConfigConstants.KEYSTORE_PASSWORD.toString()))
                .build();
    }
}