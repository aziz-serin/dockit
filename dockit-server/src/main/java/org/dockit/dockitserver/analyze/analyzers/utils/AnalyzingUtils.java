package org.dockit.dockitserver.analyze.analyzers.utils;

import org.dockit.dockitserver.entities.Alert;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class containing methods to help processing of {@link org.dockit.dockitserver.analyze.analyzers.Analyzer}
 * instances
 */
public final class AnalyzingUtils {
    private static final String DELIMITER = "/_/_/";

    /**
     * Split some data using the delimiter
     *
     * @param data Connected data
     * @return list of split data
     */
    public static List<String> splitData(String data) {
        return Arrays.stream(data.split(DELIMITER)).toList();
    }

    /**
     * Determine the {@link Alert.Importance} using the {@link AlertGenerationThresholds} fields
     *
     * @param percentage percentage data
     * @return determined importance for the given percentage
     */
    public static Alert.Importance getImportanceFromPercentage(double percentage) {
        if (percentage >= AlertGenerationThresholds.CRITICAL) {
            return Alert.Importance.CRITICAL;
        } else if (percentage < AlertGenerationThresholds.CRITICAL && percentage >= AlertGenerationThresholds.MEDIUM ) {
            return Alert.Importance.MEDIUM;
        } else if (percentage < AlertGenerationThresholds.MEDIUM && percentage >= AlertGenerationThresholds.LOW) {
            return Alert.Importance.LOW;
        } else {
            return Alert.Importance.NONE;
        }
    }
}
