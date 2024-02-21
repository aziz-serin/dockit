package org.dockit.dockitserver.analyze;

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
}
