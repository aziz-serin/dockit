package org.dockit.dockitserver.security.key;

/**
 * Constants class containing required constants for key operations
 */
public class KeyConstants {

    public static final String AES_CIPHER = "AES";

    // AES key size for encrypting/decrypting data
    public static final int ENCRYPTION_KEY_SIZE = 128;

    // AES key size for JWT keys
    public static final int JWT_KEY_SIZE = 256;

    // Initialisation vector size for encryption in bytes, so 12 * 8 = 96 bits.
    public static final int IV_SIZE_GCM = 12;

    // Initialisation vector size for encryption in bytes, so 16 * 8 = 128 bits.
    public static final int IV_SIZE_CBC = 16;

    // GCM tag length in bits
    public static final int GCM_TAG_LENGTH = 128;

    public static final String AES_GCM_CIPHER = "AES/GCM/NoPadding";

    public static final String AES_CBC_CIPHER = "AES/CBC/PKCS5Padding";

    // Used as an alias for the encryption key for the database
    public static final String DB_KEY_ALIAS = "database_encryption";
}
