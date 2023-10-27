package org.dockit.dockitserver.utils;

import org.dockit.dockitserver.config.Config;
import org.dockit.dockitserver.config.ConfigConstants;
import org.dockit.dockitserver.exceptions.config.InvalidPropertyException;
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

    public static Config generateConfigFromProperties(Properties properties) throws InvalidPropertyException {
        Config.ConfigBuilder configBuilder = new Config.ConfigBuilder();
        String maxAgentSize = properties.getProperty(ConfigConstants.MAX_AGENT_AMOUNT.toString());
        String maxDBCacheSize = properties.getProperty(ConfigConstants.DB_CONNECTION_CACHE_SIZE.toString());
        String keyStorePassword = properties.getProperty(ConfigConstants.KEYSTORE_PASSWORD.toString());
        if (isNotInt(maxAgentSize) || isNotInt(maxDBCacheSize)) {
            throw new InvalidPropertyException("Invalid property type provided!");
        }
        return configBuilder
                .setMaxAgentSize(Integer.parseInt(maxAgentSize))
                .setMaxDBCacheSize(Integer.parseInt(maxDBCacheSize))
                .setKeyStorePassword(keyStorePassword)
                .build();
    }

    private static boolean isNotInt(String value) {
        if (value == null) {
            return true;
        }
        try {
            int i = Integer.parseInt(value);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}