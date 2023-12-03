package org.dockit.dockitserver.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.dockit.dockitserver.config.Config;
import org.dockit.dockitserver.config.ConfigConstants;
import org.dockit.dockitserver.config.ConfigContainer;
import org.dockit.dockitserver.exceptions.security.jwt.JWTSecretKeyException;
import org.dockit.dockitserver.security.key.AESKeyGenerator;
import org.dockit.dockitserver.security.key.KeyConstants;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JWTGeneratorTest {

    private final static String JWT_ISSUER = ConfigConstants.DEFAULT_JWT_ISSUER.toString();
    private final static String JWT_SECRET_ALIAS = ConfigConstants.DEFAULT_JWT_SECRET_ALIAS.toString();
    private final static int JWT_EXPIRY_TIME = Integer.parseInt(ConfigConstants.DEFAULT_JWT_EXPIRATION_TIME.toString());
    private final static String ERROR_MESSAGE = "Could not generate jwt, jwt secret is not present in keystore";
    private final static String SUBJECT = "user";

    private Optional<Key> secretKey;

    @MockBean
    @Mock
    private ConfigContainer configContainer;

    @MockBean
    @Mock
    private KeyStoreHandler keyStoreHandler;

    @InjectMocks
    private JWTGenerator jwtGenerator;

    @Mock
    private Config config;

    @Before
    public void setup() {
        secretKey = AESKeyGenerator.generateKey(KeyConstants.AES_CIPHER, KeyConstants.JWT_KEY_SIZE);
    }

    @Test
    public void generateTokenThrowsExceptionGivenSecretDoesNotExist() {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getJwtSecretAlias()).thenReturn(JWT_SECRET_ALIAS);
        when(keyStoreHandler.getKey(eq(JWT_SECRET_ALIAS), eq("".toCharArray()))).thenReturn(Optional.empty());

        Exception e = assertThrows(JWTSecretKeyException.class, () -> {
           jwtGenerator.generateToken(SUBJECT);
        });

        assertThat(e.getMessage()).isEqualTo(ERROR_MESSAGE);
    }

    @Test
    public void generateTokenIsMacBased() throws Exception {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getJwtSecretAlias()).thenReturn(JWT_SECRET_ALIAS);
        when(config.getJwtIssuer()).thenReturn(JWT_ISSUER);
        when(config.getJwtExpirationTime()).thenReturn(JWT_EXPIRY_TIME);
        when(keyStoreHandler.getKey(eq(JWT_SECRET_ALIAS), eq("".toCharArray()))).thenReturn(secretKey);

        String jwt = jwtGenerator.generateToken(SUBJECT);
        SignedJWT signedJWT = SignedJWT.parse(jwt);

        assertThat(signedJWT.getHeader().getAlgorithm()).isEqualTo(JWSAlgorithm.HS256);
    }

    @Test
    public void generateTokenContainsRequiredClaims() throws Exception {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getJwtSecretAlias()).thenReturn(JWT_SECRET_ALIAS);
        when(config.getJwtIssuer()).thenReturn(JWT_ISSUER);
        when(config.getJwtExpirationTime()).thenReturn(JWT_EXPIRY_TIME);
        when(keyStoreHandler.getKey(eq(JWT_SECRET_ALIAS), eq("".toCharArray()))).thenReturn(secretKey);

        String jwt = jwtGenerator.generateToken(SUBJECT);
        JWTClaimsSet jwtClaimsSet = SignedJWT.parse(jwt).getJWTClaimsSet();

        assertThat(jwtClaimsSet.getSubject()).isEqualTo(SUBJECT);
        assertThat(jwtClaimsSet.getIssuer()).isEqualTo(JWT_ISSUER);
        assertThat(jwtClaimsSet.getExpirationTime()).isAfter(Date.from(
                Instant.now()
        ));
    }
}
