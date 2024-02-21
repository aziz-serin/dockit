package org.dockit.dockitserver.services;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.caching.CacheNames;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.services.templates.AgentService;
import org.dockit.dockitserver.services.templates.AuditService;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuditServiceTest {

    private static final String CACHE_NAME = CacheNames.AUDIT;

    @Autowired
    private AuditService auditService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private CacheManager cacheManager;

    private Cache cache;
    private Audit audit1;
    private Audit audit2;
    private Audit audit3;
    private Audit audit4;
    private Agent agent;

    @BeforeAll
    public void setup() {
        agent = EntityCreator.createAgent("name", "password", LocalDateTime.now(),
                LocalDateTime.now(), List.of("")).get();
        agentService.save(agent);

        audit1 = EntityCreator.createAudit("vm1", "resource_usage", LocalDateTime.now(), "data1", agent).get();
        auditService.save(audit1);

        audit2 = EntityCreator.createAudit("vm2", "network_usage", LocalDateTime.now().minusDays(3), "data2", agent).get();
        auditService.save(audit2);

        audit3 = EntityCreator.createAudit("vm2", "running_processes", LocalDateTime.now().minusMinutes(15), "data3", agent).get();
        auditService.save(audit3);

        audit4 = EntityCreator.createAudit("vm4", "resource_usage", LocalDateTime.now().minusHours(5), "data4", agent).get();
        auditService.save(audit4);

        cache = Objects.requireNonNull(cacheManager.getCache(CACHE_NAME));
    }

    @Test
    public void findByIdReturnsEmptyIfItDoesNotExists() {
        assertFalse(auditService.findById(UUID.randomUUID()).isPresent());
    }

    @Test
    public void findByIdReturnsAudit() {
        Optional<Audit> audit = auditService.findById(audit1.getId());

        assertTrue(audit.isPresent());
        assertThat(audit.get().getId()).isEqualTo(audit1.getId());
    }

    @Test
    public void findByVmIdReturnsEmptyListIfItDoesNotExist() {
        List<Audit> audits = auditService.findByVmId("vm999");

        assertThat(audits).isEmpty();
    }

    @Test
    public void findByVmIdReturnsAudits() {
        List<Audit> audits = auditService.findByVmId(audit2.getVmId());

        assertThat(audits).hasSize(2);
        assertThat(audits.get(0).getId()).isEqualTo(audit2.getId());
        assertThat(audits.get(1).getId()).isEqualTo(audit3.getId());
    }

    @Test
    public void findByCategoryReturnsEmptyListIfItDoesNotExist() {
        List<Audit> audits = auditService.findByCategory("category");

        assertThat(audits).isEmpty();
    }

    @Test
    public void findByCategoryReturnsAudits() {
        List<Audit> audits = auditService.findByCategory(audit2.getCategory());

        assertThat(audits).hasSize(1);
        assertThat(audits.get(0).getId()).isEqualTo(audit2.getId());
    }

    @Test
    public void findByCategoryAndVmIdReturnsEmptyIfItDoesNotExist() {
        List<Audit> audits = auditService.findByCategoryAndVmId("category", "vmId");

        assertThat(audits).hasSize(0);
    }

    @Test
    public void findByCategoryAndVmIdReturnsAudits() {
        List<Audit> audits = auditService.findByCategoryAndVmId(audit1.getCategory(), audit1.getVmId());

        assertThat(audits).hasSize(1);
        assertThat(audits.get(0).getVmId()).isEqualTo(audit1.getVmId());
        assertThat(audits.get(0).getCategory()).isEqualTo(audit1.getCategory());
    }

    @Test
    public void findAllSortByTimeStampAscendingReturnsAscendingList() {
        List<Audit> audits = auditService.findAllSortByTimeStampAscending();

        assertThat(audits).hasSize(4);
        assertThat(audits.get(0).getId()).isEqualTo(audit2.getId());
        assertThat(audits.get(1).getId()).isEqualTo(audit4.getId());
        assertThat(audits.get(2).getId()).isEqualTo(audit3.getId());
        assertThat(audits.get(3).getId()).isEqualTo(audit1.getId());
    }

    @Test
    public void findAllSortByTimeStampDescendingReturnsDescendingList() {
        List<Audit> audits = auditService.findAllSortByTimeStampDescending();

        assertThat(audits).hasSize(4);
        assertThat(audits.get(0).getId()).isEqualTo(audit1.getId());
        assertThat(audits.get(1).getId()).isEqualTo(audit3.getId());
        assertThat(audits.get(2).getId()).isEqualTo(audit4.getId());
        assertThat(audits.get(3).getId()).isEqualTo(audit2.getId());
    }

    @Test
    public void findAllByCategorySortByTimeStampAscendingReturnsAscendingList() {
        List<Audit> audits = auditService.findAllByCategorySortByTimeStampAscending(audit1.getCategory());

        assertThat(audits).hasSize(2);
        assertThat(audits.get(0).getId()).isEqualTo(audit4.getId());
        assertThat(audits.get(1).getId()).isEqualTo(audit1.getId());
    }

    @Test
    public void findAllByCategorySortByTimeStampDescendingReturnsDescendingList() {
        List<Audit> audits = auditService.findAllByCategorySortByTimeStampDescending(audit1.getCategory());

        assertThat(audits).hasSize(2);
        assertThat(audits.get(0).getId()).isEqualTo(audit1.getId());
        assertThat(audits.get(1).getId()).isEqualTo(audit4.getId());
    }

    @Test
    public void findAllByVmIdSortByTimeStampAscendingReturnsAscendingList() {
        List<Audit> audits = auditService.findAllByVmIdSortByTimeStampAscending(audit2.getVmId());

        assertThat(audits).hasSize(2);
        assertThat(audits.get(0).getId()).isEqualTo(audit2.getId());
        assertThat(audits.get(1).getId()).isEqualTo(audit3.getId());
    }

    @Test
    public void findAllByVmIdSortByTimeStampDescendingReturnsDescendingList() {
        List<Audit> audits = auditService.findAllByVmIdSortByTimeStampDescending(audit2.getVmId());

        assertThat(audits).hasSize(2);
        assertThat(audits.get(0).getId()).isEqualTo(audit3.getId());
        assertThat(audits.get(1).getId()).isEqualTo(audit2.getId());
    }

    @Test
    public void findAllByVmIdInGivenIntervalReturnsEmptyIfInvalid() {
        List<Audit> audits = auditService.findAllByVmIdInGivenInterval("vm99", LocalDateTime.now().minusHours(4),
                        LocalDateTime.now().plusDays(4));

        assertThat(audits).hasSize(0);
    }

    @Test
    public void findAllByVmIdInGivenIntervalReturnsAudits() {
        LocalDateTime startInterval = LocalDateTime.now().minusDays(5);
        LocalDateTime endInterval = LocalDateTime.now().minusDays(3);
        List<Audit> audits = auditService.findAllByVmIdInGivenInterval(audit2.getVmId(), startInterval, endInterval);

        assertThat(audits).hasSize(1);
        assertThat(audits.get(0).getVmId()).isEqualTo(audit2.getVmId());
        assertThat(audits.get(0).getTimeStamp()).isBetween(startInterval, endInterval);
    }

    @Test
    public void saveCachesAudit() {
        Audit tempAudit = EntityCreator.createAudit("vm4",
                "resource_usage",
                LocalDateTime.now().minusHours(5),
                "data4", agent).get();
        auditService.save(tempAudit);

        Audit cachedAudit = (Audit) cache.get(tempAudit.getId()).get();

        assertThat(Objects.requireNonNull(cachedAudit).getId()).isEqualTo(tempAudit.getId());

        //Undo changes
        auditService.deleteById(tempAudit.getId());
    }

    @Test
    public void deleteByIdEvictsFromCache() {
        Audit tempAudit = EntityCreator.createAudit("vm4",
                "resource_usage",
                LocalDateTime.now().minusHours(5),
                "data4", agent).get();
        auditService.save(tempAudit);

        auditService.deleteById(tempAudit.getId());

        Object cachedAudit = cache.get(tempAudit.getId());

        assertThat(cachedAudit).isNull();
    }

    @Test
    public void findByIdCachesResultOnSuccessfulFind() {
        // Place audit1 in the cache
        auditService.findById(audit1.getId());

        Audit cachedAudit = (Audit) cache.get(audit1.getId()).get();

        assertThat(Objects.requireNonNull(cachedAudit).getId()).isEqualTo(audit1.getId());
    }

    @Test
    public void findByIdDoesNotCacheResultOnFailedFind() {
        UUID id = UUID.randomUUID();
        // Place audit1 in the cache
        auditService.findById(id);

        Object cachedAudit = cache.get(id).get();

        assertThat(cachedAudit).isNull();
    }
}
