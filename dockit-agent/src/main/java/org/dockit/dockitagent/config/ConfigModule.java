package org.dockit.dockitagent.config;

import com.google.inject.AbstractModule;
import org.dockit.dockitagent.config.templates.ConfigReader;
import org.dockit.dockitagent.config.templates.Container;

/**
 * Guice binding module for configuration
 */
public class ConfigModule extends AbstractModule {
    @Override
    public void configure() {
        bind(Container.class).to(ConfigContainer.class).asEagerSingleton();
        bind(ConfigReader.class).to(PropertiesConfigReader.class);
    }
}
