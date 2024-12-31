package net.covers1624.curl4j;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static net.covers1624.curl4j.util.ForeignUtils.readNTString;

/**
 * A function callback for handling curl headers.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_HEADERFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlHeaderCallbackI
 */
public class CurlHeaderCallback extends CurlCallback implements CurlHeaderCallbackI {

    private static final CallbackDescriptor DESC = CallbackDescriptor.create(
            "size_t (*curl_write_callback)(char *buffer, size_t size, size_t nitems, void *userdata);",
            () -> MethodHandles.lookup().findVirtual(CurlHeaderCallback.class, "onInvoke", MethodType.methodType(long.class, MemorySegment.class, long.class, long.class, MemorySegment.class))
    );

    private final CurlHeaderCallbackI delegate;

    public CurlHeaderCallback(CurlHeaderCallbackI delegate) {
        super(DESC);
        this.delegate = delegate;
    }

    protected CurlHeaderCallback() {
        super(DESC);
        delegate = this;
    }

    private long onInvoke(MemorySegment ptr, long size, long nmemb, MemorySegment userdata) {
        try (Arena arena = Arena.ofConfined()) {
            long rs = size * nmemb;
            // Strings provided to this callback are not null terminated,
            // they will be exactly the length specified by size * nmemb.
            // We copy the string into a new segment, one byte longer, then
            // parse as a utf-8 string. The new segment is guaranteed to be zero filled.
            MemorySegment nString = arena.allocate(rs + 1)
                    .copyFrom(ptr.reinterpret(rs));
            delegate.onHeader(readNTString(nString), userdata);
            return rs;
        } catch (Throwable ex) {
            handleCallbackException(ex);
            return CURL.CURL_WRITEFUNC_ERROR;
        }
    }

    @Override
    public void onHeader(String header, MemorySegment userdata) {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }
}
