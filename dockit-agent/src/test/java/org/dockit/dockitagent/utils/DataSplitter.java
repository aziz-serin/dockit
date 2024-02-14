package org.dockit.dockitagent.utils;

import org.dockit.dockitagent.collector.utils.InformationBuilderHelper;

import java.util.Arrays;
import java.util.List;

public class DataSplitter {
    public static List<String> split(String data) {
        return Arrays.asList(data.split(InformationBuilderHelper.DELIMITER));
    }
}
