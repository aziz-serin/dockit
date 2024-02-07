package org.dockit.dockitserver.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.dockit.dockitserver.config.Config;
import org.dockit.dockitserver.config.ConfigContainer;
import org.dockit.dockitserver.exceptions.security.jwt.JWTSecretKeyException;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Utility class responsible for generation of jwts
 */
@Component
public class JWTGenerator {

    private final ConfigContainer configContainer;
    private final KeyStoreHandler keyStoreHandler;

    /**
     * @param configContainer {@link ConfigContainer} object to be injected
     * @param keyStoreHandler {@link KeyStoreHandler} object to be injected
     */
    public JWTGenerator(ConfigContainer configContainer, KeyStoreHandler keyStoreHandler) {
        this.configContainer = configContainer;
        this.keyStoreHandler = keyStoreHandler;
    }

    /**
     * Generates and signs the jwt
     *
     * @param subject the subject of the jwt, in this application's context, username for the
     * {@link org.dockit.dockitserver.entities.Admin}
     * @return string representing the generated jwt token
     * @throws JOSEException if the created JWT cannot be signed
     * @throws JWTSecretKeyException if JWT secret key is not found in the application's keystore
     */
    public String generateToken(String subject) throws JOSEException, JWTSecretKeyException {
        Config config = configContainer.getConfig();
        Optional<Key> secret = keyStoreHandler.getKey(config.getJwtSecretAlias(), "".toCharArray());
        if (secret.isEmpty()) {
            throw new JWTSecretKeyException("Could not generate jwt, jwt secret is not present in keystore");
        }
        JWSSigner signer = new MACSigner((SecretKey) secret.get());

        Instant instant = Instant.now().plusSeconds(
                60L * config.getJwtExpirationTime());

        Date expTime = Date.from(instant);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(config.getJwtIssuer())
                .expirationTime(expTime)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }
}
