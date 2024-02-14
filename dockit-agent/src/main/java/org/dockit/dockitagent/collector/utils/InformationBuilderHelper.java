package org.dockit.dockitagent.collector.utils;

import java.util.List;

public final class InformationBuilderHelper {
    public static String build(List<String> givenData) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String data : givenData) {
            stringBuilder.append(data);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }
}
