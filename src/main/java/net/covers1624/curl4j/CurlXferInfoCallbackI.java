package net.covers1624.curl4j;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memPutInt;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;

/**
 * A functional interface callback for receiving progress stats.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_XFERINFOFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlXferInfoCallback
 */
public interface CurlXferInfoCallbackI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
            FFI_DEFAULT_ABI,
            ffi_type_pointer,
            ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer
    );

    default FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    default void callback(long ret, long args) {
        long ptr = memGetAddress(memGetAddress(args));
        long dltotal = memGetAddress(memGetAddress(args + POINTER_SIZE));
        long donow = memGetAddress(memGetAddress(args + 2L * POINTER_SIZE));
        long ultotal = memGetAddress(memGetAddress(args + 3L * POINTER_SIZE));
        long ulnow = memGetAddress(memGetAddress(args + 4L * POINTER_SIZE));
        memPutInt(ret, invoke(ptr, dltotal, donow, ultotal, ulnow));
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
    @NativeType ("int")
    int invoke(
            @NativeType ("void *") long ptr,
            @NativeType ("curl_off_t") long dltotal,
            @NativeType ("curl_off_t") long dlnow,
            @NativeType ("curl_off_t") long ultotal,
            @NativeType ("curl_off_t") long ulnow);
}
