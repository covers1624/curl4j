package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Callback;
import net.covers1624.curl4j.core.Reflect;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * A function callback for reading POST/PUT data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 */
public class CurlReadCallback extends CurlCallback {

    private static final long cif = ffi_prep_cif(
            ffi_type_pointer,
            ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer
    );
    private static final long callback = ffi_callback(Reflect.getMethod(CurlReadCallback.CurlReadCallbackI.class, "read", long.class, long.class, long.class, long.class));

    public CurlReadCallback(CurlReadCallbackI delegate) {
        super(cif, callback, delegate);
    }

    private static native long ffi_callback(Method method);

    public interface CurlReadCallbackI extends CallbackInterface {

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
}
