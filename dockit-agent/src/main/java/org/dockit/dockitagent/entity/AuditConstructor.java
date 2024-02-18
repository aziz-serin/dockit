package org.dockit.dockitagent.entity;

import com.google.inject.Inject;
import org.dockit.dockitagent.config.templates.Container;
import org.dockit.dockitagent.encryption.AESGCMEncrypt;
import org.dockit.dockitagent.exceptions.entity.AuditBuildingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Utility class to be used in the generation of {@link Audit} construction
 */
public class AuditConstructor {

    private static final Logger logger = LoggerFactory.getLogger(AuditConstructor.class);

    private final AESGCMEncrypt encryptor;
    private final Container container;

    /**
     * @param encryptor {@link AESGCMEncrypt} instance to be injected
     * @param container {@link org.dockit.dockitagent.config.ConfigContainer} instance to be injected
     */
    @Inject
    public AuditConstructor(AESGCMEncrypt encryptor, Container container) {
        this.encryptor = encryptor;
        this.container = container;
    }

    /**
     * Construction method for a given audit using {@link org.dockit.dockitagent.config.Config} data
     *
     * @param data raw string data to be encrypted
     * @param category category for the given audit data
     * @return {@link Optional} empty if there is an exception, audit if not
     */
    public Optional<Audit> construct(String data, String category) {
        try {
            String encryptedData = encryptor.encrypt(data);
            ZonedDateTime zonedDateTime = ZonedDateTime.now(container.getConfig().getZONE_ID());

            return Optional.of(AuditBuilder.newBuilder()
                    .vmId(container.getConfig().getVM_ID())
                    .category(category)
                    .timeStamp(zonedDateTime)
                    .data(encryptedData)
                    .build());

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException | AuditBuildingException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }
}
