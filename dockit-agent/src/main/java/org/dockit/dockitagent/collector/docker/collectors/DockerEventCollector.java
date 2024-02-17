package org.dockit.dockitagent.collector.docker.collectors;

import com.google.inject.Inject;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.connection.DockerConnectionManager;

import java.util.Optional;

public class DockerEventCollector implements Collector {
    private static final String ENDPOINT = "/events";

    private final DockerConnectionManager connectionManager;

    @Inject
    public DockerEventCollector(DockerConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public String collect() {
        Optional<String> response = connectionManager.sendRequest(ENDPOINT);
        return response.orElse(null);
    }
}
