package org.dockit.dockitserver.analyze.analyzers.utils;

import org.dockit.dockitserver.entities.Alert;

import java.util.Arrays;
import java.util.List;

public final class AnalyzingUtils {
    private static final String DELIMITER = "/_/_/";
    
    public static List<String> splitData(String data) {
        return Arrays.stream(data.split(DELIMITER)).toList();
    }
    
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
