package org.dockit.dockitagent.jobs;

import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.entity.Audit;
import org.dockit.dockitagent.entity.AuditConstructor;
import org.dockit.dockitagent.entity.CategoryGenerator;
import org.dockit.dockitagent.exceptions.entity.AuditBuildingException;
import org.dockit.dockitagent.exceptions.jobs.JobException;
import org.dockit.dockitagent.sender.AuditSender;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * A collection job to collect the audit data
 */
public class CollectJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(CollectJob.class);

    /**
     * Take a {@link Collector} instance, {@link AuditSender} and {@link AuditConstructor} as input using
     * the {@link JobDataMap}, collect the data using the collector, construct the audit, and send it to the server.
     *
     * @param context contains details about the job and utility classes in {@link JobDataMap} to be used
     * @throws JobExecutionException if any exception occurs with the execution of the job
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        // Get required utility classes from job data map
        Collector collector = (Collector) jobDataMap.get(JobConstants.COLLECTOR);
        AuditSender auditSender = (AuditSender) jobDataMap.get(JobConstants.SENDER);
        AuditConstructor auditConstructor = (AuditConstructor) jobDataMap.get(JobConstants.CONSTRUCTOR);

        try {
            String collectedData = collector.collect();
            Optional<Audit> audit = auditConstructor
                    .construct(collectedData, CategoryGenerator.getCategory(collector.getClass()));
            if (audit.isEmpty()) {
                throw new AuditBuildingException("Could not construct the audit!");
            }
            if (!auditSender.send(audit.get())) {
                throw new JobException();
            };
        } catch (AuditBuildingException e) {
            logger.error("Failed with building the audit, aborting the job!");
            JobExecutionException jobException = new JobExecutionException(e);
            jobException.setUnscheduleAllTriggers(true);
            throw jobException;
        } catch (JobException e) {
            logger.error("Failed when sending the audit, trying again");
            JobExecutionException jobException = new JobExecutionException(e);
            jobException.setRefireImmediately(true);
            throw jobException;
        }
    }
}
