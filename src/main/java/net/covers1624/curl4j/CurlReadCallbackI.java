package net.covers1624.curl4j;

import java.io.IOException;
import java.lang.foreign.MemorySegment;

/**
 * A functional interface callback for reading POST/PUT data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlReadCallback
 */
@FunctionalInterface
public interface CurlReadCallbackI {

    /**
     * Called to fill the curl buffer with data.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">documentation</a>.
     *
     * @throws IOException If an error occurred whilst processing the bytes.
     *                     If the curl operation is running on a Java thread, this will bubble out. Otherwise, it will
     *                     be printed to stderr, and ignored.
     */
    long read(MemorySegment ptr, long size, long nmemb, MemorySegment userdata) throws IOException;
}
