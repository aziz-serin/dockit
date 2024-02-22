package org.dockit.dockitserver.config;

import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.utils.AlertImportanceConverter;
import org.dockit.dockitserver.exceptions.config.InvalidPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * Utility class to handle property operations.
 */
public class PropertiesManager {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesManager.class);

    /**
     * Read the properties file from the filesystem.
     *
     * @param path path to the properties file
     * @param fileName name of the properties file
     * @return the properties file's contents as a {@link Properties} object.
     */
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

    /**
     * Generate default config and convert it to properties using the constants from the {@link ConfigConstants} class
     *
     * @return {@link Properties} object containing the properties for the application.
     */
    public static Properties generateDefaultProperties() {
        Properties properties = new Properties();
        properties.put(ConfigConstants.MAX_AGENT_AMOUNT.toString(), ConfigConstants.DEFAULT_MAX_AGENT_AMOUNT.toString());
        properties.put(ConfigConstants.AGENT_CACHE_SIZE.toString(), ConfigConstants.DEFAULT_AGENT_CACHE_SIZE.toString());
        properties.put(ConfigConstants.AUDIT_CACHE_SIZE.toString(), ConfigConstants.DEFAULT_AUDIT_CACHE_SIZE.toString());
        properties.put(ConfigConstants.ADMIN_CACHE_SIZE.toString(), ConfigConstants.DEFAULT_ADMIN_CACHE_SIZE.toString());
        properties.put(ConfigConstants.API_KEY_CACHE_SIZE.toString(), ConfigConstants.DEFAULT_API_KEY_CACHE_SIZE.toString());
        properties.put(ConfigConstants.KEYSTORE_PASSWORD.toString(), ConfigConstants.DEFAULT_KEYSTORE_PASSWORD.toString());
        properties.put(ConfigConstants.JWT_ISSUER.toString(), ConfigConstants.DEFAULT_JWT_ISSUER.toString());
        properties.put(ConfigConstants.JWT_SECRET_ALIAS.toString(), ConfigConstants.DEFAULT_JWT_SECRET_ALIAS.toString());
        properties.put(ConfigConstants.JWT_EXPIRATION_TIME.toString(), ConfigConstants.DEFAULT_JWT_EXPIRATION_TIME.toString());
        properties.put(ConfigConstants.IMPORTANCE.toString(), ConfigConstants.DEFAULT_IMPORTANCE.toString());
        properties.put(ConfigConstants.SENDING_MAIL_ADDRESS.toString(), ConfigConstants.DEFAULT_SENDING_MAIL_ADDRESS.toString());
        return properties;
    }


    /**
     * Creates a {@link Config} object from the properties
     *
     * @param properties containing the application properties
     * @return {@link Config} object generated using the properties.
     * @throws InvalidPropertyException if a property is null or not the required type
     */
    public static Config generateConfigFromProperties(Properties properties) throws InvalidPropertyException {
        String maxAgentCacheSize = properties.getProperty(ConfigConstants.AGENT_CACHE_SIZE.toString());
        String maxAuditCacheSize = properties.getProperty(ConfigConstants.AUDIT_CACHE_SIZE.toString());
        String maxAdminCacheSize = properties.getProperty(ConfigConstants.ADMIN_CACHE_SIZE.toString());
        String maxAccessTokenCacheSize = properties.getProperty(ConfigConstants.AGENT_CACHE_SIZE.toString());
        String maxAgentSize = properties.getProperty(ConfigConstants.MAX_AGENT_AMOUNT.toString());
        String keyStorePassword = properties.getProperty(ConfigConstants.KEYSTORE_PASSWORD.toString());
        String jwtIssuer = properties.getProperty(ConfigConstants.JWT_ISSUER.toString());
        String jwtSecretAlias = properties.getProperty(ConfigConstants.JWT_SECRET_ALIAS.toString());
        String jwtExpirationTime = properties.getProperty(ConfigConstants.JWT_EXPIRATION_TIME.toString());
        Optional<Alert.Importance> importance = AlertImportanceConverter.getImportance(properties
                .getProperty(ConfigConstants.IMPORTANCE.toString()));
        String sendingMailAddress = properties.getProperty(ConfigConstants.SENDING_MAIL_ADDRESS.toString());

        if (isNotInt(maxAgentSize) || isNotInt(maxAgentCacheSize) || isNotInt(maxAuditCacheSize)
                || isNotInt(maxAdminCacheSize) || isNotInt(maxAccessTokenCacheSize) || isNotInt(jwtExpirationTime)
                || importance.isEmpty()) {
            throw new InvalidPropertyException("Invalid property type provided!");
        }
        return ConfigBuilder.newBuilder()
                .maxAgentCacheSize(Long.parseLong(maxAgentCacheSize))
                .maxAuditCacheSize(Long.parseLong(maxAuditCacheSize))
                .maxAdminCacheSize(Long.parseLong(maxAdminCacheSize))
                .maxAccessTokenCacheSize(Long.parseLong(maxAccessTokenCacheSize))
                .maxAgentSize(Integer.parseInt(maxAgentSize))
                .keyStorePassword(keyStorePassword)
                .jwtIssuer(jwtIssuer)
                .jwtSecretAlias(jwtSecretAlias)
                .jwtExpirationTime(Integer.parseInt(jwtExpirationTime))
                .importance(importance.get())
                .sendingMailAddress(sendingMailAddress)
                .build();
    }

    private static boolean isNotInt(String value) {
        if (value == null) {
            return true;
        }
        try {
            Integer.parseInt(value);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}