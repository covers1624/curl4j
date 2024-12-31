package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CurlWriteCallback;
import net.covers1624.curl4j.core.Memory;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static net.covers1624.curl4j.CURL.CURLOPT_WRITEFUNCTION;
import static net.covers1624.curl4j.CURL.curl_easy_setopt;

/**
 * A simple wrapper around {@link CurlWriteCallback} writing data
 * to a {@link WritableByteChannel}, {@link InputStream}, or {@link Path}/{@link File}.
 *
 * @author covers1624
 * @see MemoryCurlOutput
 */
public class CurlOutput implements Closeable, CurlBindable {

    private final OutputSupplier<WritableByteChannel> channelSupplier;

    private @Nullable CurlWriteCallback callback;
    private @Nullable WritableByteChannel channel;
    private boolean closed;

    protected CurlOutput(OutputSupplier<WritableByteChannel> channelSupplier) {
        this.channelSupplier = channelSupplier;
    }

    /**
     * Create a new {@link CurlOutput} from a {@link WritableByteChannel}.
     * <p>
     * The provided supplier will only be called once, when data is first written.
     *
     * @param supplier The supplier which provides a {@link WritableByteChannel}.
     * @return The new {@link CurlOutput}.
     */
    public static CurlOutput toChannel(OutputSupplier<WritableByteChannel> supplier) {
        return new CurlOutput(supplier);
    }

    /**
     * Create a new {@link CurlOutput} from a {@link OutputStream}.
     * <p>
     * The provided supplier will only be called once, when data is first written.
     *
     * @param supplier The supplier which provides a {@link OutputStream}.
     * @return The new {@link CurlOutput}.
     */
    public static CurlOutput toStream(OutputSupplier<OutputStream> supplier) {
        return toChannel(() -> Channels.newChannel(supplier.open()));
    }

    /**
     * Create a new {@link CurlOutput} from a {@link Path}.
     * <p>
     * The open flags for the file are {@link StandardOpenOption#WRITE} and
     * {@link StandardOpenOption#CREATE}. If these flags are not sufficient, you will
     * need to implement your own using {@link #toChannel}
     * <p>
     * This implementation will create parent directories if they don't exist.
     *
     * @param path The path to write to.
     * @return The new {@link CurlOutput}.
     */
    public static CurlOutput toFile(Path path) {
        return toChannel(() -> {
            Path parent = path.getParent();
            if (parent != null && !Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
            return Files.newByteChannel(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        });
    }

    /**
     * Overload of {@link #toFile(Path)} but for {@link File}.
     *
     * @param file The file.
     * @return The new {@link CurlOutput}.
     */
    public static CurlOutput toFile(File file) {
        return toFile(file.toPath());
    }

    /**
     * @return The {@link CurlWriteCallback} function.
     */
    public CurlWriteCallback callback() {
        if (closed) throw new IllegalStateException("Already closed.");

        if (callback == null) {
            callback = new CurlWriteCallback((ptr, size, nmemb, userdata) -> {
                if (channel == null) {
                    channel = channelSupplier.open();
                }

                int rs = (int) (size * nmemb);
                return channel.write(ptr.reinterpret(rs).asByteBuffer());
            });
        }
        return callback;
    }

    @Override
    public void apply(MemorySegment curl) {
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, callback());
    }

    @Override
    public void close() throws IOException {
        if (closed) return;

        if (channel != null) channel.close();
        closed = true;
    }

    @FunctionalInterface
    public interface OutputSupplier<T> {

        T open() throws IOException;
    }
}
