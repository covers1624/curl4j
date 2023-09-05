package net.covers1624.curl4j;

import fi.iki.elonen.NanoHTTPD;
import net.covers1624.curl4j.tests.TestWebServer;
import net.covers1624.curl4j.util.CurlInput;
import net.covers1624.curl4j.util.HeaderCollector;
import net.covers1624.curl4j.util.MemoryCurlOutput;
import net.covers1624.curl4j.util.SListHeaderWrapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static net.covers1624.curl4j.CURL.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by covers1624 on 4/9/23.
 */
public class CURLTests {

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    @Test
    public void testCurlVersion() {
        assertNotNull(curl_version());

        curl_version_info_data versionData = curl_version_info();
        assertNotNull(versionData);
        assertNotNull(versionData.getVersion());
    }

    @Test
    public void testDownload() throws IOException {
        byte[] data = randomBytes(32);

        curl_global_init(CURL_GLOBAL_DEFAULT);
        long curl = curl_easy_init();

        try (TestWebServer server = new TestWebServer()) {
            server.addHandler("/", r -> {
                assertEquals(NanoHTTPD.Method.GET, r.getMethod());
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, null, new ByteArrayInputStream(data), data.length);
            });

            curl_easy_reset(curl);

            curl_easy_setopt(curl, CURLOPT_URL, server.addr("/"));
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "GET");

            try (MemoryCurlOutput output = MemoryCurlOutput.create()) {
                curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, output.callback());

                int result = curl_easy_perform(curl);

                assertEquals(CURLE_OK, result, () -> curl_easy_strerror(result));
                assertEquals(200, curl_easy_getinfo_long(curl, CURLINFO_RESPONSE_CODE));
                assertArrayEquals(data, output.bytes());
            }
        } finally {
            curl_easy_cleanup(curl);
            curl_global_cleanup();
        }
    }

    @Test
    public void testUpload() throws IOException {
        byte[] data = randomBytes(32);

        curl_global_init(CURL_GLOBAL_DEFAULT);
        long curl = curl_easy_init();

        try (TestWebServer server = new TestWebServer()) {
            server.addHandler("/", r -> {
                assertEquals(NanoHTTPD.Method.PUT, r.getMethod());
                assertArrayEquals(data, getBody(r));
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, null, null, -1);
            });

            curl_easy_reset(curl);

            curl_easy_setopt(curl, CURLOPT_URL, server.addr("/"));
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "PUT");

            try (MemoryCurlOutput output = MemoryCurlOutput.create();
                 CurlInput input = CurlInput.fromBytes(data)) {
                curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, output.callback());

                curl_easy_setopt(curl, CURLOPT_UPLOAD, true);
                curl_easy_setopt(curl, CURLOPT_INFILESIZE_LARGE, input.availableBytes());
                curl_easy_setopt(curl, CURLOPT_READFUNCTION, input.callback());

                int result = curl_easy_perform(curl);

                assertEquals(CURLE_OK, result, () -> curl_easy_strerror(result));
                assertEquals(200, curl_easy_getinfo_long(curl, CURLINFO_RESPONSE_CODE));
            }
        } finally {
            curl_easy_cleanup(curl);
            curl_global_cleanup();
        }
    }

    @Test
    public void testHeaders() throws IOException {
        byte[] data = randomBytes(32);
        String header = randomHex(32);

        curl_global_init(CURL_GLOBAL_DEFAULT);
        long curl = curl_easy_init();

        try (TestWebServer server = new TestWebServer()) {
            server.addHandler("/", r -> {
                assertEquals(NanoHTTPD.Method.GET, r.getMethod());
                NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, null, new ByteArrayInputStream(data), data.length);
                response.addHeader("X-Magic", r.getHeaders().get("x-magic"));
                return response;
            });

            curl_easy_reset(curl);

            curl_easy_setopt(curl, CURLOPT_URL, server.addr("/"));
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "GET");

            try (MemoryCurlOutput output = MemoryCurlOutput.create();
                 HeaderCollector headerCollector = new HeaderCollector();
                 SListHeaderWrapper headerOutput = new SListHeaderWrapper("X-Magic: " + header)) {
                curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, output.callback());

                curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, headerCollector.callback());

                curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headerOutput.get());

                int result = curl_easy_perform(curl);

                assertEquals(CURLE_OK, result, () -> curl_easy_strerror(result));
                assertEquals(200, curl_easy_getinfo_long(curl, CURLINFO_RESPONSE_CODE));
                assertEquals(header, headerCollector.getHeaders().get("X-Magic").get(0));
                assertArrayEquals(data, output.bytes());
            }
        } finally {
            curl_easy_cleanup(curl);
            curl_global_cleanup();
        }
    }

    private static String randomHex(int len) {
        Random rand = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append(HEX[rand.nextInt(HEX.length)]);
        }
        return builder.toString();
    }

    private static byte[] randomBytes(int len) {
        byte[] data = new byte[len];
        new Random().nextBytes(data);
        return data;
    }

    // Thanks NanoHTTPD, your _fantastic_ at this. /s
    private static byte[] getBody(NanoHTTPD.IHTTPSession session) throws IOException {
        String lenStr = session.getHeaders().get("content-length");
        if (lenStr == null) throw new RuntimeException("Expected Content-Length header.");
        int nBytes = Integer.parseInt(lenStr);

        InputStream is = session.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int total = 0;
        int len;
        byte[] buf = new byte[1024];
        while (total < nBytes && (len = is.read(buf, 0, Math.min(buf.length, nBytes - total))) != -1) {
            bos.write(buf, 0, len);
            total += len;
        }
        return bos.toByteArray();
    }
}
