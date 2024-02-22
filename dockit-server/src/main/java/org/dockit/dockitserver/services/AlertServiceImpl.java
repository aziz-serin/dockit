package org.dockit.dockitserver.services;

import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.repositories.AlertRepository;
import org.dockit.dockitserver.services.templates.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    @Autowired
    public AlertServiceImpl(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public Alert save(Alert alert) {
        return alertRepository.save(alert);
    }

    @Override
    public List<Alert> save(List<Alert> alerts) {
        return alertRepository.saveAll(alerts);
    }

    @Override
    public List<Alert> findByAgent(Agent agent) {
        return alertRepository.findAll().stream()
                .filter(alert -> alert.getAgent().getId().equals(agent.getId()))
                .toList();
    }

    @Override
    public List<Alert> findByVmId(String vmId) {
        return alertRepository.findAll().stream()
                .filter(alert -> alert.getVmId().equals(vmId))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        alertRepository.deleteById(id);
    }

    @Override
    public List<Alert> findMostRecentWithVmId(String vmId, int count) {
        List<Alert> alerts = new java.util.ArrayList<>(alertRepository.findAll().stream()
                .filter(alert -> alert.getVmId().equals(vmId))
                .sorted(Comparator.comparing(Alert::getAuditTimeStamp))
                .toList());

        Collections.reverse(alerts);

        return alerts.stream()
                .limit(count)
                .toList();
    }

    @Override
    public List<Alert> findByImportance(Alert.Importance importance) {
        return alertRepository.findAll().stream()
                .filter(alert -> alert.getImportance().equals(importance))
                .toList();
    }

    @Override
    public List<Alert> findByImportanceWithSameVmId(Alert.Importance importance, String vmId) {
        return alertRepository.findAll().stream()
                .filter(alert -> alert.getVmId().equals(vmId)
                        && alert.getImportance().equals(importance))
                .toList();
    }
}
