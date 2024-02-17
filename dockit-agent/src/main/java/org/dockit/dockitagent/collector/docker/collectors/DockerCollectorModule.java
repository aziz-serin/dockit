package org.dockit.dockitagent.collector.docker.collectors;

import com.google.inject.AbstractModule;
import org.dockit.dockitagent.connection.DockerConnectionManager;

/**
 * Guice bindings to be used by docker collectors
 */
public class DockerCollectorModule extends AbstractModule {

    @Override
    public void configure() {
        bind(DockerConnectionManager.class);
    }
}
