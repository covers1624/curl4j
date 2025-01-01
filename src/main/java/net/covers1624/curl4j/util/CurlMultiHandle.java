package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CURL;
import net.covers1624.curl4j.ErrorBuffer;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * A simple resource management wrapper around both
 * curl_easy and curl_multi handles.
 * <p>
 * Each handle has a {@link ErrorBuffer} already attached for convenience.
 * <p>
 * This is indented to be used for curl_multi operations only.
 * <p>
 * This can either be used directly with {@link AutoCloseable}
 * or resource managed via auto-cleanup via {@link #createMulti()} ()}
 * or pooled per-thread via {@link #newMultiThreadLocal()}.
 *
 * @author covers1624
 */
public final class CurlMultiHandle extends CurlHandle {

    public final MemorySegment multi;

    private CurlMultiHandle(MemorySegment curl, MemorySegment multi, @Nullable Arena arena) {
        super(curl, arena);
        if (arena != null) {
            multi = multi.reinterpret(arena, CURL::curl_multi_cleanup);
        }
        this.multi = multi;
    }

    public static CurlMultiHandle createMulti() {
        return of(CURL.curl_easy_init(), CURL.curl_multi_init());
    }

    public static CurlMultiHandle of(MemorySegment curl, MemorySegment multi) {
        return of(curl, multi, Arena.ofAuto());
    }

    public static CurlMultiHandle of(MemorySegment curl, MemorySegment multi, Arena arena) {
        return new CurlMultiHandle(curl, multi, arena);
    }

    public static CurlHandle ofNonCleaning(MemorySegment curl, MemorySegment multi) {
        return new CurlMultiHandle(curl, multi, null);
    }
}
