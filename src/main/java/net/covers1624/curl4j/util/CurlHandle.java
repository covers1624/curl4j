package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CURL;
import net.covers1624.curl4j.ErrorBuffer;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * A simple resource management wrapper around a curl_easy handle.
 * <p>
 * Each handle has a {@link ErrorBuffer} already attached for convenience.
 * <p>
 * This is indented to be used for curl_easy operations only.
 *
 * @author covers1624
 */
public sealed class CurlHandle permits CurlMultiHandle {

    public final ErrorBuffer errorBuffer = new ErrorBuffer();
    public final MemorySegment curl;
    // Keep arena in scope for GC cleanup.
    @SuppressWarnings ("FieldCanBeLocal")
    private final @Nullable Arena arena;

    protected CurlHandle(MemorySegment curl, @Nullable Arena arena) {
        if (arena != null) {
            // TODO use libcurl instance here?
            curl = curl.reinterpret(arena, CURL::curl_easy_cleanup);
        }
        this.curl = curl;
        this.arena = arena;
    }

    public static CurlHandle create() {
        return of(CURL.curl_easy_init());
    }

    public static CurlHandle of(MemorySegment curl) {
        return of(curl, Arena.ofAuto());
    }

    public static CurlHandle of(MemorySegment curl, Arena arena) {
        return new CurlHandle(curl, arena);
    }

    public static CurlHandle ofNonCleaning(MemorySegment curl) {
        return new CurlHandle(curl, null);
    }
}
