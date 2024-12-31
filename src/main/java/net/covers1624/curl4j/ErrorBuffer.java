package net.covers1624.curl4j;

import net.covers1624.curl4j.util.CurlBindable;
import net.covers1624.curl4j.util.CurlHandle;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * A buffer for CURL to put error messages.
 * <p>
 * Created by covers1624 on 16/1/24.
 */
public class ErrorBuffer implements CurlBindable {

    /**
     * The default size of an error buf as of curl 8.2.1.
     */
    public static final int CURL_ERROR_SIZE = 256;

    private static int ERROR_BUFFER_SIZE = CURL_ERROR_SIZE;

    /**
     * Adjust the default error buffer size.
     *
     * @param size The new size.
     */
    public static void setErrorBufferSize(int size) {
        if (size > ERROR_BUFFER_SIZE) {
            ERROR_BUFFER_SIZE = size;
        }
    }

    // Keep arena in scope for GC cleanup.
    @SuppressWarnings ("FieldCanBeLocal")
    private final Arena arena = Arena.ofAuto();

    /**
     * The size of the error buffer.
     */
    public final int size;
    public final MemorySegment buffer;

    /**
     * Construct an error buffer with the default size.
     */
    public ErrorBuffer() {
        this(ERROR_BUFFER_SIZE);
    }

    /**
     * Construct an error buffer with a custom size.
     *
     * @param size The size in bytes/chars.
     */
    public ErrorBuffer(int size) {
        this.size = size;
        buffer = arena.allocate(ValueLayout.JAVA_BYTE, size);
    }

    /**
     * Clear the buffer.
     */
    public void clear() {
        buffer.set(ValueLayout.JAVA_BYTE, 0, (byte) 0);
    }

    /**
     * Read the contents of the buffer.
     *
     * @return The buffer contents.
     */
    @Override
    public String toString() {
        return buffer.getString(0);
    }

    @Override
    public void apply(CurlHandle handle) {
        apply(handle.curl);
    }

    @Override
    public void apply(MemorySegment curl) {
        CURL.curl_easy_setopt(curl, CURL.CURLOPT_ERRORBUFFER, buffer);
    }
}
