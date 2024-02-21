package org.dockit.dockitserver.analyze.analyzers;

import com.nimbusds.jose.shaded.gson.Gson;
import org.dockit.dockitserver.analyze.analyzers.utils.AlertGenerator;
import org.dockit.dockitserver.analyze.analyzers.utils.AnalyzingUtils;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.entities.Audit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Analyze resource usage of docker containers
 */
@Component
public class DockerResourceAnalyzer implements Analyzer {
    private static final String MEMORY_STATS = "memory_stats";
    private static final String CPU_STATS = "cpu_stats";
    private static final String PR_CPU_STATS = "precpu_stats";
    private static final String CPU_USAGE = "cpu_usage";


    private final AlertGenerator alertGenerator;

    /**
     * @param alertGenerator {@link AlertGenerator instance to be injected}
     */
    public DockerResourceAnalyzer(AlertGenerator alertGenerator) {
        this.alertGenerator = alertGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Alert> analyze(Audit audit) {
        List<String> containerInformation = AnalyzingUtils.splitData(audit.getData());
        List<Alert> alerts = new ArrayList<>();

        for (String information : containerInformation) {
            Optional<Alert> optionalMemoryAlert = analyzeDockerMemory(information, audit);
            optionalMemoryAlert.ifPresent(alerts::add);
            Optional<Alert> optionalCpuAlert = analyzeDockerCpu(information, audit);
            optionalCpuAlert.ifPresent(alerts::add);
        }
        return alerts;
    }

    private Optional<Alert> analyzeDockerMemory(String information, Audit audit) {
        Gson gson = new Gson();
        Map<String, ?> mappedData = gson.fromJson(information, Map.class);
        Map<String, ?> memoryStats = (Map<String, ?>) mappedData.get(MEMORY_STATS);

        Double used_memory = (Double) memoryStats.get("usage");
        Double available_memory = (Double) memoryStats.get("limit");
        double memory_usage = used_memory / available_memory * 100;

        Alert.Importance importance = AnalyzingUtils.getImportanceFromPercentage(memory_usage);

        if (importance.equals(Alert.Importance.NONE)) {
            return Optional.empty();
        }
        String message = "memory_usage: " + memory_usage;
        return alertGenerator.generateAlert(audit, importance, message);
    }

    private Optional<Alert> analyzeDockerCpu(String information, Audit audit) {
        Gson gson = new Gson();
        Map<String, ?> mappedData = gson.fromJson(information, Map.class);
        Map<String, ?> cpuStats = (Map<String, ?>) mappedData.get(CPU_STATS);
        Map<String, ?> preCpuStats = (Map<String, ?>) mappedData.get(PR_CPU_STATS);

        Double cpuTotalUsage = (Double) ((Map<String, ?>) cpuStats.get(CPU_USAGE)).get("total_usage");
        Double preCpuTotalUsage = (Double) ((Map<String, ?>) preCpuStats.get(CPU_USAGE)).get("total_usage");
        Double cpuDelta = cpuTotalUsage - preCpuTotalUsage;

        Double systemCpuUsage = (Double) cpuStats.get("system_cpu_usage");
        Double preSystemCpuUsage = (Double) preCpuStats.get("system_cpu_usage");
        Double systemCpuDelta = systemCpuUsage - preSystemCpuUsage;

        Double numberOfCpus = (Double) cpuStats.get("online_cpus");

        double cpu_usage = (cpuDelta / systemCpuDelta) * numberOfCpus * 100;

        Alert.Importance importance = AnalyzingUtils.getImportanceFromPercentage(cpu_usage);

        if (importance.equals(Alert.Importance.NONE)) {
            return Optional.empty();
        }
        String message = "cpu_usage: " + cpu_usage;
        return alertGenerator.generateAlert(audit, importance, message);
    }
}
