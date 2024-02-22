package org.dockit.dockitserver.analyze;

import java.util.List;

/**
 * Class containing all the categories sent by the dockit-agent
 */
public class AuditCategories {
    public static final String DOCKER_CONTAINERS = "containers";
    public static final String DOCKER_CONTAINER_FILESYSTEM = "container_filesystem";
    public static final String DOCKER_CONTAINER_PROCESS = "container_process";
    public static final String DOCKER_CONTAINER_RESOURCE = "container_resource";

    // Vm collected categories
    public static final String VM_CPU = "vm_cpu";
    public static final String VM_DISK = "vm_disk";
    public static final String VM_FILESYSTEM = "vm_filesystem";
    public static final String VM_MEMORY = "vm_memory";
    public static final String VM_NETWORK = "vm_network";
    public static final String VM_PROCESS = "vm_process";
    public static final String VM_USERS = "vm_users";

    public static final List<String> CATEGORIES = List.of(DOCKER_CONTAINERS,
            DOCKER_CONTAINER_FILESYSTEM,
            DOCKER_CONTAINER_PROCESS,
            DOCKER_CONTAINER_RESOURCE,
            VM_CPU,
            VM_DISK,
            VM_FILESYSTEM,
            VM_MEMORY,
            VM_NETWORK,
            VM_PROCESS,
            VM_USERS);
}
