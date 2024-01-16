package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CURL;
import net.covers1624.curl4j.ErrorBuffer;
import net.covers1624.curl4j.util.internal.CurlHandleFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A simple resource management wrapper around a
 * curl_easy handle.
 * <p>
 * Each handle has a {@link ErrorBuffer} already attached for convenience.
 * <p>
 * This is indented to be used for curl_easy operations only.
 * <p>
 * This can either be used directly with {@link AutoCloseable}
 * or resource managed via auto-cleanup via {@link #create()}
 * or pooled per-thread via {@link #newThreadLocal()}.
 *
 * @author covers1624
 */
public class CurlHandle implements AutoCloseable {

    public final long curl;
    public final ErrorBuffer errorBuffer = new ErrorBuffer();
    private final AtomicLong a_curl;

    /**
     * Construct a new {@link CurlHandle} without any
     * automatic cleanup. This handle must be cleaned with
     * {@link #close()} or via Try-With-Resources.
     */
    public CurlHandle() {
        this(new AtomicLong(CURL.curl_easy_init()));
    }

    /**
     * Intended for internal auto-cleaning implementations.
     *
     * @param a_curl The atomic holding the curl handle.
     */
    protected CurlHandle(AtomicLong a_curl) {
        curl = a_curl.get();
        this.a_curl = a_curl;
        errorBuffer.apply(curl);
    }

    /**
     * Create an auto-cleaning curl handle.
     * <p>
     * The native resources allocated by this handle will automatically
     * be cleaned up.
     * <p>
     * {@implNote On Java 8, this uses Object finalization. On Java 9+ It uses ref Cleaners.
     * These implementations are transparent and equivalent}
     *
     * @return The new handle.
     */
    public static CurlHandle create() {
        return CurlHandleFactory.INSTANCE.newHandle(new AtomicLong(CURL.curl_easy_init()));
    }

    /**
     * Create a new {@link ThreadLocal} of auto-cleaning curl handles.
     *
     * @return The thread local.
     * @see #create()
     */
    public static ThreadLocal<CurlHandle> newThreadLocal() {
        return ThreadLocal.withInitial(CurlHandle::create);
    }

    @Override
    public void close() {
        if (a_curl.compareAndSet(curl, 0)) {
            CURL.curl_easy_cleanup(curl);
        }
    }
}
