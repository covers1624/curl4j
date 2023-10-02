package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Callback;
import net.covers1624.curl4j.core.NativeType;

import java.io.IOException;

import static net.covers1624.curl4j.core.Memory.*;
import static net.covers1624.curl4j.core.NativeTypes.POINTER_SIZE;

/**
 * A functional interface callback for writing data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlWriteCallback
 */
@FunctionalInterface
public interface CurlWriteCallbackI extends Callback.CallbackInterface {

    @Override
    default void invoke(@NativeType ("void *") long ret, @NativeType ("void **") long args) throws IOException {
        long ptr = getAddress(getAddress(args));
        long size = getSizeT(getAddress(args + POINTER_SIZE));
        long nmemb = getSizeT(getAddress(args + 2L * POINTER_SIZE));
        long userdata = getAddress(getAddress(args + 3L * POINTER_SIZE));

        long r = write(ptr, size, nmemb, userdata);
        putSizeT(ret, r);
    }


    /**
     * Called to empty the curl buffer.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html">documentation</a>.
     *
     * @throws IOException If an error occurred whilst processing the bytes.
     *                     If the curl operation is running on a Java thread, this will bubble out. Otherwise, it will
     *                     be printed to stderr, and ignored.
     */
    long write(long ptr, long size, long nmemb, long userdata) throws IOException;
}
