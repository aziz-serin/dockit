package org.dockit.dockitagent.collector.docker.collectors;

import com.google.inject.Inject;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.connection.DockerConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to collect filesystem changes from running containers
 */
public class DockerContainerFileSystemCollector extends DockerCollector implements Collector {
    private static final Logger logger = LoggerFactory.getLogger(DockerContainerFileSystemCollector.class);
    private static final String CONTAINERS_ENDPOINT = "/containers/json";
    private static final String FILE_SYSTEM_ENDPOINT = "/containers/%s/changes";

    private final DockerConnectionManager connectionManager;

    /**
     * @param connectionManager {@link DockerConnectionManager} instance to be injected
     */
    @Inject
    public DockerContainerFileSystemCollector(DockerConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Collect information about all running docker containers file system changes
     *
     * @return null if there is an error with the request,
     * or string representation of data for container filesystem changes
     */
    @Override
    public String collect() {
        return send(connectionManager, CONTAINERS_ENDPOINT, FILE_SYSTEM_ENDPOINT, logger);
    }
}
