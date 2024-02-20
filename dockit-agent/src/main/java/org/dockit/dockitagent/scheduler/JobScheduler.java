package org.dockit.dockitagent.scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.dockit.dockitagent.config.templates.Container;
import org.dockit.dockitagent.jobs.JobConstants;
import org.dockit.dockitagent.jobs.JobDetailsGenerator;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Class to handle the scheduling of jobs
 */
@Singleton
public final class JobScheduler {
    private final Scheduler scheduler;
    private final JobDetailsGenerator jobDetailsGenerator;
    private final Container container;

    /**
     * @param jobDetailsGenerator {@link JobDetailsGenerator} instance to be injected
     * @param container {@link org.dockit.dockitagent.config.ConfigContainer} instance to be injected
     * @throws SchedulerException is thrown in rare occasions if there is a problem with scheduling
     */
    @Inject
    public JobScheduler(JobDetailsGenerator jobDetailsGenerator, Container container) throws SchedulerException {
        this.jobDetailsGenerator = jobDetailsGenerator;
        this.container = container;
        StdSchedulerFactory factory = new StdSchedulerFactory();
        scheduler = factory.getScheduler();
    }

    /**
     * Schedule jobs by first creating all the job details and associated triggers, then adding them to a
     * {@link Scheduler} instance to be used
     *
     * @throws SchedulerException is thrown in rare occasions if there is a problem with scheduling
     */
    public void scheduleJobs() throws SchedulerException {
        List<JobDetail> jobDetails = jobDetailsGenerator.generate(JobConstants.GROUP_ID);
        List<Trigger> triggers = new ArrayList<>();

        for (JobDetail jobDetail : jobDetails) {
            triggers.add(generateTriggerForJob(jobDetail));
        }

        scheduleJobs(jobDetails, triggers);
    }

    /**
     * Stop all the running jobs
     *
     * @throws SchedulerException is thrown in rare occasions if there is a problem with scheduling
     */
    public void startJobs() throws SchedulerException {
        scheduler.start();
    }

    /**
     * Shutdown all the running jobs
     *
     * @throws SchedulerException is thrown in rare occasions if there is a problem with scheduling
     */
    public void stopJobs() throws SchedulerException {
        scheduler.shutdown(true);
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    private void scheduleJobs(List<JobDetail> jobDetails, List<Trigger> triggers) throws SchedulerException {
        for (int i = 0; i < jobDetails.size(); i++) {
            scheduler.scheduleJob(jobDetails.get(i), triggers.get(i));
        }
    }

    private Trigger generateTriggerForJob(JobDetail jobDetail) {
        JobKey key = jobDetail.getKey();
        return newTrigger()
                .withIdentity(key.getName(), key.getGroup())
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(container.getConfig().getINTERVAL())
                        .repeatForever())
                .build();
    }
}
