package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CURL;

/**
 * A simple curl wrapper which will automatically
 * clean up the curl handle.
 * <p>
 * Created by covers1624 on 20/10/23.
 */
public final class CurlHandle implements AutoCloseable {

    public final long curl;

    private boolean cleaned;

    public CurlHandle(long curl) {
        this.curl = curl;
    }

    public static ThreadLocal<CurlHandle> newThreadLocal() {
        return ThreadLocal.withInitial(() -> new CurlHandle(CURL.curl_easy_init()));
    }

    // TODO, We can do better than this. We need a variant of CurlHandle which does not use finalize
    //       but instead uses Java9+ Cleaners.
    @Override
    protected void finalize() {
        close();
    }

    @Override
    public void close() {
        if (cleaned) return;
        cleaned = true;

        CURL.curl_easy_cleanup(curl);
    }
}
