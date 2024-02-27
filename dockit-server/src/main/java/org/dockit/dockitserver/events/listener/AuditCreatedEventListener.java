package org.dockit.dockitserver.events.listener;

import org.dockit.dockitserver.analyze.AuditAnalyzer;
import org.dockit.dockitserver.analyze.AuditCategories;
import org.dockit.dockitserver.controllers.utils.AuditDataDecryptFromDatabase;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.events.event.AuditCreationEvent;
import org.dockit.dockitserver.exceptions.encryption.EncryptionException;
import org.dockit.dockitserver.exceptions.security.key.KeyStoreException;
import org.dockit.dockitserver.services.templates.AlertService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Listener class for the creation of {@link AuditCreationEvent}
 */
@Component
public class AuditCreatedEventListener {

    private final AuditAnalyzer auditAnalyzer;
    private final AuditDataDecryptFromDatabase decrypt;
    private final AlertService alertService;

    /**
     * @param auditAnalyzer {@link AuditAnalyzer} instance to be injected
     * @param decrypt {@link AuditDataDecryptFromDatabase} instance to be injected
     * @param alertService {@link AlertService} instance to be injected
     */
    public AuditCreatedEventListener(AuditAnalyzer auditAnalyzer, AuditDataDecryptFromDatabase decrypt,
                                     AlertService alertService) {
        this.auditAnalyzer = auditAnalyzer;
        this.decrypt = decrypt;
        this.alertService = alertService;
    }

    /**
     * Decrypt and analyze the saved audit object, generate alerts if necessary and save them
     *
     * @param auditCreationEvent {@link AuditCreationEvent} instance containing the created Audit
     * @throws EncryptionException if decryption fails
     * @throws KeyStoreException if db key cannot be found
     */
    @EventListener
    public void auditCreatedEventListener(AuditCreationEvent auditCreationEvent) throws EncryptionException,
            KeyStoreException {
        Audit audit = auditCreationEvent.audit();
        if (AuditCategories.CATEGORIES.contains(audit.getCategory())) {
            Audit decryptedAudit = decrypt.decryptAudit(audit);
            List<Alert> alerts = auditAnalyzer.analyze(decryptedAudit);
            decrypt.encryptAudit(audit);
            alertService.save(alerts);
        }
    }
}
