package org.dockit.dockitserver.entities.utils;

import org.dockit.dockitserver.entities.Alert;

import java.util.Optional;

public final class AlertImportanceConverter {
    public static Optional<Alert.Importance> getImportance(String importance) {
        if (importance == null)
            return Optional.empty();
        else {
            return switch (importance) {
                case "CRITICAL" -> Optional.of(Alert.Importance.CRITICAL);
                case "MEDIUM" -> Optional.of(Alert.Importance.MEDIUM);
                case "LOW" -> Optional.of(Alert.Importance.LOW);
                default -> Optional.empty();
            };
        }
    }
}
