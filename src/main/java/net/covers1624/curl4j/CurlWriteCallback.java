package net.covers1624.curl4j;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * A function callback for writing data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlWriteCallbackI
 */
public class CurlWriteCallback extends CurlCallback implements CurlWriteCallbackI {

    private static final CallbackDescriptor DESC = CallbackDescriptor.create(
            "size_t (*curl_write_callback)(char *buffer, size_t size, size_t nitems, void *userdata);",
            () -> MethodHandles.lookup().findVirtual(CurlWriteCallback.class, "onInvoke", MethodType.methodType(long.class, MemorySegment.class, long.class, long.class, MemorySegment.class))
    );

    private final CurlWriteCallbackI delegate;

    public CurlWriteCallback(CurlWriteCallbackI delegate) {
        super(DESC);
        this.delegate = delegate;
    }

    protected CurlWriteCallback() {
        super(DESC);
        delegate = null;
    }

    private long onInvoke(MemorySegment ptr, long size, long nmemb, MemorySegment userdata) {
        try {
            return delegate.write(ptr, size, nmemb, userdata);
        } catch (Throwable ex) {
            handleCallbackException(ex);
            return CURL.CURL_WRITEFUNC_ERROR;
        }
    }

    @Override
    public long write(MemorySegment ptr, long size, long nmemb, MemorySegment userdata) throws IOException {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }
}
