package org.dockit.dockitagent.collector.docker.collectors;

import com.google.inject.Inject;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.connection.DockerConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to collect information about resource usage of containers
 */
public class DockerContainerResourceCollector extends DockerCollector implements Collector {
    private static final Logger logger = LoggerFactory.getLogger(DockerContainerResourceCollector.class);
    private static final String CONTAINERS_ENDPOINT = "/containers/json";
    private static final String RESOURCE_ENDPOINT = "/containers/%s/stats?stream=false&one-shot=true";

    private final DockerConnectionManager connectionManager;

    /**
     * @param connectionManager {@link DockerConnectionManager} instance to be injected
     */
    @Inject
    public DockerContainerResourceCollector(DockerConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Collect information about all running docker containers
     *
     * @return null if there is an error with the request,
     * or string representation of data for container resource usages
     */
    @Override
    public String collect() {
        return send(connectionManager, CONTAINERS_ENDPOINT, RESOURCE_ENDPOINT, logger);
    }
}
