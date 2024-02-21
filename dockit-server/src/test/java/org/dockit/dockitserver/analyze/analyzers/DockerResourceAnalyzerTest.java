package org.dockit.dockitserver.analyze.analyzers;


import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.analyze.AuditCategories;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DockerResourceAnalyzerTest {
    // See usage and limit for memory, that's how it is calculated, memory values are altered to give low-level alert
    private static final String MEMORY_DATA = """
            {
              "read": "2015-01-08T22:57:31.547920715Z",
              "pids_stats": {
                "current": 3
              },
              "networks": {
                "eth0": {
                  "rx_bytes": 5338,
                  "rx_dropped": 0,
                  "rx_errors": 0,
                  "rx_packets": 36,
                  "tx_bytes": 648,
                  "tx_dropped": 0,
                  "tx_errors": 0,
                  "tx_packets": 8
                },
                "eth5": {
                  "rx_bytes": 4641,
                  "rx_dropped": 0,
                  "rx_errors": 0,
                  "rx_packets": 26,
                  "tx_bytes": 690,
                  "tx_dropped": 0,
                  "tx_errors": 0,
                  "tx_packets": 9
                }
              },
              "memory_stats": {
                "stats": {
                  "total_pgmajfault": 0,
                  "cache": 0,
                  "mapped_file": 0,
                  "total_inactive_file": 0,
                  "pgpgout": 414,
                  "rss": 6537216,
                  "total_mapped_file": 0,
                  "writeback": 0,
                  "unevictable": 0,
                  "pgpgin": 477,
                  "total_unevictable": 0,
                  "pgmajfault": 0,
                  "total_rss": 6537216,
                  "total_rss_huge": 6291456,
                  "total_writeback": 0,
                  "total_inactive_anon": 0,
                  "rss_huge": 6291456,
                  "hierarchical_memory_limit": 67108864,
                  "total_pgfault": 964,
                  "total_active_file": 0,
                  "active_anon": 6537216,
                  "total_active_anon": 6537216,
                  "total_pgpgout": 414,
                  "total_cache": 0,
                  "inactive_anon": 0,
                  "active_file": 0,
                  "pgfault": 964,
                  "inactive_file": 0,
                  "total_pgpgin": 477
                },
                "max_usage": 6651904,
                "usage": 55,
                "failcnt": 0,
                "limit": 100
              },
              "blkio_stats": {},
              "cpu_stats": {
                "cpu_usage": {
                  "percpu_usage": [
                    8646879,
                    24472255,
                    36438778,
                    30657443
                  ],
                  "usage_in_usermode": 50000000,
                  "total_usage": 100,
                  "usage_in_kernelmode": 30000000
                },
                "system_cpu_usage": 739306590000000,
                "online_cpus": 4,
                "throttling_data": {
                  "periods": 0,
                  "throttled_periods": 0,
                  "throttled_time": 0
                }
              },
              "precpu_stats": {
                "cpu_usage": {
                  "percpu_usage": [
                    8646879,
                    24350896,
                    36438778,
                    30657443
                  ],
                  "usage_in_usermode": 50000000,
                  "total_usage": 100093996,
                  "usage_in_kernelmode": 30000000
                },
                "system_cpu_usage": 9492140000000,
                "online_cpus": 4,
                "throttling_data": {
                  "periods": 0,
                  "throttled_periods": 0,
                  "throttled_time": 0
                }
              }
            }""";
    // See usages for cpu, that's how it is calculated, cpu values are altered to give medium-level alert
    private static final String CPU_DATA = """
            {
              "read": "2015-01-08T22:57:31.547920715Z",
              "pids_stats": {
                "current": 3
              },
              "networks": {
                "eth0": {
                  "rx_bytes": 5338,
                  "rx_dropped": 0,
                  "rx_errors": 0,
                  "rx_packets": 36,
                  "tx_bytes": 648,
                  "tx_dropped": 0,
                  "tx_errors": 0,
                  "tx_packets": 8
                },
                "eth5": {
                  "rx_bytes": 4641,
                  "rx_dropped": 0,
                  "rx_errors": 0,
                  "rx_packets": 26,
                  "tx_bytes": 690,
                  "tx_dropped": 0,
                  "tx_errors": 0,
                  "tx_packets": 9
                }
              },
              "memory_stats": {
                "stats": {
                  "total_pgmajfault": 0,
                  "cache": 0,
                  "mapped_file": 0,
                  "total_inactive_file": 0,
                  "pgpgout": 414,
                  "rss": 6537216,
                  "total_mapped_file": 0,
                  "writeback": 0,
                  "unevictable": 0,
                  "pgpgin": 477,
                  "total_unevictable": 0,
                  "pgmajfault": 0,
                  "total_rss": 6537216,
                  "total_rss_huge": 6291456,
                  "total_writeback": 0,
                  "total_inactive_anon": 0,
                  "rss_huge": 6291456,
                  "hierarchical_memory_limit": 67108864,
                  "total_pgfault": 964,
                  "total_active_file": 0,
                  "active_anon": 6537216,
                  "total_active_anon": 6537216,
                  "total_pgpgout": 414,
                  "total_cache": 0,
                  "inactive_anon": 0,
                  "active_file": 0,
                  "pgfault": 964,
                  "inactive_file": 0,
                  "total_pgpgin": 477
                },
                "max_usage": 6651904,
                "usage": 6537216,
                "failcnt": 0,
                "limit": 67108864
              },
              "blkio_stats": {},
              "cpu_stats": {
                "cpu_usage": {
                  "percpu_usage": [
                    8646879,
                    24472255,
                    36438778,
                    30657443
                  ],
                  "usage_in_usermode": 50000000,
                  "total_usage": 110,
                  "usage_in_kernelmode": 30000000
                },
                "system_cpu_usage": 500,
                "online_cpus": 4,
                "throttling_data": {
                  "periods": 0,
                  "throttled_periods": 0,
                  "throttled_time": 0
                }
              },
              "precpu_stats": {
                "cpu_usage": {
                  "percpu_usage": [
                    8646879,
                    24350896,
                    36438778,
                    30657443
                  ],
                  "usage_in_usermode": 50000000,
                  "total_usage": 10,
                  "usage_in_kernelmode": 30000000
                },
                "system_cpu_usage": 15,
                "online_cpus": 4,
                "throttling_data": {
                  "periods": 0,
                  "throttled_periods": 0,
                  "throttled_time": 0
                }
              }
            }""";

    private Audit memoryAudit;
    private Audit cpuAudit;

    @Autowired
    public DockerResourceAnalyzer dockerResourceAnalyzer;

    @BeforeAll
    public void setup() {
        Agent agent = new Agent();

        memoryAudit = EntityCreator.createAudit("vmId", AuditCategories.DOCKER_CONTAINER_RESOURCE,
                LocalDateTime.now(), MEMORY_DATA, agent).get();
        cpuAudit = EntityCreator.createAudit("vmId", AuditCategories.DOCKER_CONTAINER_RESOURCE,
                LocalDateTime.now(), CPU_DATA, agent).get();
    }

    @Test
    public void analyzeReturnsAppropriateAlertsForMemory() {
        List<Alert> alerts = dockerResourceAnalyzer.analyze(memoryAudit);
        // There should be an alert about memory usage, see above comment for DATA
        assertThat(alerts).hasSize(1);
        assertThat(alerts.get(0).getImportance()).isEqualTo(Alert.Importance.LOW);
        assertThat(alerts.get(0).getMessage()).containsIgnoringCase("memory");
    }

    @Test
    public void analyzeReturnsAppropriateAlertsForCpu() {
        List<Alert> alerts = dockerResourceAnalyzer.analyze(cpuAudit);
        // There should be an alert about cpu usage, see above comment for DATA
        assertThat(alerts).hasSize(1);
        assertThat(alerts.get(0).getImportance()).isEqualTo(Alert.Importance.MEDIUM);
        assertThat(alerts.get(0).getMessage()).containsIgnoringCase("cpu");
    }
}
