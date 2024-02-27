package org.dockit.dockitagent.collector.initialiser;

import com.google.inject.Inject;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.docker.DockerInformationCollectorRegistry;
import org.dockit.dockitagent.collector.vm.VMInformationCollectorRegistry;
import org.dockit.dockitagent.config.templates.Container;
import org.dockit.dockitagent.connection.DockerConnectionManager;
import org.dockit.dockitagent.exceptions.collector.CollectorInitialisationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utility class to be used to initialise different collectors for both VM and Docker
 */
public class CollectorInitialiser {
    private static final Logger logger = LoggerFactory.getLogger(CollectorInitialiser.class);

    private final Container configContainer;

    private final DockerConnectionManager dockerConnectionManager;

    /**
     *
     *
     * @param configContainer {@link org.dockit.dockitagent.config.ConfigContainer} instance to be injected
     * @param dockerConnectionManager {@link DockerConnectionManager} instance to be injected
     */
    @Inject
    public CollectorInitialiser(Container configContainer, DockerConnectionManager dockerConnectionManager) {
        this.configContainer = configContainer;
        this.dockerConnectionManager = dockerConnectionManager;
    }

    /**
     * Initialise collectors from {@link DockerInformationCollectorRegistry} and {@link VMInformationCollectorRegistry}
     * using reflection
     *
     * @return {@link List} of {@link Collector} objects
     * @throws CollectorInitialisationException when there is a problem with initialisation of the object, or both vm
     * and docker config are turned off in {@link org.dockit.dockitagent.config.Config}
     */
    public List<Collector> initialiseCollectors() throws CollectorInitialisationException {
        if (configContainer.getConfig().isDOCKER() && configContainer.getConfig().isVM_DATA()) {
            List<Collector> dockerCollectors = initialiseDockerCollectors();
            List<Collector> vmCollectors = initialiseVmCollectors();
            return Stream.concat(dockerCollectors.stream(), vmCollectors.stream()).toList();
        } else if (configContainer.getConfig().isDOCKER()) {
            return initialiseDockerCollectors();
        } else if (configContainer.getConfig().isVM_DATA()) {
            return initialiseVmCollectors();
        } else {
            logger.info("Both docker and vm settings are turned off," +
                    " nothing to collect!");
            return List.of();
        }
    }

    private List<Collector> initialiseDockerCollectors() throws CollectorInitialisationException {
        return initialiseCollectors(DockerInformationCollectorRegistry.dockerCollectors,
                DockerConnectionManager.class, dockerConnectionManager);
    }

    private List<Collector> initialiseVmCollectors() throws CollectorInitialisationException {
        return initialiseCollectors(VMInformationCollectorRegistry.vmCollectors,
                null, null);
    }

    private <T> List<Collector> initialiseCollectors(List<Class<? extends Collector>> collectors,
                                                     Class<T> clazz, T object) throws CollectorInitialisationException {
        List<Collector> collectorClasses = collectors.stream()
                .map(collectorClass -> {
                    try {
                        // Use reflection to initialize all the registered classes for docker collectors
                        if (clazz != null) {
                            // This is for docker collectors since they need constructor args
                            return collectorClass
                                    .getDeclaredConstructor(clazz)
                                    .newInstance(object);
                        } else {
                            // This is for vm collectors since they do not need constructor args
                            return collectorClass
                                    .getDeclaredConstructor()
                                    .newInstance();
                        }
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                             IllegalAccessException e) {
                        return null;
                    }
                }).toList();
        if (collectorClasses.contains(null)) {
            logger.error("Could not initialise collectors!");
            throw new CollectorInitialisationException();
        }
        return collectorClasses;
    }
}
