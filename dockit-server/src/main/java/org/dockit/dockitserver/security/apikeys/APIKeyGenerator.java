package org.dockit.dockitserver.security.apikeys;

import java.security.SecureRandom;
import java.util.Base64;

public class APIKeyGenerator {
    private static final int API_KEY_LENGTH = 32;

    public static String generateApiKey() {
        byte[] apiKeyBytes = new byte[API_KEY_LENGTH];
        new SecureRandom().nextBytes(apiKeyBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(apiKeyBytes);
    }
}
