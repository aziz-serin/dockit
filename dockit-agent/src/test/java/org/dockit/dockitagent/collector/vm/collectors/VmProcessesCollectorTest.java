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
public class VmProcessesCollectorTest {
    private String collectedData;

    @BeforeAll
    public void setup() {
        Collector collector = new VmProcessesCollector();
        collectedData = collector.collect();
    }

    @Test
    public void collectContainsRequiredKeys() {
        List<String> allData = DataSplitter.split(collectedData);

        //validate the first one, the rest should be the same
        Gson gson = new Gson();
        Map<String, ?> data = gson.fromJson(allData.get(0), Map.class);

        assertThat(data).containsOnlyKeys(
                VmCollectorConstants.PROCESS_USER,
                VmCollectorConstants.PROCESS_PID,
                VmCollectorConstants.PROCESS_NAME,
                VmCollectorConstants.PROCESS_UP_TIME
        );
    }

    @Test
    public void collectContainsCorrectValues() {
        List<String> allData = DataSplitter.split(collectedData);

        //validate the first one, the rest should be the same
        Gson gson = new Gson();
        Map<String, ?> data = gson.fromJson(allData.get(0), Map.class);

        assertThat((String) data.get(VmCollectorConstants.PROCESS_USER)).isNotBlank();
        assertThat((Double) data.get(VmCollectorConstants.PROCESS_PID)).isGreaterThan( 0);
        assertThat((String) data.get(VmCollectorConstants.PROCESS_NAME)).isNotBlank();
        // If negative it means before epoch time started, doesn't really make sense
        assertThat((Double) data.get(VmCollectorConstants.PROCESS_UP_TIME)).isGreaterThanOrEqualTo(0);
    }
}