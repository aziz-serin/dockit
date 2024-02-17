package org.dockit.dockitagent.collector.docker;

import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.docker.collectors.DockerContainerCollector;
import org.dockit.dockitagent.collector.docker.collectors.DockerContainerFileSystemCollector;
import org.dockit.dockitagent.collector.docker.collectors.DockerContainerProcessCollector;
import org.dockit.dockitagent.collector.docker.collectors.DockerContainerResourceCollector;

import java.util.List;

public final class DockerInformationCollectorRegistry {
    public static final List<Class<? extends Collector>> dockerCollectors = List.of(
            DockerContainerCollector.class,
            DockerContainerFileSystemCollector.class,
            DockerContainerProcessCollector.class,
            DockerContainerResourceCollector.class
    );
}
