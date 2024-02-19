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

public class JobDetailsGenerator {

    private final List<Collector> collectors;
    private final AuditSender auditSender;
    private final AuditConstructor auditConstructor;

    @Inject
    public JobDetailsGenerator(CollectorInitialiser collectorInitialiser, AuditSender auditSender,
                               AuditConstructor auditConstructor) throws CollectorInitialisationException {
        this.collectors = collectorInitialiser.initialiseCollectors();
        this.auditSender = auditSender;
        this.auditConstructor = auditConstructor;
    }

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
