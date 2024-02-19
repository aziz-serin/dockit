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

public class CollectJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(CollectJob.class);

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
