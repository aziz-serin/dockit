package org.dockit.dockitserver.config.utils;

/**
 * Containing utilities regarding the current operating system.
 */
public class OSUtils {

    /**
     * Detects the current OS
     */
    public static final class OSDetector {
        private static String OS;

        /**
         * @return String containing the current operating system
         */
        public static String getOS() {
            if (OS == null) {
                OS = System.getProperty("os.name");
            }
            return OS;
        }

        /**
         * @return true if the OS is windows, false otherwise
         */
        public static boolean isWindows() {
            return getOS().startsWith("Windows");
        }
    }
}