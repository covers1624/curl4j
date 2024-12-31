package net.covers1624.curl4j;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * A function callback for receiving progress stats.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_XFERINFOFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlXferInfoCallbackI
 */
public class CurlXferInfoCallback extends CurlCallback implements CurlXferInfoCallbackI {

    private static final CallbackDescriptor DESC = CallbackDescriptor.create(
            "int (*curl_xferinfo_callback)(void *clientp, curl_off_t dltotal, curl_off_t dlnow, curl_off_t ultotal, curl_off_t ulnow);",
            () -> MethodHandles.lookup().findVirtual(CurlXferInfoCallback.class, "onInvoke", MethodType.methodType(int.class, MemorySegment.class, long.class, long.class, long.class, long.class))
    );

    private final CurlXferInfoCallbackI delegate;

    public CurlXferInfoCallback(CurlXferInfoCallbackI delegate) {
        super(DESC);
        this.delegate = delegate;
    }

    protected CurlXferInfoCallback() {
        super(DESC);
        delegate = this;
    }

    private int onInvoke(MemorySegment userdata, long dltotal, long dlnow, long ultotal, long ulnow) {
        try {
            return delegate.update(userdata, dltotal, dlnow, ultotal, ulnow);
        } catch (Throwable ex) {
            handleCallbackException(ex);
            return 1; // Causes CURLE_ABORTED_BY_CALLBACK.
        }
    }

    @Override
    public int update(MemorySegment ptr, long dltotal, long dlnow, long ultotal, long ulnow) {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }
}
