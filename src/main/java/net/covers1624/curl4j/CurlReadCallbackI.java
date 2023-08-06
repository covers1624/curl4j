package net.covers1624.curl4j;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

import java.io.IOException;

import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memPutCLong;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;

/**
 * A functional interface callback for reading POST/PUT data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlReadCallback
 */
@FunctionalInterface
public interface CurlReadCallbackI extends CallbackI {

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
        try {
            long ptr = memGetAddress(memGetAddress(args));
            long size = memGetAddress(memGetAddress(args + POINTER_SIZE));
            long nmemb = memGetAddress(memGetAddress(args + 2L * POINTER_SIZE));
            long userdata = memGetAddress(memGetAddress(args + 3L * POINTER_SIZE));

            long r = invoke(ptr, size, nmemb, userdata);
            memPutCLong(ret, r);
        } catch (Throwable ex) {
            CURLUtils.throwUnchecked(ex);
        }
    }

    /**
     * Called to fill the curl buffer with data.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">documentation</a>.
     *
     * @throws IOException If an error occurred whilst processing the bytes,
     *                     this will be propagated out the curl command used to start the transfer.
     */
    @NativeType ("size_t")
    long invoke(
            @NativeType ("void *") long ptr,
            @NativeType ("size_t") long size,
            @NativeType ("size_t") long nmemb,
            @NativeType ("void *") long userdata) throws IOException;
}
