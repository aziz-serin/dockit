package org.dockit.dockitagent.collector.docker.collectors;

import com.google.gson.Gson;
import com.google.inject.Injector;
import org.dockit.dockitagent.collector.Collector;
import org.dockit.dockitagent.utils.DataSplitter;
import org.dockit.dockitagent.utils.GuiceTestInitialise;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DockerContainerProcessCollectorTest {

    private Collector collector;

    @Before
    public void setup() {
        Injector injector = GuiceTestInitialise.injector();
        collector = injector.getInstance(DockerContainerProcessCollector.class);
    }

    @Test
    public void collectReturnsData() {
        String data = collector.collect();

        assertThat(data).isNotBlank();
    }

    @Test
    public void collectReturnsJsonParsableData() {
        String data = collector.collect();

        List<String> dataList = DataSplitter.split(data);

        assertThat(dataList).isNotEmpty();

        Gson gson = new Gson();
        Map<String, String> dataJson = gson.fromJson(dataList.get(0), Map.class);
        //Id of the running container
        assertThat(dataJson).containsKey("Processes");
    }
}
