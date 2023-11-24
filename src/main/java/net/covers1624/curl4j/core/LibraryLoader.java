package net.covers1624.curl4j.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author covers1624
 */
public class LibraryLoader {

    private static final String LIB_PATH = System.getProperty("net.covers1624.curl4j.lib_path");
    private static final boolean NO_EMBEDDED = Boolean.getBoolean("net.covers1624.curl4j.no_embedded");

    static {
        loadJNILibrary(System.getProperty("net.covers1624.curl4j.libcurl4j.name", "curl4j"));
    }

    /**
     * Called to trigger static init of our own native bindings.
     */
    public static void initialize() {
    }

    public static Library loadLibrary(String libName) throws UnsatisfiedLinkError {
        // First try absolute path.
        if (Paths.get(libName).isAbsolute()) {
            return loadLib(libName);
        }

        // Next, try and use the system property extracted dir override.
        for (String libPath : getLibPaths(libName)) {
            if (LIB_PATH != null) {
                Path path = Paths.get(LIB_PATH, libPath);
                if (Files.exists(path)) {
                    return loadLib(path.toAbsolutePath().toString());
                }
            }
        }

        // Try and load embedded
        if (!NO_EMBEDDED) {
            for (String libPath : getLibPaths(libName)) {
                URL url = LibraryLoader.class.getResource("/META-INF/natives/" + libPath);
                if (url != null) {
                    Path path = getAsAbsolutePath(url);
                    if (path == null) {
                        path = extract(url, libName);
                    }
                    return loadLib(path.toAbsolutePath().toString());
                }
            }
        }

        // TODO better error messages, stating where we searched, etc.
        // We tried, just sendit and see what happens!
        return loadLib(System.mapLibraryName(libName));
    }

    private static Library loadLib(String libName) {
        switch (OperatingSystem.CURRENT) {
            case WINDOWS:
                return new Library.WindowsLibrary(libName);
            case LINUX:
                return new Library.LinuxLibrary(libName);
            case MACOS:
                return new Library.MacosLibrary(libName);
            case FREEBSD:
                break;
            case UNKNOWN:
                break;
        }
        throw new UnsupportedOperationException("Unknown/unsupported Operating System. " + OperatingSystem.CURRENT);
    }

    /**
     * Load a JNI library.
     *
     * @param libName The name or path of the library to load.
     * @throws UnsatisfiedLinkError If the library was not found.
     */
    public static void loadJNILibrary(String libName) throws UnsatisfiedLinkError {
        // First try as absolute path.
        if (Paths.get(libName).isAbsolute()) {
            System.load(libName);
            return;
        }

        // Next, try and use the system property extracted dir override.
        for (String libPath : getLibPaths(libName)) {
            if (LIB_PATH != null) {
                Path path = Paths.get(LIB_PATH, libPath);
                if (Files.exists(path)) {
                    System.load(path.toAbsolutePath().toString());
                    return;
                }
            }
        }

        // Try and load embedded
        if (!NO_EMBEDDED) {
            for (String libPath : getLibPaths(libName)) {
                URL url = LibraryLoader.class.getResource("/META-INF/natives/" + libPath);
                if (url != null) {
                    Path path = getAsAbsolutePath(url);
                    if (path == null) {
                        path = extract(url, libName);
                    }
                    System.load(path.toAbsolutePath().toString());
                    return;
                }
            }
        }

        // TODO better error messages, stating where we searched, etc.
        // We tried, just sendit and see what happens!
        System.loadLibrary(libName);
    }

    private static String[] getLibPaths(String libName) {
        String path = OperatingSystem.CURRENT.lowerName() + "/" + Architecture.CURRENT.lowerName() + "/";
        if (OperatingSystem.CURRENT.isLinux()) {
            return new String[] {
                    path + libName + LibC.LIBC_SUFFIX,
                    path + libName
            };
        }
        return new String[] { path + libName };
    }

    private static Path getAsAbsolutePath(URL url) {
        if (url.getProtocol().equals("file")) {
            try {
                Path file = Paths.get(url.toURI());
                if (file.isAbsolute() && Files.isReadable(file)) {
                    return file;
                }
            } catch (URISyntaxException ignored) {
            }
        }
        return null;
    }

    private static Path extract(URL url, String libName) {
        try {
            Path tempDir = Files.createTempDirectory("libcurl4j-natives");
            tempDir.toFile().deleteOnExit();

            Path libPath = tempDir.resolve(System.mapLibraryName(libName));

            try (InputStream is = url.openStream()) {
                Files.copy(is, libPath);
            }
            return libPath;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to extract library.", ex);
        }
    }
}
