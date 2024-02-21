package org.dockit.dockitserver.analyze;

import org.dockit.dockitserver.analyze.analyzers.DockerResourceAnalyzer;
import org.dockit.dockitserver.analyze.analyzers.VmCpuUsageAnalyzer;
import org.dockit.dockitserver.analyze.analyzers.VmFileSystemUsageAnalyzer;
import org.dockit.dockitserver.analyze.analyzers.VmMemoryUsageAnalyzer;
import org.dockit.dockitserver.analyze.analyzers.VmUsersAnalyzer;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility class which brings all the analyzers together
 */
@Component
public class AuditAnalyzer {

    private final DockerResourceAnalyzer dockerResourceAnalyzer;
    private final VmCpuUsageAnalyzer vmCpuUsageAnalyzer;
    private final VmFileSystemUsageAnalyzer vmFileSystemUsageAnalyzer;
    private final VmMemoryUsageAnalyzer vmMemoryUsageAnalyzer;
    private final VmUsersAnalyzer vmUsersAnalyzer;

    /**
     * Constructor which contains all the analyzers
     *
     * @param dockerResourceAnalyzer {@link DockerResourceAnalyzer} to be injected
     * @param vmCpuUsageAnalyzer {@link VmCpuUsageAnalyzer} to be injected
     * @param vmFileSystemUsageAnalyzer {@link VmFileSystemUsageAnalyzer} to be injected
     * @param vmMemoryUsageAnalyzer {@link VmMemoryUsageAnalyzer} to be injected
     * @param vmUsersAnalyzer {@link VmUsersAnalyzer} to be injected
     */
    @Autowired
    public AuditAnalyzer(DockerResourceAnalyzer dockerResourceAnalyzer,
                                       VmCpuUsageAnalyzer vmCpuUsageAnalyzer, VmFileSystemUsageAnalyzer vmFileSystemUsageAnalyzer,
                                       VmMemoryUsageAnalyzer vmMemoryUsageAnalyzer, VmUsersAnalyzer vmUsersAnalyzer) {
        this.dockerResourceAnalyzer = dockerResourceAnalyzer;
        this.vmCpuUsageAnalyzer = vmCpuUsageAnalyzer;
        this.vmFileSystemUsageAnalyzer = vmFileSystemUsageAnalyzer;
        this.vmMemoryUsageAnalyzer = vmMemoryUsageAnalyzer;
        this.vmUsersAnalyzer = vmUsersAnalyzer;
    }

    /**
     * Depending on the given audit's category, generate appropriate alerts and return them
     *
     * @param audit {@link Audit} to be analyzed
     * @return list of generated alerts
     */
    public List<Alert> analyze(Audit audit) {
        String category = audit.getCategory();

        switch (category) {
            case AuditCategories.DOCKER_CONTAINER_RESOURCE -> {
                return dockerResourceAnalyzer.analyze(audit);
            }
            case AuditCategories.VM_CPU -> {
                return vmCpuUsageAnalyzer.analyze(audit);
            }
            case AuditCategories.VM_FILESYSTEM -> {
                return vmFileSystemUsageAnalyzer.analyze(audit);
            }
            case AuditCategories.VM_MEMORY -> {
                return vmMemoryUsageAnalyzer.analyze(audit);
            }
            case AuditCategories.VM_USERS -> {
                return vmUsersAnalyzer.analyze(audit);
            }
            default -> {
                return List.of();
            }
        }
    }
}
