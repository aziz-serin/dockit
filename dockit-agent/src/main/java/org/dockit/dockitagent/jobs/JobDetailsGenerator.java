package org.dockit.dockitagent.jobs;

import com.google.inject.Inject;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.initialiser.CollectorInitialiser;
import org.dockit.dockitagent.entity.AuditConstructor;
import org.dockit.dockitagent.exceptions.collector.CollectorInitialisationException;
import org.dockit.dockitagent.sender.AuditSender;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;

/**
 * Utility class to generate job details
 */
public class JobDetailsGenerator {

    private final List<Collector> collectors;
    private final AuditSender auditSender;
    private final AuditConstructor auditConstructor;

    /**
     * @param collectorInitialiser {@link CollectorInitialiser} to be injected
     * @param auditSender {@link AuditSender} to be injected
     * @param auditConstructor {@link AuditConstructor} to be injected
     * @throws CollectorInitialisationException
     */
    @Inject
    public JobDetailsGenerator(CollectorInitialiser collectorInitialiser, AuditSender auditSender,
                               AuditConstructor auditConstructor) throws CollectorInitialisationException {
        this.collectors = collectorInitialiser.initialiseCollectors();
        this.auditSender = auditSender;
        this.auditConstructor = auditConstructor;
    }

    /**
     * For each initialised collector, generate a {@link JobDetail} and return a list of them
     *
     * @param groupId id to be used when creating job details
     * @return {@link List} of {@link JobDetail} instances
     */
    public List<JobDetail> generate(String groupId) {
        List<JobDetail> jobDetails = new ArrayList<>();

        for (Collector collector : collectors) {
            Map<String, ?> jobData = Map.of(
                    JobConstants.COLLECTOR, collector,
                    JobConstants.SENDER, auditSender,
                    JobConstants.CONSTRUCTOR, auditConstructor);

            JobDetail job = newJob(CollectJob.class)
                    .withIdentity(collector.getClass().getName(), groupId)
                    .setJobData(new JobDataMap(jobData))
                    .withDescription("Job for the collector %s".formatted(collector.getClass().getName()))
                    .build();
            jobDetails.add(job);
        }
        return jobDetails;
    }
}
