package org.dockit.dockitserver.testUtils;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

public class MockJWTGenerator {
    public static String generateMockJwt(String subject, String issuer, Integer expiryTime, SecretKey secret)
            throws Exception {
        JWSSigner signer = new MACSigner(secret);

        Date expTime;

        if (expiryTime != null) {
            Instant instant = Instant.now().plusSeconds(
                    60L * expiryTime);

            expTime = Date.from(instant);
        } else {
            expTime = null;
        }

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(issuer)
                .expirationTime(expTime)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }
}
