package org.dockit.dockitagent.collector.vm.collectors;

import com.google.gson.Gson;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.collector.vm.collectors.constants.VmCollectorConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VmCpuUsageCollectorTest {

    private String collectedData;

    @BeforeAll
    public void setup() {
        Collector collector = new VmCpuUsageCollector();
        collectedData = collector.collect();
    }

    @Test
    public void collectContainsRequiredKeys() {
        Gson gson = new Gson();
        Map<String, ?> data = gson.fromJson(collectedData, Map.class);

        assertThat(data).containsOnlyKeys(VmCollectorConstants.CPU_LOAD);
    }

    @Test
    public void collectContainsValidValues() {
        Gson gson = new Gson();
        Map<String, ?> data = gson.fromJson(collectedData, Map.class);

        assertThat(data.get(VmCollectorConstants.CPU_LOAD)).isNotNull();
        // It works as a percentage, so the value should always be between 0 and 1
        assertThat((Double) data.get(VmCollectorConstants.CPU_LOAD)).isBetween(0D, 1D);
    }
}