package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Pointer;
import net.covers1624.curl4j.util.CurlBindable;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.covers1624.curl4j.CURL.*;

/**
 * Wrapper around a {@link curl_blob} for {@link CURL#CURLOPT_CAINFO_BLOB} and/or {@link CURL#CURLOPT_PROXY_CAINFO_BLOB}.
 * <p>
 * The built-in bundle is obtained from the Firefox NSS builtin-list, as per the curl 'mk-ca-bundle.pl' script in the curl
 * sources. This is updated periodically and/or as required.
 * <p>
 * You may choose to create/maintain your own bundle either as an embedded resource, or from a FileSystem path. You may
 * create these custom bundles with {@link #newBundleFromResource} and {@link #newBundleFromPath}.
 * <p>
 * You may choose to override the default CABundle via {@link #setDefault} this will cause all calls to {@link #getDefault} to
 * use the new default. By default, the built-in bundle is used. This may always be obtained via {@link #builtIn}.
 * <p>
 *
 * @author covers1624
 */
public final class CABundle implements CurlBindable {

    private static final CABundle BUILT_IN;
    private static CABundle DEFAULT;

    static {
        try {
            BUILT_IN = newBundleFromResource("/META-INF/ca-bundle.crt");
        } catch (IOException ex) {
            throw new RuntimeException("Failed to initialize default CABundle.", ex);
        }
        DEFAULT = BUILT_IN;
    }

    private final Arena arena = Arena.ofShared();
    private final curl_blob blob = new curl_blob(arena);

    public CABundle(byte[] data) {
        blob.setData(arena.allocateFrom(ValueLayout.JAVA_BYTE, data));
        blob.setLen((long) data.length);
        blob.setFlags(curl_blob.CURL_BLOB_NOCOPY);
    }

    /**
     * Get the builtin CABundle.
     *
     * @return The bundle.
     */
    public static CABundle builtIn() {
        return BUILT_IN;
    }

    /**
     * Get the default CABundle.
     *
     * @return The CABundle.
     */
    public static CABundle getDefault() {
        return DEFAULT;
    }

    /**
     * Override the default CABundle.
     *
     * @param bundle The bundle.
     */
    public static void setDefault(CABundle bundle) {
        DEFAULT = bundle;
    }

    /**
     * Create a new CABundle from the given embedded resource.
     *
     * @param resource The resource.
     * @return The CABundle.
     */
    public static CABundle newBundleFromResource(String resource) throws IOException {
        try (InputStream is = CABundle.class.getResourceAsStream(resource)) {
            if (is == null) throw new FileNotFoundException("Embedded resource does not exist: " + resource);

            return new CABundle(is.readAllBytes());
        }
    }

    /**
     * Create a new CABundle from the given FileSystem path.
     *
     * @param path The path.
     * @return THe CABundle.
     */
    public static CABundle newBundleFromPath(Path path) throws IOException {
        return new CABundle(Files.readAllBytes(path));
    }

    /**
     * Apply this CABundle to the given curl handle.
     * <p>
     * Sets {@link CURL#CURLOPT_CAINFO_BLOB} and {@link CURL#CURLOPT_PROXY_CAINFO_BLOB}.
     *
     * @param curl The curl instance.
     */
    @Override
    public void apply(MemorySegment curl) {
        curl_easy_setopt(curl, CURLOPT_CAINFO_BLOB, getCABlob());
        curl_easy_setopt(curl, CURLOPT_PROXY_CAINFO_BLOB, getCABlob());
    }

    /**
     * Get the {@link curl_blob} loaded from this CABundle.
     *
     * @return The blob.
     */
    public curl_blob getCABlob() {
        return blob;
    }
}
