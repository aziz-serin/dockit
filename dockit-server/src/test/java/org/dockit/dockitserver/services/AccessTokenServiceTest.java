package org.dockit.dockitserver.services;

import org.dockit.dockitserver.DockitServerApplication;
import org.dockit.dockitserver.entities.AccessToken;
import org.dockit.dockitserver.services.templates.AccessTokenService;
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
public class AccessTokenServiceTest {

    @Autowired
    private AccessTokenService accessTokenService;

    private AccessToken accessToken1;
    private AccessToken accessToken2;
    private AccessToken accessToken3;

    @BeforeAll
    public void setup() {
        accessToken1 = EntityCreator.createAccessToken("token1", LocalDateTime.now().plusHours(1)).get();
        accessTokenService.save(accessToken1);

        accessToken2 = new AccessToken();
        accessToken2.setToken("token2");
        accessToken2.setExpiryDate(LocalDateTime.now().minusMinutes(5));
        accessTokenService.save(accessToken2);

        accessToken3 = EntityCreator.createAccessToken("token3", LocalDateTime.now().plusDays(1)).get();
        accessTokenService.save(accessToken3);
    }

    @Test
    public void findAllReturnsAllTokens() {
        assertThat(accessTokenService.findAll()).hasSize(3);
    }

    @Test
    public void findAllAscendingByExpiryDateReturnsAscendingList() {
        List<AccessToken> tokens = accessTokenService.findAllAscendingByExpiryDate();

        assertThat(tokens).hasSize(3);
        assertThat(tokens.get(0).getId()).isEqualTo(accessToken2.getId());
        assertThat(tokens.get(1).getId()).isEqualTo(accessToken1.getId());
        assertThat(tokens.get(2).getId()).isEqualTo(accessToken3.getId());
    }

    @Test
    public void findAllDescendingByExpiryDateReturnsDescendingList() {
        List<AccessToken> tokens = accessTokenService.findAllDescendingByExpiryDate();

        assertThat(tokens).hasSize(3);
        assertThat(tokens.get(0).getId()).isEqualTo(accessToken3.getId());
        assertThat(tokens.get(1).getId()).isEqualTo(accessToken1.getId());
        assertThat(tokens.get(2).getId()).isEqualTo(accessToken2.getId());
    }

    @Test
    public void deleteExpiredDeletesAllExpiredTokens() {
        accessTokenService.deleteExpired();

        List<AccessToken> tokens = accessTokenService.findAll();
        assertThat(tokens).hasSize(2);
        tokens.forEach(token ->
                assertThat(token.getExpiryDate()).isAfter(LocalDateTime.now()));

        // Undo the effects of this test
        accessTokenService.save(accessToken2);
    }
}
