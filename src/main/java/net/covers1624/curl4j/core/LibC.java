package net.covers1624.curl4j.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;

/**
 * Represents the available LibC types.
 *
 * @author covers1624
 */
public enum LibC {
    GNU,
    MUSL,
    UNKNOWN;

    private static final boolean DEBUG_LIBC = Boolean.getBoolean("net.covers1624.curl4j.debug_libc");
    private static final boolean IS_LINUX = OperatingSystem.CURRENT.isLinux();
    public static final LibC CURRENT = IS_LINUX ? probeLibC() : UNKNOWN;

    public static final String LIBC_SUFFIX = IS_LINUX ? "-" + CURRENT.getSuffix() : "";

    public String getSuffix() {
        // If we don't know, just assume GNU, its the common variant.
        if (this == UNKNOWN) return GNU.getSuffix();
        return name().toLowerCase(Locale.ROOT);
    }

    private static LibC probeLibC() {
        try {
            Process process = new ProcessBuilder("ldd", "--version")
                    .redirectErrorStream(true)
                    .start();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (InputStream is = process.getInputStream()) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf, 0, 1024)) != -1) {
                    bos.write(buf, 0, len);
                }
            }

            String str = bos.toString("UTF-8");
            if (str.contains("GNU libc") || str.contains("GLIBC")) return GNU;
            if (str.contains("musl")) return MUSL;
            if (DEBUG_LIBC) {
                System.err.println("[curl4j] Failed to detect LibC variant from LDD output: " + str);
            }
            return UNKNOWN;
        } catch (Throwable ex) {
            if (DEBUG_LIBC) {
                System.err.println("[curl4j] Failed to detect LibC version.");
                ex.printStackTrace(System.err);
            }
            return UNKNOWN;
        }
    }
}
