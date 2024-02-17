package org.dockit.dockitagent.collector.utils;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public final class GetContainerIds {
    public static List<String> getContainerIds(String data) {
        Gson gson = new Gson();
        List<String> splitData = gson.fromJson(data, List.class);
        return splitData.stream()
                .map(jsonData -> {
                    Map<String, ?> parsedJson = gson.fromJson(jsonData, Map.class);
                    return (String) parsedJson.get("Id");
                }).toList();
    }
}
