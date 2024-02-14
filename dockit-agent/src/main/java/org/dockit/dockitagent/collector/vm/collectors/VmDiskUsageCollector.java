package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.utils.InformationBuilderHelper;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.List;
import java.util.Map;

public class VmDiskUsageCollector implements Collector {

    @Override
    public String collect() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        List<HWDiskStore> diskStores = hardware.getDiskStores();
        List<String> diskInformation = diskStores.stream()
                .map(diskStore -> {
                    Gson gson = new Gson();
                    Map<String, ?> data = Map.of(
                            "name", diskStore.getName(),
                            "size", diskStore.getSize(),
                            "read_bytes", diskStore.getReadBytes(),
                            "write_bytes", diskStore.getWriteBytes()
                    );
                    return gson.toJson(data);
                })
                .toList();
        return InformationBuilderHelper.build(diskInformation);
    }
}
