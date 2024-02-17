package org.dockit.dockitagent.collector.docker;

import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.docker.collectors.DockerContainerCollector;
import org.dockit.dockitagent.collector.docker.collectors.DockerContainerFileSystemCollector;
import org.dockit.dockitagent.collector.docker.collectors.DockerContainerProcessCollector;
import org.dockit.dockitagent.collector.docker.collectors.DockerContainerResourceCollector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DockerInformationCollectorRegistryTest {
    @Test
    public void registryContainsAllCollectors() {
        List<Class<? extends Collector>> dockerCollectors = DockerInformationCollectorRegistry.dockerCollectors;

        assertThat(dockerCollectors).containsExactly(
                DockerContainerCollector.class,
                DockerContainerFileSystemCollector.class,
                DockerContainerProcessCollector.class,
                DockerContainerResourceCollector.class
        );
    }
}
