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
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
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
     * @return encrypted data
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
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, IV);
        cipher.updateAAD(id.getBytes());

        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        byte[] cipherByte = ArrayUtils.addAll(IV, encryptedBytes);
        return Base64.getEncoder().encodeToString(cipherByte);
    }

    /**
     * Decrypt using AES-GCM algorithm
     *
     * @param data input string to be decrypted
     * @return
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String decrypt(String data) throws
            InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        if (data == null) {
            throw new IllegalArgumentException();
        }

        String id = configContainer.getConfig().getID();
        if (id == null) {
            logger.error("Agent id in config CANNOT be null!");
            throw new IllegalArgumentException();
        }

        byte[] dataBytes = Base64.getDecoder().decode(data);
        byte[] IV = ArrayUtils.subarray(dataBytes, 0, KeyConstants.IV_SIZE_GCM);
        byte[] encryptedBytes = ArrayUtils.subarray(dataBytes, KeyConstants.IV_SIZE_GCM, dataBytes.length);

        Cipher cipher = initCipher(Cipher.DECRYPT_MODE, IV);
        cipher.updateAAD(id.getBytes());

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private byte[] getRandomBytes() {
        byte[] nonce = new byte[KeyConstants.IV_SIZE_GCM];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    private Cipher initCipher(int mode, byte[] iv) throws InvalidKeyException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(KeyConstants.AES_GCM_CIPHER);
        cipher.init(mode, configContainer.getKey(),
                new GCMParameterSpec(KeyConstants.GCM_TAG_LENGTH, iv));
        return cipher;
    }
}
