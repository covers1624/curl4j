package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Callback;
import net.covers1624.curl4j.core.Reflect;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * A function callback for writing data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 */
public class CurlWriteCallback extends Callback {

    private static final long cif = ffi_prep_cif(
            ffi_type_pointer,
            ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer
    );

    private static final long callback = ffi_callback(Reflect.getMethod(WriteCallbackI.class, "write", long.class, long.class, long.class, long.class));

    public CurlWriteCallback(WriteCallbackI delegate) {
        super(cif, callback, delegate);
    }

    private static native long ffi_callback(Method method);

    public interface WriteCallbackI extends CallbackInterface {

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
}
