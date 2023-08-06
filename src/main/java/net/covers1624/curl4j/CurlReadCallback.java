package net.covers1624.curl4j;

import org.lwjgl.system.Callback;

import java.io.IOException;

import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * A function callback for reading POST/PUT data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlReadCallbackI
 */
public abstract class CurlReadCallback extends CURLCallback implements CurlReadCallbackI {

    public static CurlReadCallback create(long functionPointer) {
        CurlReadCallbackI instance = Callback.get(functionPointer);
        return instance instanceof CurlReadCallback ? (CurlReadCallback) instance : new Container(functionPointer, instance);
    }

    public static CurlReadCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    public static CurlReadCallback create(CurlReadCallbackI instance) {
        return instance instanceof CurlReadCallback ? (CurlReadCallback) instance : new Container(instance.address(), instance);
    }

    protected CurlReadCallback() {
        super(CIF);
    }

    CurlReadCallback(long address) {
        super(address);
    }

    private static final class Container extends CurlReadCallback {

        private final CurlReadCallbackI delegate;

        private Container(long functionPointer, CurlReadCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public long invoke(long ptr, long size, long nmemb, long userdata) throws IOException {
            return delegate.invoke(ptr, size, nmemb, userdata);
        }
    }
}
