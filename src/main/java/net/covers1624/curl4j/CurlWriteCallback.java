package net.covers1624.curl4j;

import org.lwjgl.system.Callback;

import java.io.IOException;

import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by covers1624 on 4/8/23.
 */
public abstract class CurlWriteCallback extends CURLCallback implements CurlWriteCallbackI {

    public static CurlWriteCallback create(long functionPointer) {
        CurlWriteCallbackI instance = Callback.get(functionPointer);
        return instance instanceof CurlWriteCallback ? (CurlWriteCallback) instance : new Container(functionPointer, instance);
    }

    public static CurlWriteCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    public static CurlWriteCallback create(CurlWriteCallbackI instance) {
        return instance instanceof CurlWriteCallback ? (CurlWriteCallback) instance : new Container(instance.address(), instance);
    }

    protected CurlWriteCallback() {
        super(CIF);
    }

    CurlWriteCallback(long address) {
        super(address);
    }

    private static final class Container extends CurlWriteCallback {

        private final CurlWriteCallbackI delegate;

        private Container(long functionPointer, CurlWriteCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public long invoke(long ptr, long size, long nmemb, long userdata) throws IOException {
            return delegate.invoke(ptr, size, nmemb, userdata);
        }
    }
}
