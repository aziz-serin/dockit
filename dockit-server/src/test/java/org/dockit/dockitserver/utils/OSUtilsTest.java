package org.dockit.dockitserver.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OSUtilsTest {
    static String os;

    @BeforeAll
    public static void setup() {
        os = OSUtils.OSDetector.getOS();
    }

    @Test
    public void getOSReturnsTrueOsForMac() {
        assertThat(os).startsWithIgnoringCase("mac");
    }

    // Enable this test when developing on linux
    @Test
    @Disabled
    public void getOsReturnsTrueOsForLinux() {
        assertThat(os).startsWithIgnoringCase("linux");
    }

    // Enable this test when developing on windows
    @Test
    @Disabled
    public void getOsReturnsTrueOsForWindows() {
        assertThat(os).startsWithIgnoringCase("windows");
    }

    // Invert this testcase if developing on windows
    @Test
    public void isWindowsReturnsTrue() {
        assertThat(OSUtils.OSDetector.isWindows()).isFalse();
    }
}
