package net.covers1624.curl4j.util;

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

        if (JavaVersion.parse(System.getProperty("java.version")) == JavaVersion.JAVA_1_8) {
            assertEquals(CurlHandle.FinalizingCurlHandle.class, handle.getClass());
        } else {
            assertNotEquals(CurlHandle.FinalizingCurlHandle.class, handle.getClass());
        }
    }
}
