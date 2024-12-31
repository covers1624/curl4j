package net.covers1624.curl4j;

import java.io.IOException;
import java.lang.foreign.MemorySegment;

/**
 * A functional interface callback for seeking the curl input.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_SEEKFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlSeekCallback
 */
public interface CurlSeekCallbackI {

    /**
     * Called to seek the curl input buffer.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_SEEKFUNCTION.html">documentation</a>.
     *
     * @throws IOException If an error occurred whilst processing the bytes.
     *                     If the curl operation is running on a Java thread, this will bubble out. Otherwise, it will
     *                     be printed to stderr, and ignored.
     */
    int seek(MemorySegment userdata, long offset, int origin) throws IOException;
}
