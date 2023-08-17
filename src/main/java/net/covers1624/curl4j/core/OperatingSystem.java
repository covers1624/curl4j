package net.covers1624.curl4j.core;

import java.util.Locale;

/**
 * Created by covers1624 on 15/8/23.
 */
public enum OperatingSystem {
    WINDOWS,
    LINUX,
    MACOS,
    FREEBSD,
    UNKNOWN;

    public static final OperatingSystem CURRENT = parse(System.getProperty("os.name"));

    public String lowerName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static OperatingSystem parse(String name) {
        name = name.toLowerCase(Locale.ROOT);
        if (name.contains("windows")) return WINDOWS;
        if (name.contains("linux")) return LINUX;
        if (name.contains("osx") || name.contains("os x") || name.contains("darwin")) return MACOS;
        if (name.contains("freebsd")) return FREEBSD;
        return UNKNOWN;
    }
}
