package org.dockit.dockitserver.analyze.analyzers.utils;

/**
 * Class containing template messages to be formatted for each alert
 */
public class AlertMessageTemplates {
    public static final String DOCKER_CONTAINER_RESOURCE_MESSAGE = """
            Container running in VM with vmId %s has high resource usage as following: %s.\s

             ALERT LEVEL: %s""";

    public static final String VM_CPU_MESSAGE = """
            VM with vmId %s has high cpu usage as following: %s.\s

             ALERT LEVEL: %s""";

    public static final String VM_FILE_SYSTEM_MESSAGE = """
            VM with vmId %s has high file system usage as following: %s.\s

             ALERT LEVEL: %s""";

    public static final String VM_MEMORY_MESSAGE = """
            VM with vmId %s has high memory usage as following: %s.\s

             ALERT LEVEL: %s""";

    public static final String VM_USERS_MESSAGE = """
            VM with vmId %s detected an intrusion by an unknown user with user_name %s and host %s logged in at %s.\s

             ALERT LEVEL: %s""";
}
