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

@Component
public class AuditAnalyzer {

    private final DockerResourceAnalyzer dockerResourceAnalyzer;
    private final VmCpuUsageAnalyzer vmCpuUsageAnalyzer;
    private final VmFileSystemUsageAnalyzer vmFileSystemUsageAnalyzer;
    private final VmMemoryUsageAnalyzer vmMemoryUsageAnalyzer;
    private final VmUsersAnalyzer vmUsersAnalyzer;

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
