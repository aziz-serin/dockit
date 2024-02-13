package org.dockit.dockitagent.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.dockit.dockitagent.config.templates.Container;
import org.dockit.dockitagent.exceptions.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Container class to be injected in other parts of the application
 */
@Singleton
public class ConfigContainer implements Container {
    private static final Logger logger = LoggerFactory.getLogger(ConfigContainer.class);

    private static final String CONFIG_PATH = "CONFIG_PATH";

    private final Config config;
    private final SecretKey key;

    /**
     * Constructor for the container, set config and secret key in this method
     */
    @Inject
    public ConfigContainer(ConfigLoader configLoader) {
        Config temporaryConfig = null;
        SecretKey temporaryKey = null;
        // Close kill the application if they don't exist
        try {
            String path = System.getenv(CONFIG_PATH);
            configLoader.readConfig(path);
            temporaryConfig = Config.INSTANCE.getInstance();
            byte[] decodedKey = Base64.getDecoder().decode(temporaryConfig.getKEY());
            temporaryKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        } catch (SecurityException e) {
            logger.error("Host machine does not allow access to env variables");
            System.exit(-1);
        } catch (ConfigException e) {
            logger.error(e.getMessage());
            System.exit(-1);
        } finally {
            this.config = temporaryConfig;
            this.key = temporaryKey;
        }
    }

    /**
     * @return {@link SecretKey} instance
     */
    @Override
    public SecretKey getKey() {
        return this.key;
    }

    /**
     * @return {@link Config} instance
     */
    public Config getConfig() {
        return this.config;
    }
}
