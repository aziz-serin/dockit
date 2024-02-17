package org.dockit.dockitagent.collector.docker.collectors;

import com.google.inject.Inject;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.connection.ConnectionManager;

import java.util.Optional;

public class DockerContainerCollector implements Collector {
    private static final String ENDPOINT = "/containers/json";

    private final ConnectionManager connectionManager;

    @Inject
    public DockerContainerCollector(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public String collect() {
        Optional<String> response = connectionManager.sendRequest(ENDPOINT);
        return response.orElse(null);
    }
}
