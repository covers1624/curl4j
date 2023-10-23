package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CURL;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.LongFunction;

/**
 * A simple curl wrapper which will automatically
 * clean up the curl handle.
 * <p>
 * Created by covers1624 on 20/10/23.
 */
public class CurlHandle implements AutoCloseable {

    private static final LongFunction<CurlHandle> FACTORY = chooseFactory();

    public final long curl;
    private final AtomicBoolean cleaned;

    CurlHandle(long curl, AtomicBoolean cleaned) {
        this.curl = curl;
        this.cleaned = cleaned;
    }

    public static CurlHandle create() {
        return FACTORY.apply(CURL.curl_easy_init());
    }

    public static ThreadLocal<CurlHandle> newThreadLocal() {
        return ThreadLocal.withInitial(CurlHandle::create);
    }

    @Override
    public void close() {
        if (cleaned.get()) return;
        cleaned.set(true);

        CURL.curl_easy_cleanup(curl);
    }

    @SuppressWarnings ("unchecked")
    private static LongFunction<CurlHandle> chooseFactory() {
        try {
            Class.forName("java.lang.ref.Cleaner");
            Class<?> clazz = Class.forName("net.covers1624.curl4j.util.CleaningCurlHandle$Factory");
            return (LongFunction<CurlHandle>) clazz.getConstructor().newInstance();
        } catch (Throwable ex) {
            return FinalizingCurlHandle::new;
        }
    }

    public static class FinalizingCurlHandle extends CurlHandle {

        public FinalizingCurlHandle(long curl) {
            super(curl, new AtomicBoolean());
        }

        @Override
        protected void finalize() {
            close();
        }
    }
}
