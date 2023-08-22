package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Callback;
import net.covers1624.curl4j.core.NativeType;

/**
 * A functional interface callback for handling curl headers.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_HEADERFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlHeaderCallback
 */
@FunctionalInterface
public interface CurlHeaderCallbackI extends Callback.CallbackInterface {

    /**
     * Called for each header.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_HEADERFUNCTION.html">documentation</a>.
     */
    void onHeader(String header, @NativeType ("void *") long userdata);
}
