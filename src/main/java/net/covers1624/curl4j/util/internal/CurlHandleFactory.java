package net.covers1624.curl4j.util.internal;

import net.covers1624.curl4j.util.CurlHandle;
import net.covers1624.curl4j.util.CurlMultiHandle;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by covers1624 on 11/1/24.
 */
@ApiStatus.Internal
public interface CurlHandleFactory {

    CurlHandleFactory INSTANCE = Internal.selectFactory();

    CurlHandle newHandle(AtomicLong curl);

    CurlMultiHandle newMultiHandle(AtomicLong curl, AtomicLong multi);

    class Internal {

        private static CurlHandleFactory selectFactory() {
            try {
                // Java 9!
                Class.forName("java.lang.ref.Cleaner");
                Class<?> clazz = Class.forName("net.covers1624.curl4j.util.internal.CleaningCurlHandleFactory");
                return (CurlHandleFactory) clazz.getConstructor().newInstance();
            } catch (Throwable ex) {
                // Java 8 fallback, use object finalization.
                return new FinalizingCurlHandleFactory();
            }
        }
    }
}
