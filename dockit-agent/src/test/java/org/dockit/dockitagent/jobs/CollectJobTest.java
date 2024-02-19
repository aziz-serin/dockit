package org.dockit.dockitagent.jobs;

import org.dockit.dockitagent.collector.vm.collectors.VmUsersCollector;
import org.dockit.dockitagent.entity.Audit;
import org.dockit.dockitagent.entity.AuditConstructor;
import org.dockit.dockitagent.sender.AuditSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectJobTest {
    @Mock
    private AuditSender auditSender;
    @Mock
    private AuditConstructor auditConstructor;
    @Mock
    private Audit audit;
    @Mock
    private VmUsersCollector vmUsersCollector;
    @Mock
    private JobDataMap jobDataMap;
    @Mock
    private JobExecutionContext jobExecutionContext;

    @Test
    public void executeFailsGivenFailingToBuildAudit() {
        when(jobExecutionContext.getMergedJobDataMap()).thenReturn(jobDataMap);
        when(jobDataMap.get(JobConstants.COLLECTOR)).thenReturn(vmUsersCollector);
        when(jobDataMap.get(JobConstants.SENDER)).thenReturn(auditSender);
        when(jobDataMap.get(JobConstants.CONSTRUCTOR)).thenReturn(auditConstructor);
        when(auditConstructor.construct(any(), any())).thenReturn(Optional.empty());

        CollectJob collectJob = new CollectJob();

        JobExecutionException jobExecutionException = assertThrows(JobExecutionException.class, () -> {
            collectJob.execute(jobExecutionContext);
        });

        assertTrue(jobExecutionException.unscheduleAllTriggers());
        assertFalse(jobExecutionException.refireImmediately());
    }

    @Test
    public void executeFailsGivenAuditSenderFailure() {
        when(jobExecutionContext.getMergedJobDataMap()).thenReturn(jobDataMap);
        when(jobDataMap.get(JobConstants.COLLECTOR)).thenReturn(vmUsersCollector);
        when(jobDataMap.get(JobConstants.SENDER)).thenReturn(auditSender);
        when(jobDataMap.get(JobConstants.CONSTRUCTOR)).thenReturn(auditConstructor);
        when(auditConstructor.construct(any(), any())).thenReturn(Optional.of(audit));
        when(auditSender.send(any())).thenReturn(false);

        CollectJob collectJob = new CollectJob();

        JobExecutionException jobExecutionException = assertThrows(JobExecutionException.class, () -> {
            collectJob.execute(jobExecutionContext);
        });

        assertTrue(jobExecutionException.refireImmediately());
        assertFalse(jobExecutionException.unscheduleAllTriggers());
    }
}
