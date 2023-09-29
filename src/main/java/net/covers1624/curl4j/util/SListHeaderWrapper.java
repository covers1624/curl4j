package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CURL;
import net.covers1624.curl4j.curl_slist;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A simple wrapper around {@link curl_slist} for automatic resource management.
 * <p>
 * Can be constructed with raw header lines, or with a {@link Map}.
 * // TODO HeaderList from quack as extension?
 *
 * @author covers1624
 */
public class SListHeaderWrapper implements AutoCloseable {

    private final List<String> headers = new ArrayList<>();

    private @Nullable curl_slist list;
    private boolean closed;

    /**
     * Create a new {@link SListHeaderWrapper} with the provided raw headers.
     * <p>
     * Each line must be in valid http header format: {@code 'Name: Value'}
     *
     * @param headers The headers.
     */
    public SListHeaderWrapper(String... headers) {
        Collections.addAll(this.headers, headers);
    }

    /**
     * Create a new {@link SListHeaderWrapper} with the provided raw headers.
     * <p>
     * Each line must be in valid http header format: {@code 'Name: Value'}
     *
     * @param headers The headers.
     */
    public SListHeaderWrapper(List<String> headers) {
        this.headers.addAll(headers);
    }

    /**
     * Create a new {@link SListHeaderWrapper} with the provided {@link Map} of
     * header names to list of values.
     *
     * @param headers The headers.
     */
    public SListHeaderWrapper(Map<String, List<String>> headers) {
        headers.forEach((name, values) -> values.forEach(value -> this.headers.add(name + ": " + value)));
    }

    /**
     * @return The managed {@link curl_slist} instance.
     */
    public curl_slist get() {
        if (closed) throw new IllegalStateException("Already closed");
        if (headers.isEmpty()) return null;

        if (list == null) {
            for (String header : headers) {
                list = CURL.curl_slist_append(list, header);
            }
        }
        return list;
    }

    @Override
    public void close() {
        CURL.curl_slist_free_all(list);
        closed = true;
    }
}
