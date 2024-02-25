package org.dockit.dockitagent;

import org.dockit.dockitagent.config.ConfigContainer;
import org.dockit.dockitagent.guice.ApplicationGuiceInitialiser;
import org.dockit.dockitagent.rest.RestServer;
import org.dockit.dockitagent.scheduler.JobScheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockitAgent {
    private static final Logger logger = LoggerFactory.getLogger(DockitAgent.class);
    public static void main(String[] args) {
        ApplicationGuiceInitialiser applicationGuiceInitialiser = new ApplicationGuiceInitialiser();
        JobScheduler jobScheduler = applicationGuiceInitialiser.getJobScheduler();
        ConfigContainer configContainer = applicationGuiceInitialiser.getConfig();
        if (configContainer.getConfig().isREST_API()) {
            RestServer restServer = applicationGuiceInitialiser.getRest();
            restServer.setupEndpoints();
        }
        try {
            jobScheduler.scheduleJobs();
            jobScheduler.startJobs();
        } catch (SchedulerException e) {
            logger.error("Failed to schedule the jobs, {}", e.getMessage());
            // Just terminate if they can't be scheduled
        }
    }
}