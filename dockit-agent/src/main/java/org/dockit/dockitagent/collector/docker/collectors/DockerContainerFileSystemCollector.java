package org.dockit.dockitagent.collector.docker.collectors;

import com.google.inject.Inject;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.connection.DockerConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerContainerFileSystemCollector extends DockerCollector implements Collector {
    private static final Logger logger = LoggerFactory.getLogger(DockerContainerFileSystemCollector.class);
    private static final String CONTAINERS_ENDPOINT = "/containers/json";
    private static final String FILE_SYSTEM_ENDPOINT = "/containers/%s/changes";

    private final DockerConnectionManager connectionManager;

    @Inject
    public DockerContainerFileSystemCollector(DockerConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public String collect() {
        return send(connectionManager, CONTAINERS_ENDPOINT, FILE_SYSTEM_ENDPOINT, logger);
    }
}
