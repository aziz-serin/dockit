package org.dockit.dockitagent.entity;

import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Audit entity to be passed around after collecting the information
 */
public class Audit {
    private final String vmId;
    private final String category;
    private final ZonedDateTime timeStamp;
    private final String data;

    /**
     * Constructor for the Audit entity
     *
     * @param vmId id of the running vm
     * @param category category for the audit data
     * @param timeStamp {@link ZonedDateTime} timeStamp of the collected data
     * @param data encrypted data
     */
    Audit(String vmId, String category, ZonedDateTime timeStamp, String data) {
        this.vmId = vmId;
        this.category = category;
        this.timeStamp = timeStamp;
        this.data = data;
    }

    /**
     * @return vmId
     */
    public String getVmId() {
        return vmId;
    }

    /**
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return timeStamp
     */
    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }

    /**
     * @return data
     */
    public String getData() {
        return data;
    }

    /**
     * @return {@link String} representation of the entity in JSON format
     */
    public String toMap() {
        Gson gson = new Gson();
        Map<String, String> json = Map.of(
                "vmId", vmId,
                "category", category,
                "timeStamp", timeStamp.toLocalDateTime().toString(),
                "data", data
        );
        return gson.toJson(json);
    }
}
