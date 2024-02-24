package org.dockit.dockitagent.encryption;

import org.dockit.dockitagent.config.Config;
import org.dockit.dockitagent.config.ConfigContainer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AESGCMEncryptTest {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;
    private static final String DATA = "data to encrypt";
    private static final String ID = "some-id-for-the-agent";

    @Mock
    private Config config;
    @Mock
    private ConfigContainer configContainer;

    private SecretKey validKey;

    @Before
    public void setup() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        validKey = keyGenerator.generateKey();
    }

    @Test
    public void encryptThrowsExceptionGivenNullData() {
        AESGCMEncrypt encrypt = new AESGCMEncrypt(configContainer);

        assertThrows(IllegalArgumentException.class, () -> {
            encrypt.encrypt(null);
        });
    }

    @Test
    public void encryptThrowsExceptionGivenInvalidId() {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getID()).thenReturn(null);

        AESGCMEncrypt encrypt = new AESGCMEncrypt(configContainer);

        assertThrows(IllegalArgumentException.class, () -> {
            encrypt.encrypt(DATA);
        });
    }

    @Test
    public void encryptEncryptsGivenData() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        when(configContainer.getConfig()).thenReturn(config);
        when(configContainer.getKey()).thenReturn(validKey);
        when(config.getID()).thenReturn(ID);

        AESGCMEncrypt encrypt = new AESGCMEncrypt(configContainer);

        String encrypted = encrypt.encrypt(DATA);

        assertThat(encrypted).isNotEmpty();
        assertThat(encrypted).isNotEqualTo(DATA);
    }

    @Test
    public void decryptThrowsExceptionGivenNullData() {
        AESGCMEncrypt encrypt = new AESGCMEncrypt(configContainer);

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            encrypt.decrypt(null);
        });
    }

    @Test
    public void decryptThrowsExceptionGivenInvalidId() {
        when(configContainer.getConfig()).thenReturn(config);
        when(config.getID()).thenReturn(null);

        AESGCMEncrypt encrypt = new AESGCMEncrypt(configContainer);

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            encrypt.decrypt(DATA);
        });
    }


    @Test
    public void decryptCanReverseEncryption() throws Exception {
        when(configContainer.getConfig()).thenReturn(config);
        when(configContainer.getKey()).thenReturn(validKey);
        when(config.getID()).thenReturn(ID);

        AESGCMEncrypt encrypt = new AESGCMEncrypt(configContainer);

        String encrypted = encrypt.encrypt(DATA);
        String decrypted = encrypt.decrypt(encrypted);

        assertThat(decrypted).isEqualTo(DATA);
    }
}
