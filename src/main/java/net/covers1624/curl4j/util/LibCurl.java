package net.covers1624.curl4j.util;

import net.covers1624.curl4j.curl_version_info_data;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;

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
    public final MethodHandle curl_easy_reset;

    public final MethodHandle curl_easy_recv;
    public final MethodHandle curl_easy_send;

    public final MethodHandle curl_easy_upkeep;

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
        curl_easy_reset = linker.link("void curl_easy_reset(CURL *curl);");

        curl_easy_recv = linker.link("CURLcode curl_easy_recv(CURL *curl, void *buffer, size_t buflen, size_t *n);");
        curl_easy_send = linker.link("CURLcode curl_easy_send(CURL *curl, const void *buffer, size_t buflen, size_t *n);");

        curl_easy_upkeep = linker.link("CURLcode curl_easy_upkeep(CURL *curl);");
    }

    public final String curl_version() {
        try {
            return readNTString((MemorySegment) curl_version.invokeExact());
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final curl_version_info_data curl_version_info(int curlVersion) {
        try {
            return new curl_version_info_data((MemorySegment) curl_version_info.invokeExact(curlVersion));
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }
}
