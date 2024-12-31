package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CurlHeaderCallback;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.covers1624.curl4j.CURL.CURLOPT_HEADERFUNCTION;
import static net.covers1624.curl4j.CURL.curl_easy_setopt;

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
public class HeaderCollector implements CurlBindable {

    private final Map<String, List<String>> headers = new LinkedHashMap<>();

    private @Nullable CurlHeaderCallback callback;

    public HeaderCollector() {
    }

    public CurlHeaderCallback callback() {
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

    @Override
    public void apply(MemorySegment curl) {
        curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, callback());
    }

    /**
     * Get the collected headers.
     *
     * @return The headers.
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}
