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
public class CurlXferInfoCallback extends CurlCallback {

    private static final long cif = ffi_prep_cif(
            ffi_type_int,
            ffi_type_pointer, ffi_type_long, ffi_type_long, ffi_type_long, ffi_type_long
    );
    private static final long callback = ffi_callback(Reflect.getMethod(CurlXferInfoCallbackI.class, "update", long.class, long.class, long.class, long.class, long.class));

    public CurlXferInfoCallback(CurlXferInfoCallbackI delegate) {
        super(cif, callback, delegate);
    }

    private static native long ffi_callback(Method method);

    public interface CurlXferInfoCallbackI extends CallbackInterface {

        /**
         * Called to receive the transfer progress statistics.
         * <p>
         * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_XFERINFOFUNCTION.html">documentation</a>.
         *
         * @param ptr     User pointer set by {@link CURL#CURLOPT_XFERINFODATA}.
         * @param dltotal The total expected to be downloaded.
         * @param dlnow   The amount downloaded.
         * @param ultotal The total expected to be uploaded.
         * @param ulnow   The amount uploaded.
         * @return {@link CURL#CURLE_OK} or {@link CURL#CURL_PROGRESSFUNC_CONTINUE},
         * negative values fail the transfer with {@link CURL#CURLE_ABORTED_BY_CALLBACK}.
         */
        int update(long ptr, long dltotal, long dlnow, long ultotal, long ulnow);
    }
}
