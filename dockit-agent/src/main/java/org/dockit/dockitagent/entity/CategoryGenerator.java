package org.dockit.dockitagent.entity;

import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.docker.collectors.DockerContainerCollector;
import org.dockit.dockitagent.collector.docker.collectors.DockerContainerFileSystemCollector;
import org.dockit.dockitagent.collector.docker.collectors.DockerContainerProcessCollector;
import org.dockit.dockitagent.collector.docker.collectors.DockerContainerResourceCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmCpuUsageCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmDiskUsageCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmFileSystemCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmMemoryResourcesCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmNetworkUsageCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmProcessesCollector;
import org.dockit.dockitagent.collector.vm.collectors.VmUsersCollector;

/**
 * Utility class to generate categories to be used in the construction of {@link Audit} object
 */
public final class CategoryGenerator {
    // Docker collected categories
    private static final String DOCKER_CONTAINERS = "containers";
    private static final String DOCKER_CONTAINER_FILESYSTEM = "container_filesystem";
    private static final String DOCKER_CONTAINER_PROCESS = "container_process";
    private static final String DOCKER_CONTAINER_RESOURCE = "container_resource";

    // Vm collected categories
    private static final String VM_CPU = "vm_cpu";
    private static final String VM_DISK = "vm_disk";
    private static final String VM_FILESYSTEM = "vm_filesystem";
    private static final String VM_MEMORY = "vm_memory";
    private static final String VM_NETWORK = "vm_network";
    private static final String VM_PROCESS = "vm_process";
    private static final String VM_USERS = "vm_users";


    /**
     * Given the class, return the appropriate category
     *
     * @param clazz the class for the given {@link Collector} instance
     * @return pre-defined category for the given collector
     */
    public static String getCategory(Class<? extends Collector> clazz) {
        if (clazz.equals(DockerContainerCollector.class)) {
            return DOCKER_CONTAINERS;
        } else if (clazz.equals(DockerContainerFileSystemCollector.class)) {
            return DOCKER_CONTAINER_FILESYSTEM;
        } else if (clazz.equals(DockerContainerProcessCollector.class)) {
            return DOCKER_CONTAINER_PROCESS;
        } else if (clazz.equals(DockerContainerResourceCollector.class)) {
            return DOCKER_CONTAINER_RESOURCE;
        } else if (clazz.equals(VmCpuUsageCollector.class)) {
            return VM_CPU;
        } else if (clazz.equals(VmDiskUsageCollector.class)) {
            return VM_DISK;
        } else if (clazz.equals(VmFileSystemCollector.class)) {
            return VM_FILESYSTEM;
        } else if (clazz.equals(VmMemoryResourcesCollector.class)) {
            return VM_MEMORY;
        } else if (clazz.equals(VmNetworkUsageCollector.class)) {
            return VM_NETWORK;
        } else if (clazz.equals(VmProcessesCollector.class)) {
            return VM_PROCESS;
        } else if (clazz.equals(VmUsersCollector.class)) {
            return VM_USERS;
        } else {
            return null;
        }
    }
}
