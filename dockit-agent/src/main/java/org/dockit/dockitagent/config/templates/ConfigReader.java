package org.dockit.dockitagent.config.templates;

import org.dockit.dockitagent.exceptions.config.ConfigException;

import java.util.Optional;
import java.util.Properties;

public interface ConfigReader {
    Optional<Properties> read(String path);
    void generateConfig(Properties properties) throws ConfigException;
}
