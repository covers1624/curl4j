/*
 * This file is part of Quack and is Licensed under the MIT License.
 */
package net.covers1624.curl4j.httpapi;

import net.covers1624.curl4j.CABundle;
import net.covers1624.curl4j.CURL;
import net.covers1624.curl4j.util.CurlHandle;
import net.covers1624.curl4j.util.CurlMultiHandle;
import net.covers1624.quack.annotation.Requires;
import net.covers1624.quack.net.httpapi.HttpEngine;
import org.jetbrains.annotations.Nullable;

/**
 * Created by covers1624 on 1/11/23.
 */
@Requires (value = "net.covers1624:Quack", minVersion = "0.4.111")
public class Curl4jHttpEngine implements HttpEngine {

    private static boolean CURL_GLOBAL_INIT = false;

    private final HandlePool<CurlHandle> CURL_HANDLES = new HandlePool<>(CurlHandle::create);
    private final HandlePool<CurlMultiHandle> MULTI_HANDLES = new HandlePool<>(CurlMultiHandle::createMulti);

    private @Nullable CABundle caBundle;
    public final @Nullable String impersonate;

    public Curl4jHttpEngine() {
        this((CABundle) null);
    }

    public Curl4jHttpEngine(@Nullable CABundle caBundle) {
        this(caBundle, null);
    }

    public Curl4jHttpEngine(@Nullable String impersonate) {
        this(null, impersonate);
    }

    public Curl4jHttpEngine(@Nullable CABundle caBundle, @Nullable String impersonate) {
        this.caBundle = caBundle;
        this.impersonate = impersonate;
        if (!CURL.isCurlImpersonateSupported() && impersonate != null) {
            throw new IllegalArgumentException("Current CURL instance does not support impersonation.");
        }

        synchronized (Curl4jHttpEngine.class) {
            if (!CURL_GLOBAL_INIT) {
                CURL.curl_global_init(CURL.CURL_GLOBAL_DEFAULT);
                CURL_GLOBAL_INIT = true;
            }
        }
    }

    @Override
    public Curl4jEngineRequest newRequest() {
        return new Curl4jEngineRequest(this).useCABundle(caBundle);
    }

    @Nullable String getImpersonate() {
        return impersonate;
    }

    HandlePool<CurlHandle>.Entry getHandle() {
        return CURL_HANDLES.get();
    }

    HandlePool<CurlMultiHandle>.Entry getMultiHandle() {
        return MULTI_HANDLES.get();
    }
}
