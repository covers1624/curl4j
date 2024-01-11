package net.covers1624.curl4j.util;

import net.covers1624.curl4j.util.internal.FinalizingCurlHandleFactory;
import net.covers1624.quack.util.JavaVersion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by covers1624 on 23/10/23.
 */
public class TestCurlHandle {

    @Test
    public void testCurlHandleUsesCleaner() {
        CurlHandle handle = CurlHandle.create();
        CurlMultiHandle multiHandle = CurlMultiHandle.createMulti();

        if (JavaVersion.parse(System.getProperty("java.version")) == JavaVersion.JAVA_1_8) {
            assertEquals(FinalizingCurlHandleFactory.FinalizingCurlHandle.class, handle.getClass());
            assertEquals(FinalizingCurlHandleFactory.FinalizingCurlMultiHandle.class, multiHandle.getClass());
        } else {
            assertNotEquals(FinalizingCurlHandleFactory.FinalizingCurlHandle.class, handle.getClass());
            assertNotEquals(FinalizingCurlHandleFactory.FinalizingCurlMultiHandle.class, multiHandle.getClass());
        }
    }
}
