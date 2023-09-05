package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CurlReadCallback;
import net.covers1624.curl4j.core.Memory;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A simple wrapper around {@link CurlReadCallback} supplying data
 * from a {@link ReadableByteChannel}, {@link InputStream}, or {@link Path}/{@link File}.
 *
 * @author covers1624
 */
public abstract class CurlInput implements Closeable {

    private @Nullable CurlReadCallback callback;
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

        if (callback == null) {
            callback = new CurlReadCallback((ptr, size, nmemb, userdata) -> {
                if (channel == null) {
                    channel = open();
                }

                int rs = (int) (size * nmemb);
                ByteBuffer buffer = Memory.newDirectByteBuffer(ptr, rs);
                int len = channel.read(buffer);
                return len != -1 ? len : 0;

            });
        }
        return callback;
    }

    protected abstract ReadableByteChannel open() throws IOException;

    /**
     * @return The bytes available. May be 0 or -1 if the size is unknown.
     */
    public abstract long availableBytes() throws IOException;

    @Override
    public void close() throws IOException {
        if (closed) return;

        if (callback != null) callback.close();
        if (channel != null) channel.close();
        closed = true;
    }

    @FunctionalInterface
    public interface InputSupplier<T> {

        T open() throws IOException;
    }
}
