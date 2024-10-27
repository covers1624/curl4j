package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Callback;
import net.covers1624.curl4j.core.NativeType;

import java.io.IOException;

import static net.covers1624.curl4j.core.Memory.*;
import static net.covers1624.curl4j.core.Memory.putSizeT;
import static net.covers1624.curl4j.core.NativeTypes.POINTER_SIZE;

/**
 * A functional interface callback for seeking the curl input.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_SEEKFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlSeekCallback
 */
public interface CurlSeekCallbackI extends Callback.CallbackInterface {

    @Override
    default void invoke(@NativeType ("void *") long ret, @NativeType ("void **") long args) throws IOException {
        try {
            long userdata = getAddress(getAddress(args));
            long offset = getSizeT(getAddress(args + POINTER_SIZE));
            int origin = getInt(getAddress(args + 2L * POINTER_SIZE));

            int r = seek(userdata, offset, origin);
            putInt(ret, r);
        } catch (Throwable ex) {
            putInt(ret, CURL.CURL_SEEKFUNC_FAIL);
            throw ex;
        }
    }

    /**
     * Called to seek the curl input buffer.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_SEEKFUNCTION.html">documentation</a>.
     *
     * @throws IOException If an error occurred whilst processing the bytes.
     *                     If the curl operation is running on a Java thread, this will bubble out. Otherwise, it will
     *                     be printed to stderr, and ignored.
     */
    int seek(long userdata, long offset, int origin) throws IOException;
}
