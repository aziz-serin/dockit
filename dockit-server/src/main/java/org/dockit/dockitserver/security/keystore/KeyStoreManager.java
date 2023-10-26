package org.dockit.dockitserver.security.keystore;

import org.dockit.dockitserver.exceptions.security.KeyStoreManagerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeyStoreManager {
    private static final Logger logger = LoggerFactory.getLogger(KeyStoreManager.class);

    public static KeyStore loadKeyStore(String path, String password) {
        return openKeyStore(path, password);
    }

    public static KeyStore createKeystore(String keyStoreName, String path, String password) {
        KeyStore ks = openKeyStore(null, password);
        try (FileOutputStream fos = new FileOutputStream("newKeyStoreFileName.jks")) {
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
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            char[] pwdArray = password.toCharArray();
            keyStore.load(null, pwdArray);
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
