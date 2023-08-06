package net.covers1624.curl4j;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;

/**
 * A functional interface callback for handling curl headers.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_HEADERFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlHeaderCallback
 */
@FunctionalInterface
public interface CurlHeaderCallbackI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
            FFI_DEFAULT_ABI,
            ffi_type_pointer,
            ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer
    );

    @Override
    default FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    default void callback(long ret, long args) {
        long ptr = memGetAddress(memGetAddress(args));
        long size = memGetAddress(memGetAddress(args + POINTER_SIZE));
        long nmemb = memGetAddress(memGetAddress(args + 2L * POINTER_SIZE));
        long userdata = memGetAddress(memGetAddress(args + 3L * POINTER_SIZE));

        long rs = size * nmemb;
        String string = memUTF8(ptr, (int) rs);

        invoke(string, userdata);
        memPutCLong(ret, rs);
    }

    /**
     * Called for each header.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_HEADERFUNCTION.html">documentation</a>.
     */
    void invoke(String header, @NativeType ("void *") long userdata);
}
