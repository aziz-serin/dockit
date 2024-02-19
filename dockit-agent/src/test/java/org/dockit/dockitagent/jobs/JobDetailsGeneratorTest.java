package org.dockit.dockitagent.jobs;

import org.dockit.dockitagent.collector.initialiser.CollectorInitialiser;
import org.dockit.dockitagent.collector.vm.collectors.VmUsersCollector;
import org.dockit.dockitagent.entity.Audit;
import org.dockit.dockitagent.entity.AuditConstructor;
import org.dockit.dockitagent.exceptions.collector.CollectorInitialisationException;
import org.dockit.dockitagent.sender.AuditSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobDetail;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobDetailsGeneratorTest {
    private static final String GROUP_ID = "group_id";

    @Mock
    private CollectorInitialiser collectorInitialiser;
    @Mock
    private AuditSender auditSender;
    @Mock
    private AuditConstructor auditConstructor;
    @Mock
    private Audit audit;
    @Mock
    private VmUsersCollector vmUsersCollector;

    @Test
    public void generatedJobsContainsRequiredUtilityClasses() throws CollectorInitialisationException {
        when(collectorInitialiser.initialiseCollectors()).thenReturn(List.of(vmUsersCollector));

        JobDetailsGenerator jobDetailsGenerator = new JobDetailsGenerator(collectorInitialiser, auditSender,
                auditConstructor);
        List<JobDetail> jobDetails = jobDetailsGenerator.generate(GROUP_ID);

        assertThat(jobDetails.size()).isEqualTo(1);
//        assertThat(jobDetails.get(0).getJobClass()).isEqualTo(CollectJob.class);
        assertThat(jobDetails.get(0).getJobDataMap()).containsExactlyInAnyOrderEntriesOf(
                Map.of(
                        JobConstants.COLLECTOR, vmUsersCollector,
                        JobConstants.SENDER, auditSender,
                        JobConstants.CONSTRUCTOR, auditConstructor
                )
        );
    }

    @Test
    public void generatedJobsHaveCorrectDetails() throws  CollectorInitialisationException {
        when(collectorInitialiser.initialiseCollectors()).thenReturn(List.of(vmUsersCollector));

        JobDetailsGenerator jobDetailsGenerator = new JobDetailsGenerator(collectorInitialiser, auditSender,
                auditConstructor);
        List<JobDetail> jobDetails = jobDetailsGenerator.generate(GROUP_ID);

        assertThat(jobDetails.size()).isEqualTo(1);
        assertThat(jobDetails.get(0).getJobClass()).isEqualTo(CollectJob.class);
        assertThat(jobDetails.get(0).getKey().getName()).isEqualTo(vmUsersCollector.getClass().getName());
        assertThat(jobDetails.get(0).getKey().getGroup()).isEqualTo(GROUP_ID);

    }
}
