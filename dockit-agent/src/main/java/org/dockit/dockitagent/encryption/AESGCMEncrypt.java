package org.dockit.dockitagent.encryption;

import com.google.inject.Inject;
import org.apache.commons.lang3.ArrayUtils;
import org.dockit.dockitagent.config.templates.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Class to handle AES-GCM encryption operations. Check
 * <a href="https://nvlpubs.nist.gov/nistpubs/legacy/sp/nistspecialpublication800-38d.pdf">this link</a>
 * for more information on the encryption algorithm
 */
public class AESGCMEncrypt {
    private static final Logger logger = LoggerFactory.getLogger(AESGCMEncrypt.class);

    private final Container configContainer;

    @Inject
    public AESGCMEncrypt(Container configContainer) {
        this.configContainer = configContainer;
    }

    /**
     * Encrypt using AES-GCM algorithm
     *
     * @param data input string to be encrypted
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String encrypt(String data) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {

        if (data == null) {
            logger.debug("Data for encryption cannot be null!");
            throw new IllegalArgumentException();
        }
        String id = configContainer.getConfig().getID();
        if (id == null) {
            logger.error("Agent id in config CANNOT be null!");
            throw new IllegalArgumentException();
        }

        byte[] IV = getRandomBytes();
        Cipher cipher = initCipher(IV);
        cipher.updateAAD(id.getBytes());

        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        byte[] cipherByte = ArrayUtils.addAll(IV, encryptedBytes);
        return Base64.getEncoder().encodeToString(cipherByte);
    }

    private byte[] getRandomBytes() {
        byte[] nonce = new byte[KeyConstants.IV_SIZE_GCM];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    private Cipher initCipher(byte[] iv) throws InvalidKeyException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(KeyConstants.AES_GCM_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, configContainer.getKey(),
                new GCMParameterSpec(KeyConstants.GCM_TAG_LENGTH, iv));
        return cipher;
    }
}
