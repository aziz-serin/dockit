package org.dockit.dockitagent.collector.utils;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public final class GetContainerIds {
    private static final String KEY = "Id";

    public static List<String> getContainerIds(String data) {
        Gson gson = new Gson();
        List<Map<String, String>> listOfJson = gson.fromJson(data, List.class);

        return listOfJson.stream()
                .map(json -> json.get(KEY))
                .toList();
    }
}
