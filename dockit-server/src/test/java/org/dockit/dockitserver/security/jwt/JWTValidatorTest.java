package org.dockit.dockitserver.security.jwt;

import org.dockit.dockitserver.config.Config;
import org.dockit.dockitserver.config.ConfigConstants;
import org.dockit.dockitserver.config.ConfigContainer;
import org.dockit.dockitserver.security.key.AESKeyGenerator;
import org.dockit.dockitserver.security.key.KeyConstants;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.dockit.dockitserver.testUtils.MockJWTGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JWTValidatorTest {
    private final static String JWT_ISSUER = ConfigConstants.DEFAULT_JWT_ISSUER.toString();
    private final static String JWT_SECRET_ALIAS = ConfigConstants.DEFAULT_JWT_SECRET_ALIAS.toString();
    private final static int JWT_EXPIRY_TIME = Integer.parseInt(ConfigConstants.DEFAULT_JWT_EXPIRATION_TIME.toString());
    private final static String SUBJECT = "user";

    private Optional<Key> secretKey;

    @MockBean
    @Mock
    private ConfigContainer configContainer;

    @MockBean
    @Mock
    private KeyStoreHandler keyStoreHandler;

    @InjectMocks
    private JWTValidator jwtValidator;

    @Mock
    private Config config;


    @Before
    public void setup() throws Exception {
        secretKey = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.JWT_KEY_SIZE);
    }

    @Test
    public void validateJwtTokenFailsGivenMissingSubject() throws Exception {
        String jwt = MockJWTGenerator
                .generateMockJwt("", JWT_ISSUER, JWT_EXPIRY_TIME, (SecretKey) secretKey.get());
        when(configContainer.getConfig()).thenReturn(config);

        assertFalse(jwtValidator.validateJwtToken(jwt));
    }

    @Test
    public void validateJwtTokenFailsGivenMissingIssuer() throws Exception {
        String jwt = MockJWTGenerator
                .generateMockJwt(SUBJECT, "", JWT_EXPIRY_TIME, (SecretKey) secretKey.get());
        when(configContainer.getConfig()).thenReturn(config);

        assertFalse(jwtValidator.validateJwtToken(jwt));
    }

    @Test
    public void validateJwtTokenFailsGivenMissingExpirationTime() throws Exception {
        String jwt = MockJWTGenerator
                .generateMockJwt(SUBJECT, JWT_ISSUER, null, (SecretKey) secretKey.get());
        when(configContainer.getConfig()).thenReturn(config);

        assertFalse(jwtValidator.validateJwtToken(jwt));
    }

    @Test
    public void validateJwtTokenFailsGivenJwtSecretDoesNotExist() throws Exception {
        String jwt = MockJWTGenerator
                .generateMockJwt(SUBJECT, JWT_ISSUER, JWT_EXPIRY_TIME, (SecretKey) secretKey.get());
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getJwtSecretAlias()).thenReturn(JWT_SECRET_ALIAS);
        when(keyStoreHandler.getKey(eq(JWT_SECRET_ALIAS), any(char[].class))).thenReturn(Optional.empty());

        assertFalse(jwtValidator.validateJwtToken(jwt));
    }

    @Test
    public void validateJwtTokenFailsGivenJwtSecretIsDifferent() throws Exception {
        String jwt = MockJWTGenerator
                .generateMockJwt(SUBJECT, JWT_ISSUER, JWT_EXPIRY_TIME, (SecretKey) secretKey.get());
        Optional<Key> differentKey = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.JWT_KEY_SIZE);
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getJwtSecretAlias()).thenReturn(JWT_SECRET_ALIAS);
        when(keyStoreHandler.getKey(eq(JWT_SECRET_ALIAS), any(char[].class))).thenReturn(differentKey);

        assertFalse(jwtValidator.validateJwtToken(jwt));
    }

    @Test
    public void validateJwtTokenFailsGivenJwtIssuerIsDifferent() throws Exception {
        String jwt = MockJWTGenerator
                .generateMockJwt(SUBJECT, "differentIssuer", JWT_EXPIRY_TIME, (SecretKey) secretKey.get());
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getJwtSecretAlias()).thenReturn(JWT_SECRET_ALIAS);
        when(keyStoreHandler.getKey(eq(JWT_SECRET_ALIAS), any(char[].class))).thenReturn(secretKey);
        when(config.getJwtIssuer()).thenReturn(JWT_ISSUER);

        assertFalse(jwtValidator.validateJwtToken(jwt));
    }

    @Test
    public void validateJwtTokenFailsGivenInvalidExpiryTime() throws Exception {
        String jwt = MockJWTGenerator
                .generateMockJwt(SUBJECT, JWT_ISSUER, -5, (SecretKey) secretKey.get());
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getJwtSecretAlias()).thenReturn(JWT_SECRET_ALIAS);
        when(keyStoreHandler.getKey(eq(JWT_SECRET_ALIAS), any(char[].class))).thenReturn(secretKey);
        when(config.getJwtIssuer()).thenReturn(JWT_ISSUER);

        assertFalse(jwtValidator.validateJwtToken(jwt));
    }

    @Test
    public void validateJwtTokenSucceeds() throws Exception {
        String jwt = MockJWTGenerator
                .generateMockJwt(SUBJECT, JWT_ISSUER, JWT_EXPIRY_TIME, (SecretKey) secretKey.get());
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getJwtSecretAlias()).thenReturn(JWT_SECRET_ALIAS);
        when(keyStoreHandler.getKey(eq(JWT_SECRET_ALIAS), any(char[].class))).thenReturn(secretKey);
        when(config.getJwtIssuer()).thenReturn(JWT_ISSUER);

        assertTrue(jwtValidator.validateJwtToken(jwt));
    }

    @Test
    public void getUserNameFromJwtTokenReturnsSubject() throws Exception {
        String jwt = MockJWTGenerator
                .generateMockJwt(SUBJECT, JWT_ISSUER, JWT_EXPIRY_TIME, (SecretKey) secretKey.get());

        assertThat(jwtValidator.getUserNameFromJwtToken(jwt)).isEqualTo(SUBJECT);
    }
}
