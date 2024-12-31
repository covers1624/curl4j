package net.covers1624.curl4j;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * A function callback for seeking the curl input.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_SEEKFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlSeekCallbackI
 */
public class CurlSeekCallback extends CurlCallback implements CurlSeekCallbackI {

    private static final CallbackDescriptor DESC = CallbackDescriptor.create(
            "int (*curl_seek_callback)(void *userdata, curl_off_t offset, int origin);",
            () -> MethodHandles.lookup().findVirtual(CurlSeekCallback.class, "onInvoke", MethodType.methodType(int.class, MemorySegment.class, long.class, int.class))
    );

    private final CurlSeekCallbackI delegate;

    public CurlSeekCallback(CurlSeekCallbackI delegate) {
        super(DESC);
        this.delegate = delegate;
    }

    protected CurlSeekCallback() {
        super(DESC);
        delegate = null;
    }

    private int onInvoke(MemorySegment userdata, long offset, int origin) {
        try {
            return delegate.seek(userdata, offset, origin);
        } catch (Throwable ex) {
            handleCallbackException(ex);
            return CURL.CURL_SEEKFUNC_FAIL;
        }
    }

    @Override
    public int seek(MemorySegment userdata, long offset, int origin) throws IOException {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }
}
