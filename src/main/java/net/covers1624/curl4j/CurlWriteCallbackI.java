package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Callback;

import java.io.IOException;

/**
 * A functional interface callback for writing data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlWriteCallback
 */
@FunctionalInterface
public interface CurlWriteCallbackI extends Callback.CallbackInterface {

    /**
     * Called to empty the curl buffer.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html">documentation</a>.
     *
     * @throws IOException If an error occurred whilst processing the bytes.
     *                     If the curl operation is running on a Java thread, this will bubble out. Otherwise, it will
     *                     be printed to stderr, and ignored.
     */
    long write(long ptr, long size, long nmemb, long userdata) throws IOException;
}
