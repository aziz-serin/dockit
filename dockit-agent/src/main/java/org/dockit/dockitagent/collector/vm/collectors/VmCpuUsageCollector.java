package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.Map;

public class VmCpuUsageCollector implements Collector {
    private static final int DELAY = 100;

    @Override
    public String collect() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        CentralProcessor processor = hardware.getProcessor();
        Gson gson = new Gson();
        Map<String, ?> data = Map.of(
                // Delay is the milliseconds to wait
                "cpu_load", processor.getSystemCpuLoad(DELAY)
        );
        return gson.toJson(data);
    }
}
