package org.dockit.dockitagent.collector.docker.collectors;

import org.dockit.dockitagent.collector.utils.GetContainerIds;
import org.dockit.dockitagent.collector.utils.InformationBuilderHelper;
import org.dockit.dockitagent.connection.DockerConnectionManager;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Abstract class to be used in some of the docker collector classes which obtain container-specific data
 */
public abstract class DockerCollector {

    /**
     * @param connectionManager {@link org.dockit.dockitagent.connection.DockerConnectionManager} instance to be used
     * @param id_endpoint endpoint to retrieve container ids
     * @param response_endpoint endpoint to send the request to
     * @param logger {@link Logger} instance to log incidents
     * @return response body in string format
     */
    protected String send(DockerConnectionManager connectionManager, String id_endpoint, String response_endpoint, Logger logger) {
        Optional<String> idResponse = connectionManager.sendRequest(id_endpoint);
        if (idResponse.isEmpty()) {
            return null;
        }
        List<String> ids = GetContainerIds.getContainerIds(idResponse.get());
        List<String> data = new ArrayList<>();

        for(String id : ids) {
            Optional<String> response = connectionManager.sendRequest(response_endpoint.formatted(id));
            if (response.isEmpty()) {
                logger.info("Could not retrieve information for the container {}", id);
                continue;
            }
            data.add(response.get());
        }
        return InformationBuilderHelper.build(data);
    }
}
