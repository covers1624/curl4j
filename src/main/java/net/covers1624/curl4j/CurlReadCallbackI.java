package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Callback;

import java.io.IOException;

/**
 * A functional interface callback for reading POST/PUT data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlReadCallback
 */
@FunctionalInterface
public interface CurlReadCallbackI extends Callback.CallbackInterface {

    /**
     * Called to fill the curl buffer with data.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">documentation</a>.
     *
     * @throws IOException If an error occurred whilst processing the bytes.
     *                     If the curl operation is running on a Java thread, this will bubble out. Otherwise, it will
     *                     be printed to stderr, and ignored.
     */
    long read(long ptr, long size, long nmemb, long userdata) throws IOException;
}
