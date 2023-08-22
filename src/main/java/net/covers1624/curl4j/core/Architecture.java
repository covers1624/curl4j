package net.covers1624.curl4j.core;

import java.util.Locale;

/**
 * Created by covers1624 on 15/8/23.
 */
public enum Architecture {
    X32,
    X64,
    ARM32,
    ARM64,
    UNKNOWN;

    public static final Architecture CURRENT = Architecture.parse(System.getProperty("os.arch"));

    public String lowerName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Architecture parse(String arch) {
        switch (arch.toLowerCase(Locale.ROOT)) {
            case "i386":
            case "x86":
                return X32;
            case "x64":
            case "x86_64":
            case "amd64":
                return X64;
            case "arm":
            case "armv7":
            case "armv7l":
                return ARM32;
            case "armv8":
            case "aarch64":
                return ARM64;
            default:
                return UNKNOWN;
        }
    }
}
