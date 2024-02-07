package org.dockit.dockitserver.security.encryption;

import org.apache.commons.lang3.ArrayUtils;
import org.dockit.dockitserver.security.key.KeyConstants;

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
public class AESGCMEncryptor {

    /**
     * Encrypt using AES-GCM algorithm
     *
     * @param data input string to be encrypted
     * @param aad additional authentication data for integrity checking
     * @param key {@link SecretKey} to be used when encrypting
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String encrypt(String data, String aad, SecretKey key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {

        if (data == null || aad == null || key == null) {
            throw new IllegalArgumentException();
        }

        byte[] IV = getRandomBytes(KeyConstants.IV_SIZE_GCM);

        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, key, IV);
        cipher.updateAAD(aad.getBytes());

        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        byte[] cipherByte = ArrayUtils.addAll(IV, encryptedBytes);
        return Base64.getEncoder().encodeToString(cipherByte);
    }

    /**
     * Decrypt using AES-GCM algorithm
     *
     * @param data input string to be decrypted
     * @param aad additional authentication data for integrity checking
     * @param key {@link SecretKey} to be used when decrypting
     * @return
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String decrypt(String data, String aad, SecretKey key) throws
            InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        if (data == null || aad == null || key == null) {
            throw new IllegalArgumentException();
        }

        byte[] dataBytes = Base64.getDecoder().decode(data);
        byte[] IV = ArrayUtils.subarray(dataBytes, 0, KeyConstants.IV_SIZE_GCM);
        byte[] encryptedBytes = ArrayUtils.subarray(dataBytes, KeyConstants.IV_SIZE_GCM, dataBytes.length);

        Cipher cipher = initCipher(Cipher.DECRYPT_MODE, key, IV);
        cipher.updateAAD(aad.getBytes());

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static byte[] getRandomBytes(int length) {
        byte[] nonce = new byte[length];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    private static Cipher initCipher(int mode, SecretKey secretKey, byte[] iv) throws InvalidKeyException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(KeyConstants.AES_GCM_CIPHER);
        cipher.init(mode, secretKey, new GCMParameterSpec(KeyConstants.GCM_TAG_LENGTH, iv));
        return cipher;
    }
}
