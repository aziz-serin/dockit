package org.dockit.dockitagent.collector.vm.collectors;


import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.vm.collectors.constants.VmCollectorConstants;
import org.dockit.dockitagent.utils.DataSplitter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VmDiskUsageCollectorTest {
    private String collectedData;

    @BeforeAll
    public void setup() {
        Collector collector = new VmDiskUsageCollector();
        collectedData = collector.collect();
    }

    @Test
    public void collectContainsRequiredKeys() {
        List<String> allData = DataSplitter.split(collectedData);

        //validate the first one, the rest should be the same
        Gson gson = new Gson();
        Map<String, ?> data = gson.fromJson(allData.get(0), Map.class);

        assertThat(data).containsOnlyKeys(
                VmCollectorConstants.DISK_STORE_NAME,
                VmCollectorConstants.DISK_STORE_SIZE,
                VmCollectorConstants.DISK_STORE_READ,
                VmCollectorConstants.DISK_STORE_WRITE);
    }

    @Test
    public void collectContainsCorrectValues() {
        List<String> allData = DataSplitter.split(collectedData);

        //validate the first one, the rest should be the same
        Gson gson = new Gson();
        Map<String, ?> data = gson.fromJson(allData.get(0), Map.class);

        assertThat((String) data.get(VmCollectorConstants.DISK_STORE_NAME)).isNotBlank();
        // Check the size of the disk in gb, assume disk size is larger than a gb
        assertThat((Double) data.get(VmCollectorConstants.DISK_STORE_SIZE) / Math.pow(1024, 3))
                .isGreaterThan(1D);
        assertThat((Double) data.get(VmCollectorConstants.DISK_STORE_READ)).isGreaterThanOrEqualTo(0D);
        assertThat((Double) data.get(VmCollectorConstants.DISK_STORE_WRITE)).isGreaterThanOrEqualTo(0D);
    }


}