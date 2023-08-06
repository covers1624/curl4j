package net.covers1624.curl4j;

import org.lwjgl.system.Callback;

import java.io.IOException;

import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * A function callback for receiving progress stats.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_XFERINFOFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlXferInfoCallbackI
 */
public abstract class CurlXferInfoCallback extends CURLCallback implements CurlXferInfoCallbackI {

    public static CurlXferInfoCallback create(long functionPointer) {
        CurlXferInfoCallbackI instance = Callback.get(functionPointer);
        return instance instanceof CurlXferInfoCallback ? (CurlXferInfoCallback) instance : new Container(functionPointer, instance);
    }

    public static CurlXferInfoCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    public static CurlXferInfoCallback create(CurlXferInfoCallbackI instance) {
        return instance instanceof CurlXferInfoCallback ? (CurlXferInfoCallback) instance : new Container(instance.address(), instance);
    }

    protected CurlXferInfoCallback() {
        super(CIF);
    }

    CurlXferInfoCallback(long address) {
        super(address);
    }

    private static final class Container extends CurlXferInfoCallback {

        private final CurlXferInfoCallbackI delegate;

        private Container(long functionPointer, CurlXferInfoCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long ptr, long dltotal, long dlnow, long ultotal, long ulnow) {
            return delegate.invoke(ptr, dltotal, dlnow, ultotal, ulnow);
        }
    }
}
