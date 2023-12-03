package org.dockit.dockitserver.security.apikeys;

import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class APIKeyGeneratorTest {

    private static final List<String> UNSAFECHARS = List.of("<", ">", " ", "\"", "#", "%", "{", "}",
            "|", "\\", "^", "~", "[", "]", "'", "?", ":", "/", ";", "@", "=", "&");
    private static final int LENGTH = 32;

    @Test
    public void generatedKeyIsUrlSafe() {
        String key = APIKeyGenerator.generateApiKey();
        assertThat(key).doesNotContain(UNSAFECHARS);
    }

    @Test
    public void generatedKeyHasAppropriateLength() {
        String key = APIKeyGenerator.generateApiKey();
        assertThat(Base64.getUrlDecoder().decode(key)).hasSize(LENGTH);
    }
}
