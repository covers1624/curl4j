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

    CurlHandleFactory INSTANCE = new CleaningCurlHandleFactory();

    CurlHandle newHandle(AtomicLong curl);

    CurlMultiHandle newMultiHandle(AtomicLong curl, AtomicLong multi);
}
