package net.covers1624.curl4j;

import net.covers1624.curl4j.core.NativeType;
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
public class CurlHeaderCallback extends CurlCallback {

    private static final long cif = ffi_prep_cif(
            ffi_type_pointer,
            ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer
    );
    private static final long callback = ffi_callback(Reflect.getMethod(CurlHeaderCallbackI.class, "read", String.class, long.class));

    public CurlHeaderCallback(CurlHeaderCallbackI delegate) {
        super(cif, callback, delegate);
    }

    private static native long ffi_callback(Method method);

    public interface CurlHeaderCallbackI extends CallbackInterface {

        /**
         * Called for each header.
         * <p>
         * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_HEADERFUNCTION.html">documentation</a>.
         */
        void onHeader(String header, @NativeType ("void *") long userdata);
    }
}
