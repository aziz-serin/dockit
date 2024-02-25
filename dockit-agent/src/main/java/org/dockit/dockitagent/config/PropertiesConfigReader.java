package org.dockit.dockitagent.config;

import org.dockit.dockitagent.config.templates.ConfigReader;
import org.dockit.dockitagent.exceptions.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.DateTimeException;
import java.time.ZoneId;
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
            boolean vmData = Boolean.parseBoolean((String) properties.get(ConfigConstants.VM_DATA));
            boolean restApi = Boolean.parseBoolean((String) properties.get(ConfigConstants.REST_API));
            String key = (String) properties.get(ConfigConstants.SECRET_KEY);
            String apiKey = (String) properties.get(ConfigConstants.API_KEY);
            String id = (String) properties.get(ConfigConstants.ID);
            String zoneId = (String) properties.get(ConfigConstants.ZONE_ID);
            String docker_url = (String) properties.get(ConfigConstants.DOCKER_URL);
            String server_url = (String) properties.get(ConfigConstants.SERVER_URL);
            String vm_id = (String) properties.get(ConfigConstants.VM_ID);

            ZoneId zoneIdObject = ZoneId.of(zoneId);

            Config config = Config.INSTANCE.getInstance();
            config.setINTERVAL(interval);
            config.setDOCKER(docker);
            config.setREST_API(restApi);
            config.setVM_DATA(vmData);
            config.setSECRET_KEY(key);
            config.setAPI_KEY(apiKey);
            config.setID(id);
            config.setZONE_ID(zoneIdObject);
            config.setDOCKER_URL(docker_url);
            config.setSERVER_URL(server_url);
            config.setVM_ID(vm_id);
        } catch (ClassCastException | NumberFormatException | DateTimeException e) {
            logger.error("Could not parse the input properties, check their types are correct!");
            throw new ConfigException();
        }
    }
}
