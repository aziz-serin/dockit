package org.dockit.dockitserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.security.KeyStore;

/**
 * Component containing the config and keysStore objects to be injected into different parts of the code
 */
@Component
public class ConfigContainer {
    private final Config config;
    private final KeyStore keyStore;

    /**
     * Load the config and keystore if it exists, if not, generate them and cache them in this object.
     *
     * @param environment {@link Environment} containing config from application.properties
     */
    @Autowired
    public ConfigContainer(Environment environment) {
        ConfigManager configManager = new ConfigManager();
        String configFileName = environment.getProperty("dockit.server.config.file.name");
        String keyStoreName = environment.getProperty("dockit.server.config.keystore.name");
        String path = environment.getProperty("dockit.server.config.directory.path");
        if (configManager.configExists(path, configFileName, keyStoreName)) {
            configManager.loadConfig(path, configFileName, keyStoreName);
        } else {
            configManager.createConfig(path,
                    ConfigConstants.DEFAULT_PROPERTIES_NAME.toString(),
                    ConfigConstants.DEFAULT_KEYSTORE_NAME.toString(),
                    ConfigConstants.DEFAULT_KEYSTORE_PASSWORD.toString(),
                    PropertiesManager.generateDefaultProperties());
        }
        this.config = configManager.getConfig();
        this.keyStore = configManager.getKeyStore();
    }

    /**
     * @return {@link Config} object
     */
    public Config getConfig() {
        return this.config;
    }


    /**
     * @return {@link KeyStore} object
     */
    public KeyStore getKeyStore() {
        return this.keyStore;
    }
}
