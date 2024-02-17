package org.dockit.dockitagent.collector.docker.collectors;

import com.google.inject.Inject;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.connection.ConnectionManager;
import org.dockit.dockitagent.connection.DockerConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerContainerResourceCollector extends DockerCollector implements Collector {
    private static final Logger logger = LoggerFactory.getLogger(DockerContainerResourceCollector.class);
    private static final String CONTAINERS_ENDPOINT = "/containers/json";
    private static final String RESOURCE_ENDPOINT = "/containers/%s/stats?stream=false&one-shot=true";

    private final ConnectionManager connectionManager;

    @Inject
    public DockerContainerResourceCollector(DockerConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public String collect() {
        return send(connectionManager, CONTAINERS_ENDPOINT, RESOURCE_ENDPOINT, logger);
    }
}
