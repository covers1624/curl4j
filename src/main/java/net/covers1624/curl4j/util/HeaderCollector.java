package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CurlHeaderCallback;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple wrapper around {@link CurlHeaderCallback} which
 * neatly collects all headers into a {@link Map}.
 * <p>
 * This implementation uses a {@link LinkedHashMap}, maintaining the overall
 * order of headers, however, duplicate header names will not have their order
 * preserved.
 * // TODO HeaderList from quack as extension?
 *
 * @author covers1624
 */
public class HeaderCollector implements AutoCloseable {

    private final Map<String, List<String>> headers = new LinkedHashMap<>();

    private @Nullable CurlHeaderCallback callback;
    private boolean closed;

    public HeaderCollector() {
    }

    public CurlHeaderCallback callback() {
        if (closed) throw new IllegalStateException("Already closed");

        if (callback == null) {
            callback = new CurlHeaderCallback((header, userdata) -> {
                int colon = header.indexOf(":");
                if (colon == -1) {
                    // Curl will give us all raw header lines, even for intermediate requests, (redirects, etc)
                    if (header.startsWith("HTTP/")) {
                        headers.clear();
                    }
                    // This is not a header, might be garbage or blank line.
                    return;
                }

                String[] split = header.trim().split(":", 2);
                headers.computeIfAbsent(split[0], e -> new ArrayList<>(1)).add(split[1].trim());
            });
        }
        return callback;
    }

    /**
     * Get the collected headers.
     *
     * @return The headers.
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    @Override
    public void close() {
        if (callback != null) callback.close();
        closed = true;
    }
}
