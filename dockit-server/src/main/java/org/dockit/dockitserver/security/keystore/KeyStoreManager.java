package org.dockit.dockitserver.security.keystore;

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

public class KeyStoreManager {
    private static final Logger logger = LoggerFactory.getLogger(KeyStoreManager.class);

    public static KeyStore loadKeyStore(String keyStoreName, String path, String password) {
        if (path == null) {
            return null;
        }
        return openKeyStore(path + keyStoreName, password);
    }

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
            logger.error("Could not read the keyStore in the given path, {}", path);
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

    private static KeyStore openKeyStore(String path, String password) {
        if (password == null) {
            return null;
        }
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
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
            logger.error("Could not read the keyStore in the given path, {}", path);
            return null;
        } catch (NoSuchAlgorithmException e) {
            logger.error("Specified algorithm does not exist: \n {}", e.getMessage());
            return null;
        }
    }
}
