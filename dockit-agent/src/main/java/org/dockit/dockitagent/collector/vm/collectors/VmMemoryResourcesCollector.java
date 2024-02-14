package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.Map;

/**
 * Utility class to collect information about the memory load of the system
 */
public class VmMemoryResourcesCollector implements Collector {

    /**
     * Collect total and available memory in bytes
     *
     * @return JSON representation of collected data in string format
     */
    @Override
    public String collect() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        GlobalMemory memory = hardware.getMemory();

        Gson gson = new Gson();
        Map<String, ?> data = Map.of(
                "total", memory.getTotal(),
                "available", memory.getAvailable()
        );
        return gson.toJson(data);
    }
}
