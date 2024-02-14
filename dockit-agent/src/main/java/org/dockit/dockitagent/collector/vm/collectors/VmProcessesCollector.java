package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.utils.InformationBuilderHelper;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.List;
import java.util.Map;

/**
 * Utility class to collect information about the system's processes
 */
public class VmProcessesCollector implements Collector {

    /**
     * Collect running processes and information about them in the system
     *
     * @return JSON representation of collected data in string format
     */
    @Override
    public String collect() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        List<OSProcess> processes = operatingSystem.getProcesses();
        List<String> processInformation = processes.stream()
                .map(process -> {
                    Gson gson = new Gson();
                    Map<String, ?> data = Map.of(
                            "user", process.getUser(),
                            "pid", process.getProcessID(),
                            "name", process.getName(),
                            "up_time", process.getUpTime()
                    );
                    return gson.toJson(data);
                        })
                .toList();
        return InformationBuilderHelper.build(processInformation);
    }
}
