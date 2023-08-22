package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Reflect;

import java.lang.reflect.Method;

/**
 * A function callback for receiving progress stats.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_XFERINFOFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlXferInfoCallbackI
 */
public class CurlXferInfoCallback extends CurlCallback implements CurlXferInfoCallbackI {

    private static final long cif = ffi_prep_cif(
            ffi_type_int,
            ffi_type_pointer, ffi_type_long, ffi_type_long, ffi_type_long, ffi_type_long
    );
    private static final long callback = ffi_callback(Reflect.getMethod(CurlXferInfoCallbackI.class, "update", long.class, long.class, long.class, long.class, long.class));

    public CurlXferInfoCallback(CurlXferInfoCallbackI delegate) {
        super(cif, callback, delegate);
    }

    protected CurlXferInfoCallback() {
        super(cif, callback, null);
    }

    @Override
    public int update(long ptr, long dltotal, long dlnow, long ultotal, long ulnow) {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }

    private static native long ffi_callback(Method method);
}
