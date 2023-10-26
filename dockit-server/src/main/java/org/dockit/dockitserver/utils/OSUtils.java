package org.dockit.dockitserver.utils;

public class OSUtils {

    public static final class OSDetector {
        private static String OS;

        public static String getOS() {
            if (OS == null) {
                OS = System.getProperty("os.name");
            }
            return OS;
        }

        public static boolean isWindows() {
            return getOS().startsWith("Windows");
        }
    }
}