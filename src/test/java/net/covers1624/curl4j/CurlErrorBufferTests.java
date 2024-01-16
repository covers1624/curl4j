package net.covers1624.curl4j;

import net.covers1624.curl4j.tests.TestBase;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by covers1624 on 16/1/24.
 */
public class CurlErrorBufferTests extends TestBase {

    @Test
    public void testEmpty() {
        assertEquals("", new ErrorBuffer().toString());
    }

    @Test
    public void testData() {
        ErrorBuffer buffer = new ErrorBuffer();
        String data = randomHex(ErrorBuffer.CURL_ERROR_SIZE / 2);
        buffer.buf.put(data.getBytes(StandardCharsets.UTF_8));
        buffer.buf.put((byte) '\0');
        buffer.buf.position(0);
        assertEquals(data, buffer.toString());
    }

    @Test
    public void testAlmostFull() {
        ErrorBuffer buffer = new ErrorBuffer();
        String data = randomHex(ErrorBuffer.CURL_ERROR_SIZE - 1);
        buffer.buf.put(data.getBytes(StandardCharsets.UTF_8));
        buffer.buf.put((byte) '\0');
        buffer.buf.position(0);
        assertEquals(data, buffer.toString());
    }

    @Test
    public void testFull() {
        ErrorBuffer buffer = new ErrorBuffer();
        String data = randomHex(ErrorBuffer.CURL_ERROR_SIZE);
        buffer.buf.put(data.getBytes(StandardCharsets.UTF_8));
        buffer.buf.position(0);
        assertEquals(data, buffer.toString());
    }
}
