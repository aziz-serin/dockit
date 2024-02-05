package org.dockit.dockitserver.controllers;

import org.dockit.dockitserver.controllers.utils.AuditDataDecryptFromDatabase;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.exceptions.encryption.EncryptionException;
import org.dockit.dockitserver.exceptions.security.key.KeyStoreException;
import org.dockit.dockitserver.services.templates.AuditService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/audit", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AuditController {
    private final AuditService auditService;
    private final AuditDataDecryptFromDatabase decryptor;

    public AuditController(AuditService auditService, AuditDataDecryptFromDatabase decryptor) {
        this.auditService = auditService;
        this.decryptor = decryptor;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getById(@RequestParam(name = "id") @NonNull String id) {
        Optional<Audit> audit = auditService.findById(UUID.fromString(id));
        if (audit.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            return ResponseEntity.ok().body(decryptor.decryptAudit(audit.get()));
        } catch (EncryptionException | KeyStoreException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/category")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getByCategory(@RequestParam(name = "category") @NonNull String category) {
        List<Audit> audits = auditService.findByCategory(category);
        try {
            return ResponseEntity.ok().body(decryptor.decryptAudits(audits));
        } catch (EncryptionException | KeyStoreException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/vmId")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getByVmId(@RequestParam(name = "vmId") @NonNull String vmId) {
        List<Audit> audits = auditService.findByVmId(vmId);
        try {
            return ResponseEntity.ok().body(decryptor.decryptAudits(audits));
        } catch (EncryptionException | KeyStoreException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/categoryVmId")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getByVmIdAndCategory(@RequestParam(name = "vmId") @NonNull String vmId,
                                                  @RequestParam(name = "category") @NonNull String category) {
        List<Audit> audits = auditService.findByCategoryAndVmId(category, vmId);
        try {
            return ResponseEntity.ok().body(decryptor.decryptAudits(audits));
        } catch (EncryptionException | KeyStoreException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sortedCategory")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getByCategorySorted(@RequestParam(name = "isAscending") @NonNull boolean isAscending,
                                                  @RequestParam(name = "category") @NonNull String category) {
        List<Audit> audits;
        if (isAscending) {
            audits = auditService.findAllByCategorySortByTimeStampAscending(category);
        } else {
            audits = auditService.findAllByCategorySortByTimeStampDescending(category);
        }
        try {
            return ResponseEntity.ok().body(decryptor.decryptAudits(audits));
        } catch (EncryptionException | KeyStoreException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sortedVmId")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getByVmIdSorted(@RequestParam(name = "isAscending") @NonNull boolean isAscending,
                                                  @RequestParam(name = "vmId") @NonNull String vmId) {
        List<Audit> audits;
        if (isAscending) {
            audits = auditService.findAllByVmIdSortByTimeStampAscending(vmId);
        } else {
            audits = auditService.findAllByVmIdSortByTimeStampDescending(vmId);
        }
        try {
            return ResponseEntity.ok().body(decryptor.decryptAudits(audits));
        } catch (EncryptionException | KeyStoreException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sorted")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR', 'VIEWER')")
    public ResponseEntity<?> getAllSorted(@RequestParam @NonNull boolean isAscending) {
        List<Audit> audits;
        if (isAscending) {
            audits = auditService.findAllSortByTimeStampAscending();
        } else {
            audits = auditService.findAllSortByTimeStampDescending();
        }
        try {
            return ResponseEntity.ok().body(decryptor.decryptAudits(audits));
        } catch (EncryptionException | KeyStoreException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/byIds")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR')")
    public ResponseEntity<?> deleteAllByIds(@RequestParam(name = "ids") @NonNull List<String> ids) {
        List<UUID> uuids = ids.stream()
                .map(UUID::fromString)
                .toList();
        auditService.deleteAllById(uuids);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR')")
    public ResponseEntity<?> delete(@RequestParam(name = "id") @NonNull String id) {
        UUID uuid = UUID.fromString(id);
        auditService.deleteById(uuid);
        return ResponseEntity.ok().build();
    }
}
