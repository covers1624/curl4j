package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Reflect;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * A function callback for reading POST/PUT data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlReadCallbackI
 */
public class CurlReadCallback extends CurlCallback implements CurlReadCallbackI {

    private static final long cif = ffi_prep_cif(
            ffi_type_pointer,
            ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer
    );
    private static final long callback = ffi_callback(Reflect.getMethod(CurlReadCallbackI.class, "read", long.class, long.class, long.class, long.class));

    public CurlReadCallback(CurlReadCallbackI delegate) {
        super(cif, callback, delegate);
    }

    protected CurlReadCallback() {
        super(cif, callback, null);
    }

    @Override
    public long read(long ptr, long size, long nmemb, long userdata) throws IOException {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }

    private static native long ffi_callback(Method method);
}
