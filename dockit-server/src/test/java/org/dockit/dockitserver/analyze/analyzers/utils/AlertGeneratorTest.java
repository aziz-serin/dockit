package org.dockit.dockitserver.analyze.analyzers.utils;

import org.dockit.dockitserver.analyze.AuditCategories;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AlertGeneratorTest {

    private static final String VM_ID = "vmId";
    private static final String CATEGORY = AuditCategories.VM_CPU;
    private static final LocalDateTime TIME_STAMP = LocalDateTime.now();
    private static final String MESSAGE = "some_message";
    private static final Alert.Importance IMPORTANCE = Alert.Importance.CRITICAL;


    private final AlertGenerator alertGenerator = new AlertGenerator();

    @Mock
    private Audit audit;
    @Mock
    private Agent agent;

    @Test
    public void generateAlertGeneratesEmptyGivenInvalidAudit() {
        when(audit.getVmId()).thenReturn(null);
        when(audit.getCategory()).thenReturn(CATEGORY);
        when(audit.getAgent()).thenReturn(agent);
        when(audit.getTimeStamp()).thenReturn(TIME_STAMP);

        Optional<Alert> alert = alertGenerator.generateAlert(audit, IMPORTANCE, MESSAGE);
        assertThat(alert).isEmpty();
    }

    @Test
    public void generateAlertGeneratesAlert() {
        when(audit.getVmId()).thenReturn(VM_ID);
        when(audit.getCategory()).thenReturn(CATEGORY);
        when(audit.getAgent()).thenReturn(agent);
        when(audit.getTimeStamp()).thenReturn(TIME_STAMP);

        Optional<Alert> alert = alertGenerator.generateAlert(audit, IMPORTANCE, MESSAGE);
        assertThat(alert).isPresent();
        assertThat(alert.get().getImportance()).isEqualTo(IMPORTANCE);
        assertThat(alert.get().getAgent()).isEqualTo(agent);
        assertThat(alert.get().getAuditTimeStamp()).isEqualTo(TIME_STAMP);
        assertThat(alert.get().getMessage()).contains(MESSAGE);
    }
}
