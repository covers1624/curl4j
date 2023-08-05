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
 * Created by covers1624 on 3/8/23.
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
     * This function gets called by libcurl instead of its internal equivalent with a frequent interval. While data is
     * being transferred it will be called frequently, and during slow periods like when nothing is being transferred it
     * can slow down to about one call per second.
     * <p>
     * {@code clientp} is the pointer set with {@link CURL#CURLOPT_XFERINFODATA}, it is not used by libcurl but is only passed along
     * from the application to the callback.
     * <p>
     * The callback gets told how much data libcurl will transfer and has transferred, in number of bytes. dltotal is
     * the total number of bytes libcurl expects to download in this transfer. dlnow is the number of bytes
     * downloaded so far. ultotal is the total number of bytes libcurl expects to upload in this transfer.
     * ulnow is the number of bytes uploaded so far.
     * <p>
     * Unknown/unused argument values passed to the callback will be set to zero (like if you only download data,
     * the upload size will remain 0). Many times the callback will be called one or more times first, before it knows
     * the data sizes so a program must be made to handle that.
     * <p>
     * If your callback function returns {@link CURL#CURL_PROGRESSFUNC_CONTINUE} it will cause libcurl to continue
     * executing the default progress function.
     * <p>
     * Returning any other non-zero value from this callback will cause libcurl to abort the transfer and return
     * {@link CURL#CURLE_ABORTED_BY_CALLBACK}.
     * <p>
     * If you transfer data with the multi interface, this function will not be called during periods of idleness
     * unless you call the appropriate libcurl function that performs transfers.
     * <p>
     * {@link CURL#CURLOPT_NOPROGRESS} must be set to 0 to make this function actually get called.
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
