package net.covers1624.curl4j;

import net.covers1624.curl4j.util.CLikeStructParser;
import net.covers1624.curl4j.util.CLikeSymbolLinker;
import net.covers1624.curl4j.util.CLikeSymbolResolver;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongConsumer;

import static net.covers1624.curl4j.util.ForeignUtils.readNTString;
import static net.covers1624.curl4j.util.ForeignUtils.rethrowUnchecked;

/**
 * @author covers1624
 */
public class LibCurl {

    public static final CLikeSymbolResolver SYMBOL_RESOLVER = new CLikeSymbolResolver()
            .addAlias("CURLversion", "int")
            .addAlias("CURLcode", "int")
            .addAlias("CURLoption", "int")
            .addAlias("CURLINFO", "int");

    public static final CLikeStructParser STRUCT_PARSER = new CLikeStructParser(SYMBOL_RESOLVER);

    public final SymbolLookup lookup;

    public final MethodHandle curl_version;
    public final MethodHandle curl_version_info;
    public final MethodHandle curl_global_init;
    public final MethodHandle curl_global_cleanup;

    public final MethodHandle curl_easy_init;
    public final MethodHandle curl_easy_setopt_int;
    public final MethodHandle curl_easy_setopt_long;
    public final MethodHandle curl_easy_setopt_ptr;
    public final MethodHandle curl_easy_perform;
    public final MethodHandle curl_easy_cleanup;
    public final MethodHandle curl_easy_getinfo;
    public final MethodHandle curl_easy_duphandle;
    public final MethodHandle curl_easy_pause;
    public final MethodHandle curl_easy_reset;

    public final MethodHandle curl_easy_recv;
    public final MethodHandle curl_easy_send;

    public final MethodHandle curl_easy_upkeep;

    public final MethodHandle curl_easy_strerror;

    public final @Nullable MethodHandle curl_easy_impersonate;

    public LibCurl(SymbolLookup lookup) {
        this.lookup = lookup;
        var linker = new CLikeSymbolLinker(lookup, SYMBOL_RESOLVER);

        curl_version = linker.link("char *curl_version(void);");
        curl_version_info = linker.link("curl_version_info_data *curl_version_info(CURLversion);");
        curl_global_init = linker.link("CURLcode curl_global_init(long flags);");
        curl_global_cleanup = linker.link("void curl_global_cleanup(void);");

        curl_easy_init = linker.link("CURL *curl_easy_init(void);");
        curl_easy_setopt_int = linker.link("CURLcode curl_easy_setopt(CURL *curl, CURLoption option, ...);", "int");
        curl_easy_setopt_long = linker.link("CURLcode curl_easy_setopt(CURL *curl, CURLoption option, ...);", "long");
        curl_easy_setopt_ptr = linker.link("CURLcode curl_easy_setopt(CURL *curl, CURLoption option, ...);", "void*");
        curl_easy_perform = linker.link("CURLcode curl_easy_perform(CURL *curl);");
        curl_easy_cleanup = linker.link("void curl_easy_cleanup(CURL *curl);");
        curl_easy_getinfo = linker.link("CURLcode curl_easy_getinfo(CURL *curl, CURLINFO info, ...);", "void*");
        curl_easy_duphandle = linker.link("CURL *curl_easy_duphandle(CURL *curl);");
        curl_easy_pause = linker.link("CURLcode curl_easy_pause(CURL *handle, int bitmask);");
        curl_easy_reset = linker.link("void curl_easy_reset(CURL *curl);");

        curl_easy_recv = linker.link("CURLcode curl_easy_recv(CURL *curl, void *buffer, size_t buflen, size_t *n);");
        curl_easy_send = linker.link("CURLcode curl_easy_send(CURL *curl, const void *buffer, size_t buflen, size_t *n);");

        curl_easy_upkeep = linker.link("CURLcode curl_easy_upkeep(CURL *curl);");

        curl_easy_strerror = linker.link("const char *curl_easy_strerror(CURLcode);");

        curl_easy_impersonate = linker.linkOptionally("CURLcode curl_easy_impersonate(CURL *curl, const char *target, int default_headers);");
    }

    public final String curl_version() {
        try {
            return readNTString((MemorySegment) curl_version.invokeExact());
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final @Nullable curl_version_info_data curl_version_info(int curlVersion) {
        try {
            return safeGetStruct((MemorySegment) curl_version_info.invokeExact(curlVersion), curl_version_info_data::new);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_global_init(long flags) {
        try {
            return (int) curl_global_init.invokeExact(flags);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final void curl_global_cleanup() {
        try {
            curl_global_cleanup.invokeExact();
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final MemorySegment curl_easy_init() {
        try {
            return (MemorySegment) curl_easy_init.invokeExact();
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_easy_setopt(MemorySegment curl, int option, int i) {
        try {
            return (int) curl_easy_setopt_int.invokeExact(curl, option, i);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_easy_setopt(MemorySegment curl, int option, boolean b) {
        return curl_easy_setopt(curl, option, b ? 1 : 0);
    }

    public final int curl_easy_setopt(MemorySegment curl, int option, long l) {
        try {
            return (int) curl_easy_setopt_long.invokeExact(curl, option, l);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_easy_setopt(MemorySegment curl, int option, MemorySegment p) {
        try {
            return (int) curl_easy_setopt_ptr.invokeExact(curl, option, p);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_easy_setopt(MemorySegment curl, int option, String str) {
        try (Arena arena = Arena.ofShared()) {
            return curl_easy_setopt(curl, option, arena.allocateFrom(str));
        }
    }

    public final int curl_easy_perform(MemorySegment curl) {
        try {
            return (int) curl_easy_perform.invokeExact(curl);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final void curl_easy_cleanup(MemorySegment curl) {
        try {
            curl_easy_cleanup.invokeExact(curl);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_easy_getinfo(MemorySegment curl, int info, MemorySegment result) {
        try {
            return (int) curl_easy_getinfo.invokeExact(curl, info, result);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final InfoResult<String> curl_easy_getinfo_String(MemorySegment curl, int info) {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment result = arena.allocate(ValueLayout.ADDRESS);
            int ret = curl_easy_getinfo(curl, info, result);
            if (ret != CURL.CURLE_OK) {
                return new InfoResult<>(ret, null);
            }

            return new InfoResult<>(ret, readNTString(result.get(ValueLayout.ADDRESS, 0)));
        }
    }

    public final int curl_easy_getinfo_String(MemorySegment curl, int info, Consumer<String> cons) {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment result = arena.allocate(ValueLayout.ADDRESS);
            int ret = curl_easy_getinfo(curl, info, result);
            if (ret == CURL.CURLE_OK) {
                cons.accept(readNTString(result.get(ValueLayout.ADDRESS, 0)));
            }
            return ret;
        }
    }

    public final int curl_easy_getinfo_String(MemorySegment curl, int info, String[] result) {
        if (result.length != 1) throw new RuntimeException("Expected array length of 1 got " + result.length);
        try (Arena arena = Arena.ofShared()) {
            MemorySegment resultAddr = arena.allocate(ValueLayout.ADDRESS);
            int ret = curl_easy_getinfo(curl, info, resultAddr);
            if (ret == CURL.CURLE_OK) {
                result[0] = readNTString(resultAddr.get(ValueLayout.ADDRESS, 0));
            }
            return ret;
        }
    }

    public final InfoResult<Long> curl_easy_getinfo_long(MemorySegment curl, int info) {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment result = arena.allocate(ValueLayout.JAVA_LONG);
            int ret = curl_easy_getinfo(curl, info, result);
            if (ret != CURL.CURLE_OK) {
                return new InfoResult<>(ret, null);
            }

            return new InfoResult<>(ret, result.get(ValueLayout.JAVA_LONG, 0));
        }
    }

    public final int curl_easy_getinfo_long(MemorySegment curl, int info, LongConsumer cons) {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment result = arena.allocate(ValueLayout.JAVA_LONG);
            int ret = curl_easy_getinfo(curl, info, result);
            if (ret == CURL.CURLE_OK) {
                cons.accept(result.get(ValueLayout.JAVA_LONG, 0));
            }
            return ret;
        }
    }

    public final int curl_easy_getinfo_long(MemorySegment curl, int info, long[] result) {
        if (result.length != 1) throw new RuntimeException("Expected array length of 1 got " + result.length);
        try (Arena arena = Arena.ofShared()) {
            MemorySegment resultAddr = arena.allocate(ValueLayout.JAVA_LONG);
            int ret = curl_easy_getinfo(curl, info, resultAddr);
            if (ret == CURL.CURLE_OK) {
                result[0] = resultAddr.get(ValueLayout.JAVA_LONG, 0);
            }
            return ret;
        }
    }

    public final MemorySegment curl_easy_duphandle(MemorySegment curl) {
        try {
            return (MemorySegment) curl_easy_duphandle.invokeExact(curl);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_easy_pause(MemorySegment curl, int bitmask) {
        try {
            return (int) curl_easy_pause.invokeExact(curl, bitmask);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final void curl_easy_reset(MemorySegment curl) {
        try {
            curl_easy_reset.invokeExact(curl);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_easy_recv(MemorySegment curl, MemorySegment buffer, long bufLen, MemorySegment n) {
        try {
            return (int) curl_easy_recv.invokeExact(curl, buffer, bufLen, n);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_easy_send(MemorySegment curl, MemorySegment buffer, long bufLen, MemorySegment n) {
        try {
            return (int) curl_easy_send.invokeExact(curl, buffer, bufLen, n);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_easy_upkeep(MemorySegment curl) {
        try {
            return (int) curl_easy_upkeep.invokeExact(curl);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final String curl_easy_strerror(int n) {
        try {
            return readNTString((MemorySegment) curl_easy_strerror.invokeExact(n));
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public boolean isCurlImpersonateSupported() {
        return curl_easy_impersonate != null;
    }

    public final int curl_easy_impersonate(MemorySegment curl, String target, int defaultHeaders) {
        if (curl_easy_impersonate == null) throw new NullPointerException("curl_easy_impersonate is not supported with this libcurl.");

        try {
            return (int) curl_easy_impersonate.invokeExact(curl, target, defaultHeaders);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    private static <T> @Nullable T safeGetStruct(MemorySegment addr, Function<MemorySegment, T> func) {
        if (addr.equals(MemorySegment.NULL)) return null;

        return func.apply(addr);
    }
}
