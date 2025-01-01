/*
 * This file is part of Quack and is Licensed under the MIT License.
 */
package net.covers1624.curl4j.httpapi;

import net.covers1624.curl4j.CURLMsg;
import net.covers1624.curl4j.CurlWriteCallback;
import net.covers1624.curl4j.CurlXferInfoCallback;
import net.covers1624.curl4j.util.*;
import net.covers1624.quack.net.httpapi.HeaderList;
import net.covers1624.quack.net.httpapi.WebBody;
import net.covers1624.quack.util.SneakyUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Consumer;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static net.covers1624.curl4j.CURL.*;

/**
 * Created by covers1624 on 10/1/24.
 */
class IncrementalCurl4jResponse extends Curl4jEngineResponse {

    // 64k buffer.
    private Arena bufferArena = Arena.ofAuto();
    private MemorySegment buf = bufferArena.allocate(JAVA_BYTE, 64 * 1024);
    private ByteBuffer buffer = buf.asByteBuffer();

    private boolean done;
    private boolean paused;

    private final Curl4jEngineRequest request;
    private final HandlePool<CurlMultiHandle>.Entry handleEntry;
    private final CurlMultiHandle handle;

    private final @Nullable CurlInput input;
    private final @Nullable CurlMimeBody mimeBody;
    private final SListHeaderWrapper headers;
    private final @Nullable CurlXferInfoCallback xferCallback;

    private final int statusCode;

    private final HeaderList responseHeaders = new HeaderList();

    private final CurlWriteCallback writeCallback = new CurlWriteCallback((ptr, size, nmemb, userdata) -> {
        int rs = (int) (size * nmemb);
        if (rs == 0) return rs;

        // If our buffer is too small to consume this piece of data.
        if (buffer.remaining() < rs) {
            // If we already have a chunk of data, pause the transfer.
            if (buffer.position() != 0) {
                paused = true;
                return CURL_WRITEFUNC_PAUSE;
            }
            // We must grow the buffer, we can't pause as we have no data,
            // and we can't partially consume.
            growBuffer(rs - buffer.remaining());
        }
        buf.asSlice(buffer.position()).copyFrom(ptr);
        buffer.position(buffer.position() + rs);
        return rs;
    });
    private final InputStream is = new InputStream() {
        @Override
        public int read() throws IOException {
            if (buffer.remaining() == 0) {
                if (done) return -1;
                fillBuffer();
            }
            return buffer.get() & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (buffer.remaining() == 0) {
                if (done) return -1;
                fillBuffer();
            }
            int l = Math.min(len, buffer.remaining());
            buffer.get(b, off, l);
            return l;
        }
    };
    private final ReadableByteChannel channel = new ReadableByteChannel() {
        private boolean open = true;

        @Override
        public int read(ByteBuffer dst) throws IOException {
            if (buffer.remaining() == 0) {
                if (done) return -1;
                fillBuffer();
            }
            int toRead = Math.min(dst.remaining(), buffer.remaining());
            int oldL = buffer.limit();
            buffer.limit(buffer.position() + toRead);
            dst.put(buffer);
            buffer.limit(oldL);
            return toRead;
        }

        @Override
        public boolean isOpen() {
            return open;
        }

        @Override
        public void close() {
            open = false;
        }
    };
    private final WebBody webBody;

    public IncrementalCurl4jResponse(Curl4jEngineRequest request, HandlePool<CurlMultiHandle>.Entry handleEntry) throws IOException {
        this.request = request;
        this.handleEntry = handleEntry;

        handle = handleEntry.handle;
        input = request.makeInput();
        mimeBody = request.buildMime(handle);
        headers = new SListHeaderWrapper(request.headers().toStrings());
        xferCallback = request.xferCallback(request.listener());

        if (input != null) {
            input.apply(handle);
        } else if (mimeBody != null) {
            mimeBody.apply(handle);
        }
        headers.apply(handle);

        HeaderCollector headerCollector = new HeaderCollector();
        headerCollector.apply(handle);

        curl_easy_setopt(handle.curl, CURLOPT_WRITEFUNCTION, writeCallback);

        if (request.caBundle() != null) {
            request.caBundle().apply(handle);
        }

        if (xferCallback != null) {
            curl_easy_setopt(handle.curl, CURLOPT_NOPROGRESS, false);
            curl_easy_setopt(handle.curl, CURLOPT_XFERINFOFUNCTION, xferCallback);
        }

        curl_easy_setopt(handle.curl, CURLOPT_NOSIGNAL, true);

        for (Consumer<CurlHandle> customOption : request.customOptions()) {
            customOption.accept(handle);
        }

        curl_multi_add_handle(handle.multi, handle.curl);

        // Fill the buffer! This will populate all headers and response codes.
        fillBuffer();
        long[] statusCode = { 0 };
        int result = curl_easy_getinfo_long(handle.curl, CURLINFO_RESPONSE_CODE, statusCode);
        if (result != CURLE_OK) {
            throw new IOException("Unable to get CURLINFO_RESPONSE_CODE result. " + curl_easy_strerror(result) + "/" + handle.errorBuffer);
        }
        this.statusCode = (int) statusCode[0];

        responseHeaders.addAllMulti(headerCollector.getHeaders());
        String contentType = responseHeaders.get("Content-Type");
        String len = responseHeaders.get("Content-Length");
        long contentLength = len != null && !len.isEmpty() ? Long.parseLong(len) : -1;

        // @formatter:off
        webBody = new WebBody() {
            @Override public InputStream open() { return is; }
            @Override public ReadableByteChannel openChannel() { return channel; }
            @Override public boolean multiOpenAllowed() { return false; }
            @Override public long length() { return contentLength; }
            @Override public @Nullable String contentType() { return contentType; }
        };
        // @formatter:on
    }

    private void fillBuffer() throws IOException {
        assert buffer.position() == 0 || buffer.position() == buffer.limit() : "Buffer must either be empty or fully consumed.";

        // Reset position to 0.
        buffer.position(0);
        // Reset limit to our capacity.
        buffer.limit(buffer.capacity());

        // If we were paused, unpause.
        if (paused) {
            paused = false;
            curl_easy_pause(handle.curl, CURLPAUSE_RECV_CONT);
        }

        try (Arena arena = Arena.ofShared()) {
            MemorySegment nHandles = arena.allocate(ValueLayout.ADDRESS, 1);
            // Do work until we are finished, or we paused.
            while (!done && !paused) {
                int ret = curl_multi_perform(handle.multi, nHandles);

                // curl multi is not healthy.
                if (ret != CURLM_OK) throw new Curl4jHttpException("Curl multi returned error: " + handle.errorBuffer + "(" + curl_multi_strerror(ret) + ")");

                // curl_multi_perform gives us an out pointer for the number of active curl requests.
                done = nHandles.get(ValueLayout.JAVA_INT, 0) == 0;
            }
            // We are done!
            if (done) {
                // curl multi puts the curl results into a consumable list of messages for us to process
                // currently (curl 8.2.1) only has a single CURLMSG result type.
                CURLMsg msg;
                while ((msg = curl_multi_info_read(handle.multi, nHandles)) != null) {
                    if (msg.msg() != CURLMSG_DONE) continue;
                    int ret = (int) msg.data();
                    if (ret != CURLE_OK) {
                        throw new Curl4jHttpException("Curl returned error: " + handle.errorBuffer + "(" + curl_easy_strerror(ret) + ")");
                    }
                }
            }
        }
        // Flip the buffer position and limit. l = p; p = 0
        buffer.flip();
    }

    private void growBuffer(int more) {
        // FFM api has no way to reallocate a memory segment efficiently.
        // We must allocate a new segment and copy the data.
        // Calculate new buffer size.
        // We simply let the old arena and segment get GC'd auto cleaning up the native memory
        // We can't 'un allocate' memory from an arena either.

        int newSize = buffer.limit() + more;
        Arena newArena = Arena.ofAuto();
        // Allocate new buffer
        MemorySegment newSegment = newArena.allocate(JAVA_BYTE, newSize);
        ByteBuffer newBuffer = newSegment.asByteBuffer();
        // Copy the data.
        newSegment.copyFrom(buf);
        // Carry position
        newBuffer.position(buffer.position());

        buf = newSegment;
        buffer = newBuffer;
        bufferArena = newArena;
    }

    @Override
    public void close() throws IOException {
        // Removing the curl handle from the multi handle will abort the request
        // if one is still running.
        curl_multi_remove_handle(handle.multi, handle.curl);
        closeSafe(input, mimeBody, headers, handleEntry);
        if (request.listener() != null) {
            request.listener().end();
        }
    }

    private static void closeSafe(AutoCloseable... closeables) {
        Throwable exception = null;
        for (AutoCloseable closeable : closeables) {
            if (closeable == null) continue;

            try {
                closeable.close();
            } catch (Throwable ex) {
                if (exception == null) {
                    exception = ex;
                } else {
                    exception.addSuppressed(ex);
                }
            }
        }
        if (exception != null) {
            SneakyUtils.throwUnchecked(exception);
        }
    }

    // @formatter:off
    @Override public Curl4jEngineRequest request() { return request; }
    @Override public int statusCode() { return statusCode; }
    @Override public String message() { return ""; }
    @Override public HeaderList headers() { return responseHeaders; }
    @Override public WebBody body() { return webBody; }
    // @formatter:on
}
