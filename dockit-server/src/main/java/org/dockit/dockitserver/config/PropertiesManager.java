package org.dockit.dockitserver.config;

import org.dockit.dockitserver.exceptions.config.InvalidPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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

    public static Properties generateDefaultProperties() {
        Properties properties = new Properties();
        properties.put(ConfigConstants.MAX_AGENT_AMOUNT.toString(), ConfigConstants.DEFAULT_MAX_AGENT_AMOUNT.toString());
        properties.put(ConfigConstants.AGENT_CACHE_SIZE.toString(), ConfigConstants.DEFAULT_AGENT_CACHE_SIZE.toString());
        properties.put(ConfigConstants.AUDIT_CACHE_SIZE.toString(), ConfigConstants.DEFAULT_AUDIT_CACHE_SIZE.toString());
        properties.put(ConfigConstants.ADMIN_CACHE_SIZE.toString(), ConfigConstants.DEFAULT_ADMIN_CACHE_SIZE.toString());
        properties.put(ConfigConstants.ACCESS_TOKEN_CACHE_SIZE.toString(), ConfigConstants.DEFAULT_ACCESS_TOKEN_CACHE_SIZE.toString());
        properties.put(ConfigConstants.KEYSTORE_PASSWORD.toString(), ConfigConstants.DEFAULT_KEYSTORE_PASSWORD.toString());
        return properties;
    }

    public static Config generateConfigFromProperties(Properties properties) throws InvalidPropertyException {
        String maxAgentCacheSize = properties.getProperty(ConfigConstants.AGENT_CACHE_SIZE.toString());
        String maxAuditCacheSize = properties.getProperty(ConfigConstants.AUDIT_CACHE_SIZE.toString());
        String maxAdminCacheSize = properties.getProperty(ConfigConstants.ADMIN_CACHE_SIZE.toString());
        String maxAccessTokenCacheSize = properties.getProperty(ConfigConstants.AGENT_CACHE_SIZE.toString());
        String maxAgentSize = properties.getProperty(ConfigConstants.MAX_AGENT_AMOUNT.toString());
        String keyStorePassword = properties.getProperty(ConfigConstants.KEYSTORE_PASSWORD.toString());
        if (isNotInt(maxAgentSize) || isNotInt(maxAgentCacheSize) || isNotInt(maxAuditCacheSize)
                || isNotInt(maxAdminCacheSize) || isNotInt(maxAccessTokenCacheSize)) {
            throw new InvalidPropertyException("Invalid property type provided!");
        }
        return ConfigBuilder.newBuilder()
                .maxAgentCacheSize(Long.parseLong(maxAgentCacheSize))
                .maxAuditCacheSize(Long.parseLong(maxAuditCacheSize))
                .maxAdminCacheSize(Long.parseLong(maxAdminCacheSize))
                .maxAccessTokenCacheSize(Long.parseLong(maxAccessTokenCacheSize))
                .maxAgentSize(Integer.parseInt(maxAgentSize))
                .keyStorePassword(keyStorePassword)
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