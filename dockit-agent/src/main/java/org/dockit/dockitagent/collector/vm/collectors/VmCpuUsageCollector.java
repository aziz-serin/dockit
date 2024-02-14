package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.vm.collectors.constants.VmCollectorConstants;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.Map;

/**
 * Utility class to collect information about the system's CPU usage
 */
public class VmCpuUsageCollector implements Collector {
    private static final int DELAY = 100;

    /**
     * Collect cpu load of the system
     *
     * @return JSON representation of collected data in string format
     */
    @Override
    public String collect() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        CentralProcessor processor = hardware.getProcessor();
        Gson gson = new Gson();
        Map<String, ?> data = Map.of(
                // Delay is the milliseconds to wait
                VmCollectorConstants.CPU_LOAD, processor.getSystemCpuLoad(DELAY)
        );
        return gson.toJson(data);
    }
}
