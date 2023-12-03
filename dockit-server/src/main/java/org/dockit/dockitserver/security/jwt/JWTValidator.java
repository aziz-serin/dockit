package org.dockit.dockitserver.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.dockit.dockitserver.config.Config;
import org.dockit.dockitserver.config.ConfigContainer;
import org.dockit.dockitserver.exceptions.security.jwt.JWTSecretKeyException;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
public class JWTValidator {
    private static final Logger logger = LoggerFactory.getLogger(JWTValidator.class);

    private final ConfigContainer configContainer;
    private final KeyStoreHandler keyStoreHandler;


    public JWTValidator(ConfigContainer configContainer, KeyStoreHandler keyStoreHandler) {
        this.configContainer = configContainer;
        this.keyStoreHandler = keyStoreHandler;
    }

    public String getUserNameFromJwtToken(String token) throws ParseException {
        return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
    }

    public boolean validateJwtToken(String token) throws ParseException {
        Config config = configContainer.getConfig();
        SignedJWT jwt = SignedJWT.parse(token);

        if (!containsRequiredClaims(jwt.getJWTClaimsSet())) {
            logger.error("Jwt does not have the required claims!");
            return false;
        }
        if (!isSignatureValid(jwt, config)) {
            logger.error("Jwt signature is invalid!");
            return false;
        }
        if (!isIssuerValid(jwt.getJWTClaimsSet().getIssuer(), config)) {
            logger.error("Jwt issuer is not valid!");
            return false;
        }
        if (!isExpValid(jwt.getJWTClaimsSet().getExpirationTime())) {
            logger.error("The token is expired!");
            return false;
        }
        return true;
    }

    private boolean containsRequiredClaims(JWTClaimsSet claimsSet) {
        return StringUtils.hasLength(claimsSet.getSubject()) && StringUtils.hasLength(claimsSet.getIssuer())
                && (claimsSet.getExpirationTime() != null);
    }

    private boolean isSignatureValid(SignedJWT jwt, Config config) {
        try {
            Optional<Key> secretKey = keyStoreHandler.getKey(config.getJwtSecretAlias(), "".toCharArray());
            if (secretKey.isEmpty()) {
                throw new JWTSecretKeyException("Could not generate jwt, jwt secret is not present in keystore");
            }
            JWSVerifier verifier = new MACVerifier((SecretKey) secretKey.get());
            return jwt.verify(verifier);
        } catch (JOSEException e) {
            return false;
        } catch (JWTSecretKeyException e) {
            logger.debug(e.getMessage());
            return false;
        }
    }

    private boolean isIssuerValid(String issuer, Config config) {
        if (!StringUtils.hasLength(issuer)) {
            return false;
        } else {
            return config.getJwtIssuer().equals(issuer);
        }
    }

    private boolean isExpValid(Date exp) {
        long expiryTime = exp.toInstant().getEpochSecond();
        long currentTime = Instant.now().getEpochSecond();
        return expiryTime > currentTime;
    }
}
