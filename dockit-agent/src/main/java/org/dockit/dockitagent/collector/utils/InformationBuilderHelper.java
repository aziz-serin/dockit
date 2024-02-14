package org.dockit.dockitagent.collector.utils;

import java.util.List;

/**
 * Helper class to build string from a list of data
 */
public final class InformationBuilderHelper {
    public static final String DELIMITER = "/_/_/";

    /**
     * Build one large string with a delimiter from list of data
     *
     * @param givenData {@link List} of data items
     * @return string representation of produced data
     */
    public static String build(List<String> givenData) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String data : givenData) {
            stringBuilder.append(data);
            stringBuilder.append(DELIMITER);
        }
        return stringBuilder.toString();
    }
}
