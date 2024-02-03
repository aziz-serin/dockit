package org.dockit.dockitserver.services;

import org.dockit.dockitserver.caching.CacheNames;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.repositories.AgentRepository;
import org.dockit.dockitserver.services.templates.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = {CacheNames.AGENT})
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;

    @Autowired
    public AgentServiceImpl(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Override
    @CachePut(key = "#agent.id")
    public Agent save(Agent agent) {
        return agentRepository.save(agent);
    }

    @Override
    @CachePut(key = "#id")
    public Optional<Agent> updateAgentName(UUID id, String newAgentName) {
        Optional<Agent> optionalAgent = agentRepository.findById(id);
        if (optionalAgent.isPresent()) {
            Agent agent = optionalAgent.get();
            agent.setAgentName(newAgentName);
            return Optional.of(agentRepository.save(agent));
        }
        return Optional.empty();
    }

    @Override
    @CachePut(key = "#id")
    public Optional<Agent> updateLastActiveTime(UUID id, LocalDateTime lastActiveTime) {
        Optional<Agent> optionalAgent = agentRepository.findById(id);
        if (optionalAgent.isPresent()) {
            Agent agent = optionalAgent.get();
            agent.setLastActiveTime(lastActiveTime);
            return Optional.of(agentRepository.save(agent));
        }
        return Optional.empty();
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(UUID id) {
        agentRepository.deleteById(id);
    }

    @Override
    @Cacheable(key = "#id")
    public Optional<Agent> findById(UUID id) {
        return agentRepository.findById(id);
    }

    @Override
    public List<Agent> findAll() {
        return agentRepository.findAll();
    }

    @Override
    public List<Agent> findByAgentName(String agentName) {
        List<Agent> agents = agentRepository.findAll();
        return agents.stream()
                .filter(a -> a.getAgentName().equals(agentName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Agent> findAllSortedByCreationDateAscending() {
        List<Agent> agents = agentRepository.findAll();
        return agents.stream()
                .sorted(Comparator.comparing(Agent::getCreationTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<Agent> findAllSortedByCreationDateDescending() {
        List<Agent> agents = agentRepository.findAll();
        List<Agent> sortedAgents = new java.util.ArrayList<>(agents.stream()
                .sorted(Comparator.comparing(Agent::getCreationTime))
                .toList());
        Collections.reverse(sortedAgents);
        return sortedAgents;
    }

    @Override
    public List<Agent> findAllByCreationDateBetweenTwoDates(LocalDateTime intervalStart, LocalDateTime intervalEnd) {
        List<Agent> agents = agentRepository.findAll();
        return agents.stream()
                .filter(agent -> {
                    LocalDateTime agentCreationDate = agent.getCreationTime();
                    return agentCreationDate.isAfter(intervalStart) && agentCreationDate.isBefore(intervalEnd);
                }).collect(Collectors.toList());
    }

    @Override
    public List<Agent> findAllRecentlyActiveInGivenMinutes(long minutes) {
        List<Agent> agents = agentRepository.findAll();
        return agents.stream()
                .filter(agent -> {
                    LocalDateTime agentLastActiveTime = agent.getLastActiveTime();
                    LocalDateTime now = LocalDateTime.now();
                    return agentLastActiveTime.isAfter(now.minus(Duration.ofMinutes(minutes)));
                }).collect(Collectors.toList());
    }

    @Override
    public List<Agent> findAllRecentlyNotActiveInGivenMinutes(long minutes) {
        List<Agent> agents = agentRepository.findAll();
        return agents.stream()
                .filter(agent -> {
                    LocalDateTime agentLastActiveTime = agent.getLastActiveTime();
                    LocalDateTime now = LocalDateTime.now();
                    return agentLastActiveTime.isBefore(now.minus(Duration.ofMinutes(minutes)));
                }).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return agentRepository.count();
    }
}
