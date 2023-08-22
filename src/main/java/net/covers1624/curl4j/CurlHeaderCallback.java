package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Reflect;

import java.lang.reflect.Method;

/**
 * A function callback for handling curl headers.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_HEADERFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlHeaderCallbackI
 */
public class CurlHeaderCallback extends CurlCallback implements CurlHeaderCallbackI {

    private static final long cif = ffi_prep_cif(
            ffi_type_pointer,
            ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer
    );
    private static final long callback = ffi_callback(Reflect.getMethod(CurlHeaderCallbackI.class, "onHeader", String.class, long.class));

    public CurlHeaderCallback(CurlHeaderCallbackI delegate) {
        super(cif, callback, delegate);
    }

    protected CurlHeaderCallback() {
        super(cif, callback, null);
    }

    @Override
    public void onHeader(String header, long userdata) {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }

    private static native long ffi_callback(Method method);
}
