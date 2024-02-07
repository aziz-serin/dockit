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

/**
 * Controller containing the endpoints for audit operations
 */
@RestController
@RequestMapping(path = "/api/audit", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AuditController {
    private final AuditService auditService;
    private final AuditDataDecryptFromDatabase decryptor;

    /**
     * @param auditService {@link AuditService} object to be injected
     * @param decryptor {@link AuditDataDecryptFromDatabase} object to be injected
     */
    public AuditController(AuditService auditService, AuditDataDecryptFromDatabase decryptor) {
        this.auditService = auditService;
        this.decryptor = decryptor;
    }

    /**
     * Returns an audit specified given their id
     *
     * @param id id of the Audit
     * @return Response entity containing the response
     */
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

    /**
     * Returns all audits that belong to the same category
     *
     * @param category category string to filter through audits
     * @return Response entity containing the response
     */
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

    /**
     * Returns all audits that belong to the same vmId
     *
     * @param vmId vmId string to filter through audits
     * @return Response entity containing the response
     */
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

    /**
     * Returns all audits with same vmId and category
     *
     * @param vmId vmId string to filter through audits
     * @param category category string to filter through audits
     * @return Response entity containing the response
     */
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

    /**
     * Returns all audits with the same category in specified order
     *
     * @param isAscending specify the order to be sorted
     * @param category category string to filter through audits
     * @return Response entity containing the response
     */
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

    /**
     * Returns all audits with the same vmId in specified order
     *
     * @param isAscending specify the order to be sorted
     * @param vmId vmId string to filter through audits
     * @return Response entity containing the response
     */
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

    /**
     * Returns all audits sorted in specified order
     *
     * @param isAscending specify the order to be sorted
     * @return Response entity containing the response
     */
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

    /**
     * Deletes all given audits using their ids
     *
     * @param ids {@link List} of ids to be deleted
     * @return Response entity containing the response
     */
    @DeleteMapping("/byIds")
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR')")
    public ResponseEntity<?> deleteAllByIds(@RequestParam(name = "ids") @NonNull List<String> ids) {
        List<UUID> uuids = ids.stream()
                .map(UUID::fromString)
                .toList();
        auditService.deleteAllById(uuids);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes specified audits using their id
     *
     * @param id id of the audit
     * @return Response entity containing the response
     */
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('SUPER', 'EDITOR')")
    public ResponseEntity<?> delete(@RequestParam(name = "id") @NonNull String id) {
        UUID uuid = UUID.fromString(id);
        auditService.deleteById(uuid);
        return ResponseEntity.ok().build();
    }
}
