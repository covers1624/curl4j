package net.covers1624.curl4j.util;

import net.covers1624.curl4j.CURL;

import java.lang.ref.Cleaner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.LongFunction;

/**
 * A {@link CurlHandle} implementation which uses Java9+
 * {@link Cleaner}s instead of {@link Object#finalize()}
 * to clean up curl handles.
 * <p>
 * Created by covers1624 on 23/10/23.
 */
public class CleaningCurlHandle extends CurlHandle {

    private static final Cleaner CLEANER = Cleaner.create();

    public CleaningCurlHandle(long curl) {
        this(curl, new AtomicBoolean());
    }

    private CleaningCurlHandle(long curl, AtomicBoolean cleaned) {
        super(curl, cleaned);
        CLEANER.register(this, () -> {
            if (!cleaned.get()) {
                CURL.curl_easy_cleanup(curl);
            }
        });
    }

    public static class Factory implements LongFunction<CurlHandle> {

        @Override
        public CurlHandle apply(long value) {
            return new CleaningCurlHandle(value);
        }
    }
}
