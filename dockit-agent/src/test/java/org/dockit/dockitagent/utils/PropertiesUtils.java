package org.dockit.dockitagent.utils;

import org.dockit.dockitagent.config.ConfigConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {
    /**
     * Generate and save the created properties file
     *
     * @param path Path string representing root directory
     * @param configFileName Name of the config file
     * @param properties {@link Properties} containing the config of the application
     * @return {@link Properties} object after its creation
     */
    public static String write(String path, String configFileName, Properties properties) {
        String writePath = path + configFileName;
        try {
            FileOutputStream outputStream = new FileOutputStream(writePath);
            properties.store(outputStream, "");
            return writePath;
        } catch (IOException e) {
            // ignore exception
            return null;
        }
    }

    /**
     * Generate mock properties to be used in testing
     *
     * @return Mock properties to be used in testing
     */
    public static Properties generateMockProperties() {
        Properties properties = new Properties();
        properties.setProperty(ConfigConstants.ID, "given_id");
        properties.setProperty(ConfigConstants.SECRET_KEY, "secret_key");
        properties.setProperty(ConfigConstants.API_KEY, "api_key");
        properties.setProperty(ConfigConstants.DOCKER, "true");
        properties.setProperty(ConfigConstants.VM_DATA, "true");
        properties.setProperty(ConfigConstants.INTERVAL, "5");
        properties.setProperty(ConfigConstants.ZONE_ID, "Europe/London");
        properties.setProperty(ConfigConstants.DOCKER_URL, "http://docker");
        properties.setProperty(ConfigConstants.SERVER_URL, "http://server");
        return properties;
    }
}
