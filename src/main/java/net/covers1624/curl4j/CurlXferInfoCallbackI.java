package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Callback;
import net.covers1624.curl4j.core.NativeType;

import static net.covers1624.curl4j.core.Memory.*;
import static net.covers1624.curl4j.core.NativeTypes.POINTER_SIZE;

/**
 * A functional interface callback for receiving progress stats.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_XFERINFOFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlXferInfoCallback
 */
@FunctionalInterface
public interface CurlXferInfoCallbackI extends Callback.CallbackInterface {

    @Override
    default void invoke(@NativeType ("void *") long ret, @NativeType ("void **") long args) {
        long ptr = getAddress(getAddress(args));
        long dltotal = getLong(getAddress(args + POINTER_SIZE));
        long dlnow = getLong(getAddress(args + 2L * POINTER_SIZE));
        long ultotal = getLong(getAddress(args + 3L * POINTER_SIZE));
        long ulnow = getLong(getAddress(args + 4L * POINTER_SIZE));

        int r = update(ptr, dltotal, dlnow, ultotal, ulnow);
        putInt(ret, r);
    }

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
