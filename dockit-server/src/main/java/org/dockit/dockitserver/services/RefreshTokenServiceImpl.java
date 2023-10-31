package org.dockit.dockitserver.services;

import org.dockit.dockitserver.entities.RefreshToken;
import org.dockit.dockitserver.repositories.RefreshTokenRepository;
import org.dockit.dockitserver.services.templates.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void deleteById(Long id) {
        refreshTokenRepository.deleteById(id);
    }

    private void deleteAllById(List<Long> ids) {
        refreshTokenRepository.deleteAllById(ids);
    }
    @Override
    public void deleteExpired() {
        List<RefreshToken> tokens = refreshTokenRepository.findAll();
        List<Long> expiredTokenIds = tokens.stream()
                .filter(t -> t.getExpiryDate().isBefore(LocalDateTime.now()))
                .map(RefreshToken::getId)
                .toList();
        deleteAllById(expiredTokenIds);
    }

    @Override
    public List<RefreshToken> findAll() {
        return refreshTokenRepository.findAll();
    }

    @Override
    public List<RefreshToken> findAllAscendingByExpiryDate() {
        List<RefreshToken> tokens = refreshTokenRepository.findAll();
        return tokens.stream()
                .sorted(Comparator.comparing(RefreshToken::getExpiryDate))
                .toList();
    }

    @Override
    public List<RefreshToken> findAllDescendingByExpiryDate() {
        List<RefreshToken> tokens = refreshTokenRepository.findAll();
        List<RefreshToken> sortedTokens = new ArrayList<>(tokens.stream()
                .sorted(Comparator.comparing(RefreshToken::getExpiryDate))
                .toList());
        Collections.reverse(sortedTokens);
        return sortedTokens;
    }

    @Override
    public long count() {
        return refreshTokenRepository.count();
    }
}
