package net.covers1624.curl4j;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;

/**
 * Functional interface to handle {@link CURL#CURLOPT_HEADERFUNCTION}.
 * See <a href="https://curl.se/libcurl/c/CURLOPT_HEADERFUNCTION.html">the curl documentation.</a>
 * <p>
 *
 * @author covers1624
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
     * This function gets called by libcurl as soon as it has received header data. The header callback
     * will be called once for each header and only complete header lines are passed on to the callback. Parsing headers is
     * easy to do using this callback. buffer points to the delivered data, and the size of that data is nitems; size is
     * always 1. Do not assume that the header line is null-terminated!
     * <p>
     * The pointer named userdata is the one you set with the {@link CURL#CURLOPT_HEADERDATA} option.
     * <p>
     * Your callback should return the number of bytes actually taken care of. If that amount differs from the
     * amount passed to your callback function, it will signal an error condition to the library. This will cause the
     * transfer to get aborted and the libcurl function used will return {@link CURL#CURLE_WRITE_ERROR}.
     * <p>
     * You can also abort the transfer by returning {@link CURL#CURL_WRITEFUNC_ERROR}. (7.87.0)
     * <p>
     * A complete HTTP header that is passed to this function can be up to {@link CURL#CURL_MAX_HTTP_HEADER} (100K)
     * bytes and includes the final line terminator.
     * <p>
     * If this option is not set, or if it is set to NULL, but {@link CURL#CURLOPT_HEADERDATA} is set to anything but NULL, the
     * function used to accept response data will be used instead. That is, it will be the function specified with
     * {@link CURL#CURLOPT_WRITEFUNCTION}, or if it is not specified or NULL - the default, stream-writing function.
     * <p>
     * It's important to note that the callback will be invoked for the headers of all responses received after initiating
     * a request and not just the final response. This includes all responses which occur during authentication negotiation.
     * If you need to operate on only the headers from the final response, you will need to collect headers in the callback
     * yourself and use HTTP status lines, for example, to delimit response boundaries.
     * <p>
     * For an HTTP transfer, the status line and the blank line preceding the response body are both included as
     * headers and passed to this function.
     * <p>
     * When a server sends a chunked encoded transfer, it may contain a trailer. That trailer is identical to an HTTP
     * header and if such a trailer is received it is passed to the application using this callback as well. There are
     * several ways to detect it being a trailer and not an ordinary header: 1) it comes after the response-body. 2) it
     * comes after the final header line (CR LF) 3) a Trailer: header among the regular response-headers mention
     * what header(s) to expect in the trailer.
     * <p>
     * For non-HTTP protocols like FTP, POP3, IMAP and SMTP this function will get called with the server
     * responses to the commands that libcurl sends.
     */
    void invoke(String header, @NativeType ("void *") long userdata);
}
