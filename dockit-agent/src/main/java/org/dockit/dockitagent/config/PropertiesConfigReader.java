package org.dockit.dockitagent.config;

import org.dockit.dockitagent.config.templates.ConfigReader;
import org.dockit.dockitagent.exceptions.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * Utility class to handle the config properties
 */
public class PropertiesConfigReader implements ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfigReader.class);

    /**
     * Read the properties file from the filesystem.
     *
     * @param path path to the properties file
     * @return the properties file's contents as a {@link Properties} object.
     */
    public Optional<Properties> read(String path) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = new FileInputStream(path);
            properties.load(inputStream);
            return Optional.of(properties);

        } catch(IOException e) {
            logger.error("Could not find the a file in path {} containing the properties!", path);
            return Optional.empty();
        }
    }

    /**
     * Takes a {@link Properties} object and populates {@link Config} using pre-set properties
     * from {@link ConfigConstants}
     *
     * @param properties object containing properties
     * @throws ConfigException if there is a {@link ClassCastException}
     */
    public void generateConfig(Properties properties) throws ConfigException {
        try {
            int interval = Integer.parseInt((String) properties.get(ConfigConstants.INTERVAL));
            boolean docker = Boolean.parseBoolean((String) properties.get(ConfigConstants.DOCKER));
            boolean vm_data = Boolean.parseBoolean((String) properties.get(ConfigConstants.VM_DATA));
            String key = (String) properties.get(ConfigConstants.KEY);
            String id = (String) properties.get(ConfigConstants.ID);

            Config config = Config.INSTANCE.getInstance();
            config.setINTERVAL(interval);
            config.setDOCKER(docker);
            config.setVM_DATA(vm_data);
            config.setKEY(key);
            config.setID(id);
        } catch (ClassCastException | NumberFormatException e) {
            logger.error("Could not parse the input properties, check their types are correct!");
            throw new ConfigException();
        }
    }
}
