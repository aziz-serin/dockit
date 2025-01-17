package org.dockit.dockitserver.security.keystore;

import org.dockit.dockitserver.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Class to manage the handling of the application's keystore.
 */
public class KeyStoreManager {
    private static final Logger logger = LoggerFactory.getLogger(KeyStoreManager.class);

    /**
     * Loads a keystore from the filesystem
     *
     * @param keyStoreName name of the keystore
     * @param path path to the given keystore
     * @param password password for the keystore
     * @return null if the path is null, {@link KeyStore} if not
     */
    public static KeyStore loadKeyStore(String keyStoreName, String path, String password) {
        if (path == null) {
            return null;
        }
        return openKeyStore(path + keyStoreName, password);
    }

    /**
     * Create a keystore and save it to the filesystem
     *
     * @param keyStoreName name of the keystore
     * @param path path to save the keystore to
     * @param password password to save the keystore with
     * @return null if an error occurs, {@link KeyStore} if not
     */
    public static KeyStore createKeystore(String keyStoreName, String path, String password) {
        if (keyStoreName == null) {
            return null;
        }
        KeyStore ks = openKeyStore(null, password);
        if (ks == null) {
            return null;
        }
        try (FileOutputStream fos = new FileOutputStream(path + keyStoreName)) {
            char[] pwdArray = password.toCharArray();
            ks.store(fos, pwdArray);
            return ks;
        } catch (FileNotFoundException e) {
            logger.error("Could not find the keyStore in the given path, {}", path);
            return null;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        } catch (CertificateException e) {
            logger.error("Invalid certificate, check exception, \n {}", e.getMessage());
            return null;
        } catch (KeyStoreException e) {
            logger.error("Could not create the keystore, check the exception: \n {}", e.getMessage());
            return null;
        } catch (NoSuchAlgorithmException e) {
            logger.error("Specified algorithm does not exist: \n {}", e.getMessage());
            return null;
        }
    }

    public static void saveKeyStore(KeyStore keyStore) {
        try (FileOutputStream fos = new FileOutputStream(ConfigManager.KEY_STORE_PATH)) {
            char[] pwdArray = ConfigManager.KEY_STORE_PASSWORD.toCharArray();
            keyStore.store(fos, pwdArray);
        } catch (FileNotFoundException e) {
            logger.error("Could not find the keyStore in the given path, {}", ConfigManager.KEY_STORE_PATH);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (CertificateException e) {
            logger.error("Invalid certificate, check exception, \n {}", e.getMessage());
        } catch (KeyStoreException e) {
            logger.error("Could not create the keystore, check the exception: \n {}", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Specified algorithm does not exist: \n {}", e.getMessage());
        }
    }

    private static KeyStore openKeyStore(String path, String password) {
        if (password == null) {
            return null;
        }
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            char[] pwdArray = password.toCharArray();
            // This creates the keyStore
            if (path == null) {
                keyStore.load(null, pwdArray);
            }
            // This loads an existing keystore from filesystem
            else {
                keyStore.load(new FileInputStream(path), pwdArray);
            }
            return keyStore;
        } catch (KeyStoreException e) {
            logger.error("Could not create the keystore, check the exception: \n {}", e.getMessage());
            return null;
        } catch (FileNotFoundException e) {
            logger.error("Could not find the keyStore in the given path, {}", path);
            return null;
        } catch (CertificateException e) {
            logger.error("Invalid certificate, check exception, \n {}", e.getMessage());
            return null;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        } catch (NoSuchAlgorithmException e) {
            logger.error("Specified algorithm does not exist: \n {}", e.getMessage());
            return null;
        }
    }
}
