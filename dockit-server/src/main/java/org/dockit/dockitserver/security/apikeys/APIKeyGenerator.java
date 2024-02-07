package org.dockit.dockitserver.security.apikeys;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Handle generation of {@link org.dockit.dockitserver.entities.APIKey} tokens
 */
public class APIKeyGenerator {
    private static final int API_KEY_LENGTH = 32;

    /**
     * Generate a random string with length 32 and urlEncode it
     *
     * @return generated token for the {@link org.dockit.dockitserver.entities.APIKey}
     */
    public static String generateApiKey() {
        byte[] apiKeyBytes = new byte[API_KEY_LENGTH];
        new SecureRandom().nextBytes(apiKeyBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(apiKeyBytes);
    }
}
