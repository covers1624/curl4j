package net.covers1624.curl4j.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A simple {@link CurlOutput} extension for pure in-memory
 * response bodies.
 *
 * @author covers1624
 */
public class MemoryCurlOutput extends CurlOutput {

    private final ByteArrayOutputStream bos;

    private MemoryCurlOutput(ByteArrayOutputStream bos) {
        super(() -> Channels.newChannel(bos));

        this.bos = bos;
    }

    /**
     * @return The new {@link MemoryCurlOutput}.
     */
    public static MemoryCurlOutput create() {
        return new MemoryCurlOutput(new ByteArrayOutputStream());
    }

    /**
     * Return the content of this output as a UTF-8 {@link String}.
     *
     * @return The string.
     */
    public String string() {
        return string(StandardCharsets.UTF_8);
    }

    /**
     * Return the content of this output as a {@link String}.
     *
     * @param charset The charset for the {@link String}.
     * @return The string.
     */
    public String string(Charset charset) {
        try {
            return bos.toString(charset.name());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Impossible.", ex);
        }
    }

    /**
     * @return The raw bytes of this output.
     */
    public byte[] bytes() {
        return bos.toByteArray();
    }
}
