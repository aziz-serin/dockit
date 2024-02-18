package org.dockit.dockitagent.sender;

import org.dockit.dockitagent.connection.ServerConnectionManager;
import org.dockit.dockitagent.entity.Audit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuditSenderTest {
    private static final String AUDIT_STRING = "string";
    private static final String RESPONSE = "response";

    @Mock
    private ServerConnectionManager serverConnectionManager;
    @Mock
    private Audit audit;

    @Test
    public void sendReturnsFalseGivenServerNotAlive() {
        when(serverConnectionManager.isAlive()).thenReturn(false);

        AuditSender auditSender = new AuditSender(serverConnectionManager);

        assertFalse(auditSender.send(audit));
    }

    @Test
    public void sendReturnsFalseGivenError() {
        when(serverConnectionManager.isAlive()).thenReturn(true);
        when(serverConnectionManager.sendRequest(anyString(), anyString())).thenReturn(Optional.empty());
        when(audit.toMap()).thenReturn(AUDIT_STRING);

        AuditSender auditSender = new AuditSender(serverConnectionManager);

        assertFalse(auditSender.send(audit));
    }

    @Test
    public void sendReturnsTrueGivenError() {
        when(serverConnectionManager.isAlive()).thenReturn(true);
        when(serverConnectionManager.sendRequest(anyString(), anyString())).thenReturn(Optional.of(RESPONSE));
        when(audit.toMap()).thenReturn(AUDIT_STRING);

        AuditSender auditSender = new AuditSender(serverConnectionManager);

        assertTrue(auditSender.send(audit));
    }
}
