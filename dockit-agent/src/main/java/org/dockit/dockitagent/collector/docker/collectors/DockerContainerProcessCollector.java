package org.dockit.dockitagent.collector.docker.collectors;

import com.google.inject.Inject;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.connection.DockerConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to collect information about running processes from containers running in docker
 */
public class DockerContainerProcessCollector extends DockerCollector implements Collector {
    private static final Logger logger = LoggerFactory.getLogger(DockerContainerProcessCollector.class);
    private static final String CONTAINERS_ENDPOINT = "/containers/json";
    private static final String PROCESS_ENDPOINT = "/containers/%s/top";

    private final DockerConnectionManager connectionManager;

    /**
     * @param connectionManager {@link DockerConnectionManager} instance to be injected
     */
    @Inject
    public DockerContainerProcessCollector(DockerConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Collect information about processes running in containers
     *
     * @return null if there is an error with the request,
     * or string representation of data for container processes information
     */
    @Override
    public String collect() {
        return send(connectionManager, CONTAINERS_ENDPOINT, PROCESS_ENDPOINT, logger);
    }
}
