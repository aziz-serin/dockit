package org.dockit.dockitagent.collector.vm;

import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.vm.collectors.VmCpuUsageCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmDiskUsageCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmFileSystemCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmMemoryResourcesCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmNetworkUsageCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmProcessesCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmUsersCollector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VMInformationCollectorRegistryTest {

    @Test
    public void registryContainsAllCollectors() {
        List<Class<? extends Collector>> vmCollectors = VMInformationCollectorRegistry.vmCollectors;

        assertThat(vmCollectors).containsExactly(
                VmCpuUsageCollector.class,
                VmDiskUsageCollector.class,
                VmFileSystemCollector.class,
                VmMemoryResourcesCollector.class,
                VmNetworkUsageCollector.class,
                VmProcessesCollector.class,
                VmUsersCollector.class
        );
    }
}