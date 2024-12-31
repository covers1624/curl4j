package net.covers1624.curl4j;

import java.lang.foreign.MemorySegment;

/**
 * A functional interface callback for receiving progress stats.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_XFERINFOFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlXferInfoCallback
 */
@FunctionalInterface
public interface CurlXferInfoCallbackI {

    /**
     * Called to receive the transfer progress statistics.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_XFERINFOFUNCTION.html">documentation</a>.
     *
     * @param ptr     User pointer set by {@link CURL#CURLOPT_XFERINFODATA}.
     * @param dltotal The total expected to be downloaded.
     * @param dlnow   The amount downloaded.
     * @param ultotal The total expected to be uploaded.
     * @param ulnow   The amount uploaded.
     * @return {@link CURL#CURLE_OK} or {@link CURL#CURL_PROGRESSFUNC_CONTINUE},
     * negative values fail the transfer with {@link CURL#CURLE_ABORTED_BY_CALLBACK}.
     */
    int update(MemorySegment ptr, long dltotal, long dlnow, long ultotal, long ulnow);
}
