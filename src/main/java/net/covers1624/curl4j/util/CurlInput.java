package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CurlReadCallback;
import net.covers1624.curl4j.CurlSeekCallback;
import net.covers1624.curl4j.core.Memory;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.covers1624.curl4j.CURL.*;

/**
 * A simple wrapper around {@link CurlReadCallback} supplying data
 * from a {@link ReadableByteChannel}, {@link InputStream}, or {@link Path}/{@link File}.
 *
 * @author covers1624
 */
public abstract class CurlInput implements Closeable, CurlBindable {

    private @Nullable CurlReadCallback readCallback;
    private @Nullable CurlSeekCallback seekCallback;
    private @Nullable ReadableByteChannel channel;
    private boolean closed;

    /**
     * Create a new {@link CurlInput} from a {@link ReadableByteChannel}.
     * <p>
     * The provided supplier will only be called once, when data is first requested.
     *
     * @param supplier The supplier which provides a {@link ReadableByteChannel}.
     * @return The new {@link CurlInput}.
     */
    public static CurlInput fromChannel(InputSupplier<ReadableByteChannel> supplier) {
        return new CurlInput() {
            @Override
            protected ReadableByteChannel open() throws IOException {
                return supplier.open();
            }

            @Override
            public long availableBytes() {
                return -1;
            }
        };
    }

    /**
     * Create a new {@link CurlInput} from a {@link InputStream}.
     * <p>
     * The provided supplier will only be called once, when data is first requested.
     *
     * @param supplier The supplier which provides a {@link InputStream}.
     * @return The new {@link CurlInput}.
     */
    public static CurlInput fromStream(InputSupplier<InputStream> supplier) {
        return new CurlInput() {
            private @Nullable InputStream is;

            @Override
            protected ReadableByteChannel open() throws IOException {
                return Channels.newChannel(getStream());
            }

            @Override
            public long availableBytes() throws IOException {
                return getStream().available();
            }

            private InputStream getStream() throws IOException {
                if (is == null) {
                    is = supplier.open();
                }
                return is;
            }
        };
    }

    /**
     * Create a new {@link CurlInput} from a {@link Path}.
     *
     * @param path The path to read from.
     * @return The new {@link CurlInput}.
     */
    public static CurlInput fromFile(Path path) {
        return new CurlInput() {
            @Override
            protected ReadableByteChannel open() throws IOException {
                return Files.newByteChannel(path);
            }

            @Override
            public long availableBytes() throws IOException {
                return Files.size(path);
            }
        };
    }

    /**
     * Create a new {@link CurlInput} from a {@link File}.
     *
     * @param path The file to read from.
     * @return The new {@link CurlInput}.
     */
    public static CurlInput fromFile(File path) {
        return fromFile(path.toPath());
    }

    /**
     * Create a new {@link CurlInput} from a {@link byte[]}.
     *
     * @param bytes The bytes.
     * @return The new {@link CurlInput}.
     */
    public static CurlInput fromBytes(byte[] bytes) {
        return new CurlInput() {
            @Override
            protected ReadableByteChannel open() throws IOException {
                return Channels.newChannel(new ByteArrayInputStream(bytes));
            }

            @Override
            public long availableBytes() throws IOException {
                return bytes.length;
            }
        };
    }

    /**
     * @return The {@link CurlReadCallback} function.
     */
    public CurlReadCallback callback() {
        if (closed) throw new IllegalStateException("Already closed.");

        if (readCallback == null) {
            readCallback = new CurlReadCallback((ptr, size, nmemb, userdata) -> {
                if (channel == null) {
                    channel = open();
                }

                int rs = (int) (size * nmemb);
                int len = channel.read(ptr.reinterpret(rs).asByteBuffer());
                return len != -1 ? len : 0;

            });
        }
        return readCallback;
    }

    /**
     * @return The {@link CurlSeekCallback} function.
     */
    public CurlSeekCallback seekCallback() {
        if (closed) throw new IllegalStateException("Already closed.");

        if (seekCallback == null) {
            seekCallback = new CurlSeekCallback(((userdata, offset, origin) -> {
                if (origin != SEEK_SET) return CURL_SEEKFUNC_CANTSEEK;

                if (!(channel instanceof SeekableByteChannel)) {
                    return CURL_SEEKFUNC_CANTSEEK;
                }
                ((SeekableByteChannel) channel).position(offset);
                return CURL_SEEKFUNC_OK;
            }));
        }
        return seekCallback;
    }

    @Override
    public void apply(MemorySegment curl) throws IOException {
        long len = availableBytes();
        if (len == -1) throw new IllegalStateException("Must have a known length.");

        curl_easy_setopt(curl, CURLOPT_UPLOAD, true);
        curl_easy_setopt(curl, CURLOPT_INFILESIZE_LARGE, len);
        curl_easy_setopt(curl, CURLOPT_READFUNCTION, callback());
        curl_easy_setopt(curl, CURLOPT_SEEKFUNCTION, seekCallback());
    }

    protected abstract ReadableByteChannel open() throws IOException;

    /**
     * @return The bytes available. May be 0 or -1 if the size is unknown.
     */
    public abstract long availableBytes() throws IOException;

    @Override
    public void close() throws IOException {
        if (closed) return;

        if (channel != null) channel.close();
        closed = true;
    }

    @FunctionalInterface
    public interface InputSupplier<T> {

        T open() throws IOException;
    }
}
