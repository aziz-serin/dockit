package org.dockit.dockitserver.services.templates;

import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;

import java.util.List;
import java.util.UUID;

/**
 * Service layer to interact with Alert entity
 */
public interface AlertService {
    /**
     *
     * Save the alert object
     *
     * @param alert {@link Alert} to be saved
     * @return {@link Alert} saved alert
     */
    Alert save(Alert alert);

    /**
     *
     * Save the alert objects
     *
     * @param alert {@link Alert}s to be saved
     * @return {@link Alert} saved alerts
     */
    List<Alert> save(List<Alert> alert);

    /**
     * Return all alerts saved by an agent
     *
     * @param agent {@link Agent} instance
     * @return List of alerts
     */
    List<Alert> findByAgent(Agent agent);

    /**
     * Return all alerts with the same vmId
     *
     * @param vmId string representation of vmId
     * @return List of alerts
     */
    List<Alert> findByVmId(String vmId);

    /**
     * Delete a specified alert
     *
     * @param id id of the alert to be deleted
     */
    void deleteById(UUID id);

    /**
     * Return most recent n alerts from the same vmId
     *
     * @param vmId string representation of vmId
     * @param count number of most recent alerts to return
     * @return List of alerts
     */
    List<Alert> findMostRecentWithVmId(String vmId, int count);

    /**
     * Find all the alerts with the same importance
     *
     * @param importance importance of the alert
     * @return List of alerts
     */
    List<Alert> findByImportance(Alert.Importance importance);

    /**
     * Find all the alerts with the same importance and vmId
     *
     * @param importance importance of the alert
     * @return List of alerts
     */
    List<Alert> findByImportanceWithSameVmId(Alert.Importance importance, String vmId);

}
