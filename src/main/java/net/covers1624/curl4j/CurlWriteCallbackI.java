package net.covers1624.curl4j;

import java.io.IOException;
import java.lang.foreign.MemorySegment;

/**
 * A functional interface callback for writing data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlWriteCallback
 */
@FunctionalInterface
public interface CurlWriteCallbackI {

    /**
     * Called to empty the curl buffer.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html">documentation</a>.
     *
     * @throws IOException If an error occurred whilst processing the bytes.
     *                     If the curl operation is running on a Java thread, this will bubble out. Otherwise, it will
     *                     be printed to stderr, and ignored.
     */
    long write(MemorySegment ptr, long size, long nmemb, MemorySegment userdata) throws IOException;
}
