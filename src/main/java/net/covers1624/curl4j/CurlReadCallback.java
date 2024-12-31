package net.covers1624.curl4j;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * A function callback for reading POST/PUT data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlReadCallbackI
 */
public class CurlReadCallback extends CurlCallback implements CurlReadCallbackI {

    private static final CallbackDescriptor DESC = CallbackDescriptor.create(
            "size_t (*curl_read_callback)(char *buffer, size_t size,  size_t nitems, void *userdata);",
            () -> MethodHandles.lookup().findVirtual(CurlReadCallback.class, "onInvoke", MethodType.methodType(long.class, MemorySegment.class, long.class, long.class, MemorySegment.class))
    );

    private final CurlReadCallbackI delegate;

    public CurlReadCallback(CurlReadCallbackI delegate) {
        super(DESC);
        this.delegate = delegate;
    }

    protected CurlReadCallback() {
        super(DESC);
        delegate = this;
    }

    private long onInvoke(MemorySegment ptr, long size, long nmemb, MemorySegment userdata) {
        try {
            return delegate.read(ptr, size, nmemb, userdata);
        } catch (Throwable ex) {
            handleCallbackException(ex);
            return CURL.CURL_READFUNC_ABORT;
        }
    }

    @Override
    public long read(MemorySegment ptr, long size, long nmemb, MemorySegment userdata) throws IOException {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }
}
