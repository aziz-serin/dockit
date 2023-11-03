package org.dockit.dockitserver.security.keystore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeyStoreManagerTest {

    static final String KEY_STORE_NAME = "keystore.jks";
    static final String KEY_STORE_PASSWORD = "changeit";

    @TempDir
    Path tempDir;

    @Test
    public void createKeyStoreReturnsNullGivenInvalidPath() {
        KeyStore ks = KeyStoreManager.createKeystore(KEY_STORE_NAME, "/", KEY_STORE_PASSWORD);

        assertThat(ks).isNull();
    }

    @Test
    public void createKeyStoreReturnsNullIfPasswordNull() {
        String path = tempDir.toString() + File.separator;
        KeyStore ks = KeyStoreManager.createKeystore(KEY_STORE_NAME, path, null);

        assertThat(ks).isNull();
    }

    @Test
    public void createKeyStoreReturnsNullIfKeyStoreNameNull() {
        String path = tempDir.toString() + File.separator;
        KeyStore ks = KeyStoreManager.createKeystore(null, path, KEY_STORE_PASSWORD);

        assertThat(ks).isNull();
    }

    @Test
    public void createKeyStoreReturnsKeystore() {
        String path = tempDir.toString() + File.separator;
        KeyStore ks = KeyStoreManager.createKeystore(KEY_STORE_NAME, path, KEY_STORE_PASSWORD);
        File keyStoreFile = new File(path + KEY_STORE_NAME);

        assertThat(ks).isInstanceOf(KeyStore.class);
        assertTrue(keyStoreFile.isFile());
        assertFalse(keyStoreFile.isDirectory());
        assertThat(keyStoreFile.getName()).contains(KEY_STORE_NAME);
    }

    @Test
    public void loadKeyStoreReturnsNullIfPasswordNull() {
        String path = tempDir.toString() + File.separator;
        KeyStore ks = KeyStoreManager.loadKeyStore(KEY_STORE_NAME, path, null);

        assertThat(ks).isNull();
    }

    @Test
    public void loadKeyStoreReturnsNullIfPathIsNull() {
        KeyStore ks = KeyStoreManager.loadKeyStore(KEY_STORE_NAME,null, KEY_STORE_PASSWORD);

        assertThat(ks).isNull();
    }

    @Test
    public void loadKeyStoreReturnsNullIfKeyStoreDoesNotExist() {
        String path = tempDir.toString() + File.separator;
        KeyStore ks = KeyStoreManager.loadKeyStore(KEY_STORE_NAME, path, KEY_STORE_PASSWORD);

        assertThat(ks).isNull();
    }

    @Test
    public void loadKeyStoreReturnsNullIfWrongPassword() {
        String path = tempDir.toString() + File.separator;
        KeyStoreManager.createKeystore(KEY_STORE_NAME,path, KEY_STORE_PASSWORD);
        KeyStore ks = KeyStoreManager.loadKeyStore(KEY_STORE_NAME, path, "password");

        assertThat(ks).isNull();
    }

    @Test
    public void loadKeyStoreLoadsKeyStore() throws KeyStoreException {
        String path = tempDir.toString() + File.separator;
        KeyStoreManager.createKeystore(KEY_STORE_NAME,path, KEY_STORE_PASSWORD);
        KeyStore ks = KeyStoreManager.loadKeyStore(KEY_STORE_NAME, path, KEY_STORE_PASSWORD);

        assertThat(ks).isInstanceOf(KeyStore.class);
        assertThat(ks.size()).isEqualTo(0);
    }
}
