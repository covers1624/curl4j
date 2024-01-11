package net.covers1624.curl4j.util.internal;

import net.covers1624.curl4j.util.CurlHandle;
import net.covers1624.curl4j.util.CurlMultiHandle;
import org.jetbrains.annotations.ApiStatus;

import java.lang.ref.Cleaner;
import java.util.concurrent.atomic.AtomicLong;

import static net.covers1624.curl4j.CURL.curl_easy_cleanup;
import static net.covers1624.curl4j.CURL.curl_multi_cleanup;

/**
 * Created by covers1624 on 11/1/24.
 */
@ApiStatus.Internal
public class CleaningCurlHandleFactory implements CurlHandleFactory {

    private final Cleaner CLEANER = Cleaner.create();

    @Override
    public CurlHandle newHandle(AtomicLong a_curl) {
        long curl = a_curl.get();
        CleaningCurlHandle handle = new CleaningCurlHandle(a_curl);
        CLEANER.register(handle, () -> {
            if (a_curl.compareAndSet(curl, 0)) {
                curl_easy_cleanup(curl);
            }
        });
        return handle;
    }

    @Override
    public CurlMultiHandle newMultiHandle(AtomicLong a_curl, AtomicLong a_multi) {
        long curl = a_curl.get();
        long multi = a_multi.get();
        CleaningCurlMultiHandle handle = new CleaningCurlMultiHandle(a_curl, a_multi);
        CLEANER.register(handle, () -> {
            if (a_curl.compareAndSet(curl, 0)) {
                curl_easy_cleanup(curl);
            }
            if (a_multi.compareAndSet(multi, 0)) {
                curl_multi_cleanup(multi);
            }
        });

        return handle;
    }

    private static class CleaningCurlHandle extends CurlHandle {

        public CleaningCurlHandle(AtomicLong curl) {
            super(curl);
        }
    }

    private static class CleaningCurlMultiHandle extends CurlMultiHandle {

        public CleaningCurlMultiHandle(AtomicLong curl, AtomicLong multi) {
            super(curl, multi);
        }
    }
}
