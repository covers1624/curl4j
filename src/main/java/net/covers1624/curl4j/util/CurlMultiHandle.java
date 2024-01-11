package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CURL;
import net.covers1624.curl4j.util.internal.CurlHandleFactory;

import java.util.concurrent.atomic.AtomicLong;

import static net.covers1624.curl4j.CURL.*;

/**
 * A simple resource management wrapper around both
 * curl_easy and curl_multi handles.
 * <p>
 * This is indented to be used for curl_multi operations only.
 * <p>
 * This can either be used directly with {@link AutoCloseable}
 * or resource managed via auto-cleanup via {@link #createMulti()} ()}
 * or pooled per-thread via {@link #newMultiThreadLocal()}.
 *
 * @author covers1624
 */
public class CurlMultiHandle extends CurlHandle {

    public final long multi;
    private final AtomicLong a_multi;

    /**
     * Construct a new {@link CurlMultiHandle} without any
     * automatic cleanup.This handle must be cleaned with
     * {@link #close()} or via Try-With-Resources.
     */
    public CurlMultiHandle() {
        this(new AtomicLong(curl_easy_init()), new AtomicLong(curl_multi_init()));
    }

    /**
     * Intended for internal auto-cleaning implementations.
     *
     * @param a_curl  The atomic holding the curl handle.
     * @param a_multi The atomic holding the multi handle.
     */
    protected CurlMultiHandle(AtomicLong a_curl, AtomicLong a_multi) {
        super(a_curl);
        multi = a_multi.get();
        this.a_multi = a_multi;
    }

    /**
     * Create an auto-cleaning curl and curl_multi handles.
     * <p>
     * The native resources allocated by this handle will automatically
     * be cleaned up.
     * <p>
     * {@implNote On Java 8, this uses Object finalization. On Java 9+ It uses ref Cleaners.
     * These implementations are transparent and equivalent}
     *
     * @return The new handle.
     */
    public static CurlMultiHandle createMulti() {
        return CurlHandleFactory.INSTANCE.newMultiHandle(
                new AtomicLong(CURL.curl_easy_init()),
                new AtomicLong(CURL.curl_multi_init())
        );
    }

    /**
     * Create a new {@link ThreadLocal} of auto-cleaning curl and curl_multi handles.
     *
     * @return The thread local.
     * @see #createMulti()
     */
    public static ThreadLocal<CurlMultiHandle> newMultiThreadLocal() {
        return ThreadLocal.withInitial(CurlMultiHandle::createMulti);
    }

    @Override
    public void close() {
        super.close();
        if (a_multi.compareAndSet(multi, 0)) {
            curl_multi_cleanup(multi);
        }
    }
}
