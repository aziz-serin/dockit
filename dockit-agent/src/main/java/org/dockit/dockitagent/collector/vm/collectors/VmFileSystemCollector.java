package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.utils.InformationBuilderHelper;
import oshi.SystemInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.List;
import java.util.Map;

public class VmFileSystemCollector implements Collector {

    @Override
    public String collect() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        FileSystem fileSystem = operatingSystem.getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores();
        List<String> fileStoreInformation = fileStores.stream()
                .map(fileStore -> {
                    Gson gson = new Gson();
                    Map<String, ?> data = Map.of(
                            "total_space", fileStore.getTotalSpace(),
                            "free_space", fileStore.getFreeSpace()
                    );
                    return gson.toJson(data);
                }).toList();
        return InformationBuilderHelper.build(fileStoreInformation);
    }
}
