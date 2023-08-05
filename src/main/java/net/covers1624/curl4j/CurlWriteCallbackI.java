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
 * Functional interface to handle {@link CURL#CURLOPT_WRITEFUNCTION}.
 * See <a href="https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html">the curl documentation.</a>
 * <p>
 *
 * @author covers1624
 */
@FunctionalInterface
public interface CurlWriteCallbackI extends CallbackI {

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
     * This callback function gets called by libcurl as soon as there is data received that needs to be saved.
     * For most transfers this callback gets called many times as each invoke delivers another chunk of data.
     * {@code ptr} points to the delivered data and the size of that data is {@code nmemb}; {code size} is always {@code 1}.
     * <p>
     * The callback function will be passed as much data as possible in all invokes, but you must not make any assumptions.
     * It may be one byte, it may be thousands, The maximum amount of body data that will be passed to the write callback is defined
     * in the curl.h header file {@code CURL_MAX_WRITE_SIZE} (the usual default is 16k) (This amount is dependant on the underlying built
     * version of libcurl as it is a compile-time constant).
     * If {@link CURL#CURLOPT_HEADER} is enabled, which makes the header data get passed to the write callback, you
     * can get up to {@code CURL_MAX_HTTP_HEADER} bytes passed into it. This usually means 100k.
     * <p>
     * This function may be called with zero bytes of the transferred file is empty.
     * <p>
     * The data passed to this function will not be null-terminated!
     * <p>
     * Set the {@code userdata} argument with the {@link CURL#CURLOPT_WRITEDATA} option.
     * <p>
     * Your callback should return the number of bytes actually taken care of. If that amount differs from the amount passed
     * to your callback function, it will singnal an error condition to the library. This will cause the transfer to get aborted and
     * the libcurl function used will return {@link CURL#CURLE_WRITE_ERROR}.
     * <p>
     * If your callback function returns {@link CURL#CURL_WRITEFUNC_PAUSE} it will cause this transfer to become paused.
     * See {@link CURL#curl_easy_pause} for further details.
     *
     * @param ptr      The pointer to the data buffer.
     * @param size     The width of the data. (Always 1).
     * @param nmemb    The number of {@code size}'s in the buffer.
     * @param userdata Pointer to user defined data set via {@link CURL#CURLOPT_WRITEDATA}.
     * @return The number of bytes handled. May be {@link CURL#CURL_WRITEFUNC_PAUSE} to pause the transfer.
     * Otherwise, any value other than {@code size * nmemb} will result in {@link CURL#CURLE_WRITE_ERROR}
     * being returned by the curl command used to start the transfer.
     * @throws IOException If an error occurred whilst processing the bytes,
     *                     this will be propagated out the curl command used to start the transfer.
     */
    @NativeType ("size_t")
    long invoke(
            @NativeType ("void *") long ptr,
            @NativeType ("size_t") long size,
            @NativeType ("size_t") long nmemb,
            @NativeType ("void *") long userdata) throws IOException;

//    static ICurlWriteCallback simple(Consumer<byte[]> cons) {
//        return (ptr, size, nmemb, userdata) -> {
//            int realSize = (int) (size * nmemb);
//
//            ByteBuffer buffer = MemoryUtil.memByteBuffer(ptr, realSize);
//            byte[] bytes = new byte[realSize];
//
//            buffer.get(bytes);
//            cons.accept(bytes);
//
//            return realSize;
//        };
//    }
}
