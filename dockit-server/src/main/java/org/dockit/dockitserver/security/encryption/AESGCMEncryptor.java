package org.dockit.dockitserver.security.encryption;

import org.dockit.dockitserver.security.key.KeyConstants;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class AESGCMEncryptor {

    public String encrypt(String data, String authenticationTagData, SecretKey key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {

        byte[] IV = getRandomBytes(KeyConstants.IV_SIZE_GCM);
        byte[] authenticationTagDataBytes = authenticationTagData.getBytes();

        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, key, IV);

        cipher.updateAAD(authenticationTagDataBytes);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(UTF_8));

        byte[] cipherByte = ByteBuffer
                .allocate(IV.length + authenticationTagDataBytes.length + encryptedBytes.length)
                .put(IV)
                .put(authenticationTagDataBytes)
                .put(encryptedBytes)
                .array();
        return Base64.getEncoder().encodeToString(cipherByte);
    }


    public String decrypt(String data, String authenticationTagData, SecretKey key) throws
            InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        byte[] decodedToDecrypt = Base64.getDecoder().decode(data);
        byte[] IV = new byte[KeyConstants.IV_SIZE_GCM];
        byte[] authenticationTagDataBytes = authenticationTagData.getBytes();

        System.arraycopy(decodedToDecrypt, 0, IV, 0, IV.length);

        Cipher cipher = initCipher(Cipher.DECRYPT_MODE, key, IV);

        byte[] encryptedBytes = new byte[decodedToDecrypt.length - IV.length];
        System.arraycopy(decodedToDecrypt, IV.length, encryptedBytes, 0, encryptedBytes.length);

        cipher.updateAAD(authenticationTagDataBytes);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return Base64.getEncoder().encodeToString(decryptedBytes);
    }

    private byte[] getRandomBytes(int length) {
        byte[] nonce = new byte[length];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    private Cipher initCipher(int mode, SecretKey secretKey, byte[] iv) throws InvalidKeyException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(KeyConstants.AES_GCM_CIPHER);
        cipher.init(mode, secretKey, new GCMParameterSpec(KeyConstants.GCM_TAG_LENGTH, iv));
        return cipher;
    }
}
