package org.dockit.dockitagent.jobs;

/**
 * Class containing some constants to be used when creating {@link org.quartz.Job} and {@link org.quartz.Trigger}
 */
public final class JobConstants {
    public static final String COLLECTOR = "collector";
    public static final String SENDER = "auditSender";
    public static final String CONSTRUCTOR = "auditConstructor";
    public static final String GROUP_ID = "collectorJobs";
}
