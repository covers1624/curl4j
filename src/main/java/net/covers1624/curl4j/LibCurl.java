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
            .addAlias("CURLINFO", "int")
            .addAlias("CURLMcode", "int")
            .addAlias("CURLMoption", "int")
            .addAlias("curl_off_t", "jlong");

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

    public final MethodHandle curl_slist_append;
    public final MethodHandle curl_slist_free_all;

    public final MethodHandle curl_mime_init;
    public final MethodHandle curl_mime_free;
    public final MethodHandle curl_mime_addpart;
    public final MethodHandle curl_mime_name;
    public final MethodHandle curl_mime_filename;
    public final MethodHandle curl_mime_type;
    public final MethodHandle curl_mime_encoder;
    public final MethodHandle curl_mime_data;
    public final MethodHandle curl_mime_filedata;
    public final MethodHandle curl_mime_data_cb;
    public final MethodHandle curl_mime_subparts;
    public final MethodHandle curl_mime_headers;

    public final MethodHandle curl_multi_init;
    public final MethodHandle curl_multi_add_handle;
    public final MethodHandle curl_multi_remove_handle;
    public final MethodHandle curl_multi_perform;
    public final MethodHandle curl_multi_cleanup;
    public final MethodHandle curl_multi_info_read;
    public final MethodHandle curl_multi_strerror;

    public final MethodHandle curl_multi_timeout;

    public final MethodHandle curl_multi_setopt_int;
    public final MethodHandle curl_multi_setopt_long;
    public final MethodHandle curl_multi_setopt_ptr;

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

        curl_slist_append = linker.link("struct curl_slist *curl_slist_append(struct curl_slist *list, const char *data);");
        curl_slist_free_all = linker.link("void curl_slist_free_all(struct curl_slist *list);");

        curl_mime_init = linker.link("curl_mime *curl_mime_init(CURL *easy);");
        curl_mime_free = linker.link("void curl_mime_free(curl_mime *mime);");
        curl_mime_addpart = linker.link("curl_mimepart *curl_mime_addpart(curl_mime *mime);");
        curl_mime_name = linker.link("CURLcode curl_mime_name(curl_mimepart *part, const char *name);");
        curl_mime_filename = linker.link("CURLcode curl_mime_filename(curl_mimepart *part, const char *filename);");
        curl_mime_type = linker.link("CURLcode curl_mime_type(curl_mimepart *part, const char *mimetype);");
        curl_mime_encoder = linker.link("CURLcode curl_mime_encoder(curl_mimepart *part, const char *encoding);");
        curl_mime_data = linker.link("CURLcode curl_mime_data(curl_mimepart *part, const char *data, size_t datasize);");
        curl_mime_filedata = linker.link("CURLcode curl_mime_filedata(curl_mimepart *part, const char *filename);");
        curl_mime_data_cb = linker.link("CURLcode curl_mime_data_cb(curl_mimepart *part, curl_off_t datasize, curl_read_callback *readfunc, curl_seek_callback *seekfunc, curl_free_callback *freefunc, void *arg);");
        curl_mime_subparts = linker.link("CURLcode curl_mime_subparts(curl_mimepart *part, curl_mime *subparts);");
        curl_mime_headers = linker.link("CURLcode curl_mime_headers(curl_mimepart *part, struct curl_slist *headers, int take_ownership);");

        curl_multi_init = linker.link("CURLM *curl_multi_init(void);");
        curl_multi_add_handle = linker.link("CURLMcode curl_multi_add_handle(CURLM *multi_handle, CURL *curl_handle);");
        curl_multi_remove_handle = linker.link("CURLMcode curl_multi_remove_handle(CURLM *multi_handle, CURL *curl_handle);");
        curl_multi_perform = linker.link("CURLMcode curl_multi_perform(CURLM *multi_handle, int *running_handles);");
        curl_multi_cleanup = linker.link("CURLMcode curl_multi_cleanup(CURLM *multi_handle);");
        curl_multi_info_read = linker.link("CURLMsg *curl_multi_info_read(CURLM *multi_handle, int *msgs_in_queue);");
        curl_multi_strerror = linker.link("const char *curl_multi_strerror(CURLMcode);");
        curl_multi_timeout = linker.link("CURLMcode curl_multi_timeout(CURLM *multi_handle, long *milliseconds);");
        curl_multi_setopt_int = linker.link("CURLMcode curl_multi_setopt(CURLM *multi_handle, CURLMoption option, ...);", "int");
        curl_multi_setopt_long = linker.link("CURLMcode curl_multi_setopt(CURLM *multi_handle, CURLMoption option, ...);", "long");
        curl_multi_setopt_ptr = linker.link("CURLMcode curl_multi_setopt(CURLM *multi_handle, CURLMoption option, ...);", "void*");
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

    public final boolean isCurlImpersonateSupported() {
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

    public final @Nullable curl_slist curl_slist_append(@Nullable curl_slist slist, String data) {
        MemorySegment listPtr = slist != null ? slist.address() : MemorySegment.NULL;
        try (Arena arena = Arena.ofShared()) {
            MemorySegment result = (MemorySegment) curl_slist_append.invokeExact(listPtr, arena.allocateFrom(data));
            if (result.equals(listPtr)) return slist;

            if (result.equals(MemorySegment.NULL)) return null;

            return new curl_slist(result);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final void curl_slist_free_all(@Nullable curl_slist slist) {
        if (slist == null) return;

        try {
            curl_slist_free_all.invokeExact(slist.address());
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final MemorySegment curl_mime_init(MemorySegment curl) {
        try {
            return (MemorySegment) curl_mime_init.invokeExact(curl);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final void curl_mime_free(MemorySegment mime) {
        try {
            curl_mime_free.invokeExact(mime);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final MemorySegment curl_mime_addpart(MemorySegment mime) {
        try {
            return (MemorySegment) curl_mime_addpart.invokeExact(mime);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_mime_name(MemorySegment part, @Nullable String name) {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment namePtr = name != null ? arena.allocateFrom(name) : MemorySegment.NULL;
            return (int) curl_mime_name.invokeExact(part, namePtr);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_mime_filename(MemorySegment part, @Nullable String filename) {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment filenamePtr = filename != null ? arena.allocateFrom(filename) : MemorySegment.NULL;
            return (int) curl_mime_filename.invokeExact(part, filenamePtr);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_mime_type(MemorySegment part, @Nullable String mimetype) {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment mimetypePtr = mimetype != null ? arena.allocateFrom(mimetype) : MemorySegment.NULL;
            return (int) curl_mime_type.invokeExact(part, mimetypePtr);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_mime_encoder(MemorySegment part, @Nullable String encoding) {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment encodingPtr = encoding != null ? arena.allocateFrom(encoding) : MemorySegment.NULL;
            return (int) curl_mime_encoder.invokeExact(part, encodingPtr);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_mime_data(MemorySegment part, byte[] data) {
        try (Arena arena = Arena.ofShared()) {
            return curl_mime_data(part, arena.allocateFrom(ValueLayout.JAVA_BYTE, data));
        }
    }

    public final int curl_mime_data(MemorySegment part, MemorySegment data) {
        try {
            return (int) curl_mime_data.invokeExact(part, data, data.byteSize());
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_mime_filedata(MemorySegment part, @Nullable String filedata) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment fileDataPtr = filedata != null ? arena.allocateFrom(filedata) : MemorySegment.NULL;
            return (int) curl_mime_filedata.invokeExact(part, fileDataPtr);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_mime_data_cb(MemorySegment part, long datasize, MemorySegment readfunc, MemorySegment seekfunc, MemorySegment freefunc, MemorySegment arg) {
        try {
            return (int) curl_mime_data_cb.invokeExact(part, datasize, readfunc, seekfunc, freefunc, arg);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_mime_subparts(MemorySegment part, MemorySegment subparts) {
        try {
            return (int) curl_mime_subparts.invokeExact(part, subparts);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_mime_headers(MemorySegment part, @Nullable curl_slist headers, boolean takeOwnership) {
        return curl_mime_headers(part, headers, takeOwnership ? 1 : 0);
    }

    public final int curl_mime_headers(MemorySegment part, @Nullable curl_slist headers, int takeOwnership) {
        try {
            MemorySegment headersPtr = headers != null ? headers.address() : MemorySegment.NULL;
            return (int) curl_mime_headers.invokeExact(part, headersPtr, takeOwnership);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final MemorySegment curl_multi_init() {
        try {
            return (MemorySegment) curl_multi_init.invokeExact();
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_multi_add_handle(MemorySegment multi, MemorySegment curl) {
        try {
            return (int) curl_multi_add_handle.invokeExact(multi, curl);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_multi_remove_handle(MemorySegment multi, MemorySegment curl) {
        try {
            return (int) curl_multi_remove_handle.invokeExact(multi, curl);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_multi_perform(MemorySegment multi, MemorySegment running_handles) {
        try {
            return (int) curl_multi_perform.invokeExact(multi, running_handles);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_multi_cleanup(MemorySegment multi) {
        try {
            return (int) curl_multi_cleanup.invokeExact(multi);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final @Nullable CURLMsg curl_multi_info_read(MemorySegment multi, MemorySegment msgs_in_queue) {
        try {
            return safeGetStruct((MemorySegment) curl_multi_info_read.invokeExact(multi, msgs_in_queue), CURLMsg::new);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final String curl_multi_strerror(int code) {
        try {
            return readNTString((MemorySegment) curl_multi_strerror.invokeExact(code));
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_multi_timeout(MemorySegment multi, MemorySegment milliseconds) {
        try {
            return (int) curl_multi_timeout.invokeExact(multi, milliseconds);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_multi_setopt(MemorySegment multi, int option, int value) {
        try {
            return (int) curl_multi_setopt_int.invokeExact(multi, option, value);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_multi_setopt(MemorySegment multi, int option, long value) {
        try {
            return (int) curl_multi_setopt_long.invokeExact(multi, option, value);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    public final int curl_multi_setopt(MemorySegment multi, int option, MemorySegment value) {
        try {
            return (int) curl_multi_setopt_ptr.invokeExact(multi, option, value);
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }

    private static <T> @Nullable T safeGetStruct(MemorySegment addr, Function<MemorySegment, T> func) {
        if (addr.equals(MemorySegment.NULL)) return null;

        return func.apply(addr);
    }
}
