package org.dockit.dockitagent.encryption;

/**
 * Constants class containing required constants for key operations
 */
public class KeyConstants {
    // Initialisation vector size for encryption in bytes, so 12 * 8 = 96 bits.
    public static final int IV_SIZE_GCM = 12;

    // GCM tag length in bits
    public static final int GCM_TAG_LENGTH = 128;

    public static final String AES_GCM_CIPHER = "AES/GCM/NoPadding";
}

