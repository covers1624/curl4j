package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Callback;
import net.covers1624.curl4j.core.Memory;
import net.covers1624.curl4j.core.NativeType;

import static net.covers1624.curl4j.core.Memory.*;
import static net.covers1624.curl4j.core.NativeTypes.POINTER_SIZE;

/**
 * A functional interface callback for handling curl headers.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_HEADERFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlHeaderCallback
 */
@FunctionalInterface
public interface CurlHeaderCallbackI extends Callback.CallbackInterface {

    @Override
    default void invoke(@NativeType ("void *") long ret, @NativeType ("void **") long args) {
        long ptr = getAddress(getAddress(args));
        long size = getSizeT(getAddress(args + POINTER_SIZE));
        long nmemb = getSizeT(getAddress(args + 2L * POINTER_SIZE));
        long userdata = getAddress(getAddress(args + 3L * POINTER_SIZE));

        int rs = (int) (size * nmemb);
        onHeader(readUtf8(ptr, rs), userdata);
        putSizeT(ret, rs);
    }

    /**
     * Called for each header.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_HEADERFUNCTION.html">documentation</a>.
     */
    void onHeader(String header, @NativeType ("void *") long userdata);
}
