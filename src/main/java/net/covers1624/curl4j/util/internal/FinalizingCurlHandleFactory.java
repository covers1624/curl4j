package net.covers1624.curl4j.util.internal;

import net.covers1624.curl4j.util.CurlHandle;
import net.covers1624.curl4j.util.CurlMultiHandle;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by covers1624 on 11/1/24.
 */
@ApiStatus.Internal
public class FinalizingCurlHandleFactory implements CurlHandleFactory {

    @Override
    public CurlHandle newHandle(AtomicLong curl) {
        return new FinalizingCurlHandle(curl);
    }

    @Override
    public CurlMultiHandle newMultiHandle(AtomicLong curl, AtomicLong multi) {
        return new FinalizingCurlMultiHandle(curl, multi);
    }

    public static class FinalizingCurlHandle extends CurlHandle {

        private FinalizingCurlHandle(AtomicLong curl) {
            super(curl);
        }

        @Override
        protected void finalize() {
            close();
        }
    }

    public static class FinalizingCurlMultiHandle extends CurlMultiHandle {

        protected FinalizingCurlMultiHandle(AtomicLong curl, AtomicLong multi) {
            super(curl, multi);
        }

        @Override
        protected void finalize() {
            close();
        }
    }
}
