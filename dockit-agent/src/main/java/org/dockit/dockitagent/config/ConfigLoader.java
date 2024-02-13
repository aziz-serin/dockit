package org.dockit.dockitagent.config;

import com.google.inject.Inject;
import org.dockit.dockitagent.config.templates.ConfigReader;
import org.dockit.dockitagent.exceptions.config.ConfigException;

import java.util.Optional;
import java.util.Properties;

/**
 * Utility class used to read the configuration of the application
 */
public class ConfigLoader {

    private final ConfigReader configReader;

    @Inject
    public ConfigLoader(ConfigReader configReader) {
        this.configReader = configReader;
    }

    /**
     * @param path path to the .properties config file
     * @throws ConfigException is thrown if the path is null or properties are incorrect
     */
    public void readConfig(String path) throws ConfigException {
        if (path == null) {
            throw new ConfigException("Config could not be read!");
        }

        Optional<Properties> properties = configReader.read(path);;
        if (properties.isEmpty()) {
            throw new ConfigException("Config could not be read!");
        }
        // init the Config
        configReader.generateConfig(properties.get());
    }
}
