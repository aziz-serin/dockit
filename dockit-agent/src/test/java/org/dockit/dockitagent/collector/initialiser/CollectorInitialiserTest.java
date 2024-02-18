package org.dockit.dockitagent.collector.initialiser;

import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.docker.DockerInformationCollectorRegistry;
import org.dockit.dockitagent.collector.vm.VMInformationCollectorRegistry;
import org.dockit.dockitagent.config.Config;
import org.dockit.dockitagent.config.ConfigContainer;
import org.dockit.dockitagent.connection.DockerConnectionManager;
import org.dockit.dockitagent.exceptions.collector.CollectorInitialisationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectorInitialiserTest {
    @Mock
    private DockerConnectionManager dockerConnectionManager;
    @Mock
    private ConfigContainer configContainer;
    @Mock
    private Config config;

    @Test
    public void collectorInitialiserThrowsExceptionGivenBothSettingsTurnedOff() {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.isDOCKER()).thenReturn(false);
        when(config.isVM_DATA()).thenReturn(false);

        CollectorInitialiser collectorInitialiser = new CollectorInitialiser(configContainer, dockerConnectionManager);

        assertThrows(CollectorInitialisationException.class, collectorInitialiser::initialiseCollectors);
    }

    @Test
    public void collectorInitialiserReturnsDockerCollectors() throws CollectorInitialisationException {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.isDOCKER()).thenReturn(true);
        when(config.isVM_DATA()).thenReturn(false);

        CollectorInitialiser collectorInitialiser = new CollectorInitialiser(configContainer, dockerConnectionManager);

        List<Collector> collectors =  collectorInitialiser.initialiseCollectors();

        assertThat(getClasses(collectors))
                .containsExactlyInAnyOrderElementsOf(DockerInformationCollectorRegistry.dockerCollectors);
    }

    @Test
    public void collectorInitialiserReturnsVmCollectors() throws CollectorInitialisationException {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.isDOCKER()).thenReturn(false);
        when(config.isVM_DATA()).thenReturn(true);

        CollectorInitialiser collectorInitialiser = new CollectorInitialiser(configContainer, dockerConnectionManager);

        List<Collector> collectors =  collectorInitialiser.initialiseCollectors();

        assertThat(getClasses(collectors))
                .containsExactlyInAnyOrderElementsOf(VMInformationCollectorRegistry.vmCollectors);
    }

    @Test
    public void collectorInitialiserReturnsVmAndDockerCollectors() throws CollectorInitialisationException {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.isDOCKER()).thenReturn(true);
        when(config.isVM_DATA()).thenReturn(true);

        CollectorInitialiser collectorInitialiser = new CollectorInitialiser(configContainer, dockerConnectionManager);

        List<Collector> collectors =  collectorInitialiser.initialiseCollectors();
        List<Class<? extends Collector>> classes = Stream.concat(VMInformationCollectorRegistry.vmCollectors.stream(),
                DockerInformationCollectorRegistry.dockerCollectors.stream()).toList();

        assertThat(getClasses(collectors))
                .containsExactlyInAnyOrderElementsOf(classes);
    }

    private List<Class<? extends Collector>> getClasses(List<Collector> collectors) {
        return collectors.stream().map(collector -> collector.getClass())
                .collect(Collectors.toList());
    }

}
