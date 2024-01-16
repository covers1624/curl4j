package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Memory;
import net.covers1624.curl4j.util.CurlBindable;
import net.covers1624.curl4j.util.CurlHandle;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.nio.ByteBuffer;

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

    /**
     * The size of the error buffer.
     */
    public final int size;
    @VisibleForTesting
    final ByteBuffer buf;
    public final long address;

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
        buf = ByteBuffer.allocateDirect(size);
        address = Memory.getDirectByteBufferAddress(buf);
    }

    /**
     * Clear the buffer.
     */
    public void clear() {
        Memory.putByte(address, (byte) 0);
    }

    /**
     * Read the contents of the buffer.
     *
     * @return The buffer contents.
     */
    @Override
    public String toString() {
        int len = 0;
        while (len < size && Memory.getByte(address + len) != 0) {
            len++;
        }
        if (len == 0) return "";
        // Curl docs say this should not be possible, but we did not find a null char, so there must be _some_ data in the buffer
        if (len == size) return Memory.readUtf8(address, size);
        return Memory.readUtf8(address);
    }

    @Override
    public void apply(CurlHandle handle) {
        apply(handle.curl);
    }

    @Override
    public void apply(long curl) {
        CURL.curl_easy_setopt(curl, CURL.CURLOPT_ERRORBUFFER, address);
    }
}
