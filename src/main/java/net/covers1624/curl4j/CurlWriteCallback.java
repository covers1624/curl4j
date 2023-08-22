package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Reflect;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * A function callback for writing data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlWriteCallbackI
 */
public class CurlWriteCallback extends CurlCallback implements CurlWriteCallbackI {

    private static final long cif = ffi_prep_cif(
            ffi_type_pointer,
            ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer
    );

    private static final long callback = ffi_callback(Reflect.getMethod(CurlWriteCallbackI.class, "write", long.class, long.class, long.class, long.class));

    public CurlWriteCallback(CurlWriteCallbackI delegate) {
        super(cif, callback, delegate);
    }

    protected CurlWriteCallback() {
        super(cif, callback, null);
    }

    @Override
    public long write(long ptr, long size, long nmemb, long userdata) throws IOException {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }

    private static native long ffi_callback(Method method);
}
