package org.dockit.dockitserver.services;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.RefreshToken;
import org.dockit.dockitserver.services.templates.RefreshTokenService;
import org.dockit.dockitserver.entities.utils.EntityCreator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = DockitServerApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RefreshTokenServiceTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    private RefreshToken refreshToken1;
    private RefreshToken refreshToken2;
    private RefreshToken refreshToken3;

    @BeforeAll
    public void setup() {
        refreshToken1 = EntityCreator.createRefreshToken("token1", LocalDateTime.now().plusHours(1)).get();
        refreshTokenService.save(refreshToken1);

        // Set like this to avoid getting empty optional since expiration date is not valid
        refreshToken2 = new RefreshToken();
        refreshToken2.setToken("token2");
        refreshToken2.setExpiryDate(LocalDateTime.now().minusMinutes(5));
        refreshTokenService.save(refreshToken2);

        refreshToken3 = EntityCreator.createRefreshToken("token3", LocalDateTime.now().plusDays(1)).get();
        refreshTokenService.save(refreshToken3);
    }

    @Test
    public void findAllReturnsAllTokens() {
        assertThat(refreshTokenService.findAll()).hasSize(3);
    }

    @Test
    public void findAllAscendingByExpiryDateReturnsAscendingList() {
        List<RefreshToken> tokens = refreshTokenService.findAllAscendingByExpiryDate();

        assertThat(tokens).hasSize(3);
        assertThat(tokens.get(0).getId()).isEqualTo(refreshToken2.getId());
        assertThat(tokens.get(1).getId()).isEqualTo(refreshToken1.getId());
        assertThat(tokens.get(2).getId()).isEqualTo(refreshToken3.getId());
    }

    @Test
    public void findAllDescendingByExpiryDateReturnsDescendingList() {
        List<RefreshToken> tokens = refreshTokenService.findAllDescendingByExpiryDate();

        assertThat(tokens).hasSize(3);
        assertThat(tokens.get(0).getId()).isEqualTo(refreshToken3.getId());
        assertThat(tokens.get(1).getId()).isEqualTo(refreshToken1.getId());
        assertThat(tokens.get(2).getId()).isEqualTo(refreshToken2.getId());
    }

    @Test
    public void deleteExpiredDeletesAllExpiredTokens() {
        refreshTokenService.deleteExpired();

        List<RefreshToken> tokens = refreshTokenService.findAll();
        assertThat(tokens).hasSize(2);
        tokens.forEach(token ->
                assertThat(token.getExpiryDate()).isAfter(LocalDateTime.now()));

        // Undo the effects of this test
        refreshTokenService.save(refreshToken2);
    }
}
