package org.dockit.dockitagent.connection;

import com.google.inject.AbstractModule;

/**
 * Guice bindings for connection classes
 */
public class ConnectionModule extends AbstractModule {

    @Override
    public void configure() {
        bind(DockerConnectionManager.class);
        bind(ServerConnectionManager.class);
    }
}
