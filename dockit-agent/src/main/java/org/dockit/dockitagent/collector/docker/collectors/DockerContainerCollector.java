package org.dockit.dockitagent.collector.docker.collectors;

import com.google.gson.Gson;
import com.google.inject.Inject;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.utils.InformationBuilderHelper;
import org.dockit.dockitagent.connection.DockerConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class to collect information about running containers from docker
 */
public class DockerContainerCollector implements Collector {
    private static final String ENDPOINT = "/containers/json";

    private final DockerConnectionManager connectionManager;

    /**
     * @param connectionManager {@link DockerConnectionManager} instance to be injected
     */
    @Inject
    public DockerContainerCollector(DockerConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Collect information about all running docker containers
     *
     * @return null if there is an error with the request,
     * or string representation of data for container information
     */
    @Override
    public String collect() {
        Optional<String> response = connectionManager.sendRequest(ENDPOINT);
        if (response.isEmpty()) {
            return null;
        }
        Gson gson = new Gson();
        List<Map<String, String>> dataList = gson.fromJson(response.get(), List.class);
        List<String> data = new ArrayList<>();

        for (Map<String, String> dataItem : dataList) {
            data.add(gson.toJson(dataItem));
        }

        return InformationBuilderHelper.build(data);
    }
}
