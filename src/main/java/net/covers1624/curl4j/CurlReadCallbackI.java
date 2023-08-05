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
 * Functional interface to handle {@link CURL#CURLOPT_READFUNCTION}.
 * See <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">the curl documentation.</a>
 * <p>
 *
 * @author covers1624
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
     * This callback function gets called by libcurl as soon as it needs to read data in order to send it to the peer -
     * like if you ask it to upload or post data to the server. The data area pointed at by the pointer buffer should be
     * filled up with at most size multiplied with nitems number of bytes by your function. size is always 1.
     * <p>
     * Set the userdata argument with the {@link CURL#CURLOPT_READDATA} option.
     * <p>
     * Your function must return the actual number of bytes that it stored in the data area pointed at by the pointer
     * buffer. Returning 0 will signal end-of-file to the library and cause it to stop the current transfer.
     * <p>
     * If you stop the current transfer by returning 0 "pre-maturely" (i.e before the server expected it, like when you
     * have said you will upload N bytes and you upload less than N bytes), you may experience that the server
     * "hangs" waiting for the rest of the data that will not come.
     * <p>
     * The read callback may return {@link CURL#CURL_READFUNC_ABORT} to stop the current operation immediately,
     * resulting in a {@link CURL#CURLE_ABORTED_BY_CALLBACK} error code from the transfer.
     * <p>
     * The callback can return {@link CURL#CURL_READFUNC_PAUSE} to cause reading from this connection to pause. See
     * curl_easy_pause for further details.
     * <p>
     * Bugs: when doing TFTP uploads, you must return the exact amount of data that the callback wants, or it will
     * be considered the final packet by the server end and the transfer will end there.
     * <p>
     * If you set this callback pointer to NULL, or do not set it at all, the default internal read function will be used. It
     * is doing an fread() on the FILE * userdata set with {@link CURL#CURLOPT_READDATA}.
     * <p>
     * You can set the total size of the data you are sending by using {@link CURL#CURLOPT_INFILESIZE_LARGE} or
     * {@link CURL#CURLOPT_POSTFIELDSIZE_LARGE}, depending on the type of transfer. For some transfer types it may be
     * required and it allows for better error checking.
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
