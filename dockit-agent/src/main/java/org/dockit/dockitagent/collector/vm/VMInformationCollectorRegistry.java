package org.dockit.dockitagent.collector.vm;

import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.vm.collectors.VmCpuUsageCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmDiskUsageCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmFileSystemCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmMemoryResourcesCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmNetworkUsageCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmProcessesCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmUsersCollector;

import java.util.List;

public final class VMInformationCollectorRegistry {
    public static final List<Class<? extends Collector>> vmCollectors = List.of(
            VmCpuUsageCollector.class,
            VmDiskUsageCollector.class,
            VmFileSystemCollector.class,
            VmMemoryResourcesCollector.class,
            VmNetworkUsageCollector.class,
            VmProcessesCollector.class,
            VmUsersCollector.class
    );
}
