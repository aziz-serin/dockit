package org.dockit.dockitserver.security.encryption;

import org.apache.commons.lang3.ArrayUtils;
import org.dockit.dockitserver.security.key.KeyConstants;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Class to handle AES-CBC encryption operations. Check <a href="https://www.ietf.org/rfc/rfc3602.txt">this link</a>
 * for more information on the encryption algorithm
 */
public class AESCBCEncryptor {
    private static IvParameterSpec generateIv() {
        byte[] iv = new byte[KeyConstants.IV_SIZE_CBC];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * Encrypt using AES-CBC algorithm
     *
     * @param input string data to be encrypted
     * @param key {@link SecretKey} used to encrypt the data
     * @return encrypted string if successful
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String encrypt(String input, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        if (input == null || key == null) {
            throw new IllegalArgumentException();
        }

        Cipher cipher = Cipher.getInstance(KeyConstants.AES_CBC_CIPHER);
        IvParameterSpec iv = generateIv();

        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        byte[] ivBytes = iv.getIV();

        // Concatenate iv BEFORE cipherText
        byte[] outputCipher = ArrayUtils.addAll(ivBytes, cipherText);

        return Base64.getEncoder().encodeToString(outputCipher);
    }

    /**
     * Decrypt using AES-CBC algorithm
     *
     * @param input string data to be decrypted
     * @param key {@link SecretKey} used to decrypt the data
     * @return decrypted string if successful
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String decrypt(String input, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        if (input == null || key == null) {
            throw new IllegalArgumentException();
        }

        byte[] inputBytes = Base64.getDecoder().decode(input);

        // Get first bytes which represent the IV
        IvParameterSpec iv = new IvParameterSpec(
                ArrayUtils.subarray(inputBytes, 0, KeyConstants.IV_SIZE_CBC)
        );

        // Get the rest of the bytes which represent the data to decrypt
        byte[] data = ArrayUtils.subarray(inputBytes, KeyConstants.IV_SIZE_CBC, inputBytes.length);

        Cipher cipher = Cipher.getInstance(KeyConstants.AES_CBC_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(data);

        return new String(plainText);
    }
}
