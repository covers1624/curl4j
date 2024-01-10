package net.covers1624.curl4j.util;

import java.io.IOException;

/**
 * Utility interface for helpers which can be bound to a curl instance.
 *
 * @author covers1624
 */
public interface CurlBindable {

    default void apply(CurlHandle handle) throws IOException {
        apply(handle.curl);
    }

    void apply(long curl) throws IOException;
}
