package net.covers1624.curl4j;

import fi.iki.elonen.NanoHTTPD;
import net.covers1624.curl4j.tests.TestBase;
import net.covers1624.curl4j.tests.TestWebServer;
import net.covers1624.curl4j.util.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.Response.Status.OK;
import static net.covers1624.curl4j.CURL.*;
import static net.covers1624.curl4j.tests.TestWebServer.getBody;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by covers1624 on 4/9/23.
 */
public class CURLTests extends TestBase {

    @Test
    public void testCurlVersion() {
        assertNotNull(curl_version());

        curl_version_info_data versionData = curl_version_info(CURLVERSION_MAX);
        assertNotNull(versionData);
        assertNotNull(versionData.getVersion());
    }

    @Test
    public void testDownload() throws IOException {
        byte[] data = randomBytes(32);

        curl_global_init(CURL_GLOBAL_DEFAULT);
        MemorySegment curl = curl_easy_init();

        try (TestWebServer server = new TestWebServer()) {
            server.addHandler("/", r -> {
                assertEquals(NanoHTTPD.Method.GET, r.getMethod());
                return bytesResponse(OK, data);
            });

            curl_easy_reset(curl);

            curl_easy_setopt(curl, CURLOPT_URL, server.addr("/"));
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "GET");

            try (MemoryCurlOutput output = MemoryCurlOutput.create()) {
                curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, output.callback());

                int result = curl_easy_perform(curl);

                assertEquals(CURLE_OK, result, () -> curl_easy_strerror(result));
                assertEquals(200, curl_easy_getinfo_long(curl, CURLINFO_RESPONSE_CODE).result());
                assertEquals("127.0.0.1", curl_easy_getinfo_String(curl, CURL.CURLINFO_PRIMARY_IP).result());
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
        MemorySegment curl = curl_easy_init();

        try (TestWebServer server = new TestWebServer()) {
            server.addHandler("/", r -> {
                assertEquals(NanoHTTPD.Method.PUT, r.getMethod());
                assertArrayEquals(data, getBody(r));
                return NanoHTTPD.newFixedLengthResponse(OK, null, null, -1);
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
                assertEquals(200, curl_easy_getinfo_long(curl, CURLINFO_RESPONSE_CODE).result());
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
        MemorySegment curl = curl_easy_init();

        try (TestWebServer server = new TestWebServer()) {
            server.addHandler("/", r -> {
                assertEquals(NanoHTTPD.Method.GET, r.getMethod());
                NanoHTTPD.Response response = bytesResponse(OK, data);
                response.addHeader("X-Magic", r.getHeaders().get("x-magic"));
                return response;
            });

            curl_easy_reset(curl);

            curl_easy_setopt(curl, CURLOPT_URL, server.addr("/"));
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "GET");

            try (MemoryCurlOutput output = MemoryCurlOutput.create();
                 SListHeaderWrapper headerOutput = new SListHeaderWrapper("X-Magic: " + header)) {
                HeaderCollector headerCollector = new HeaderCollector();
                curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, output.callback());

                curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, headerCollector.callback());

                curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headerOutput.get());

                int result = curl_easy_perform(curl);

                assertEquals(CURLE_OK, result, () -> curl_easy_strerror(result));
                assertEquals(200, curl_easy_getinfo_long(curl, CURLINFO_RESPONSE_CODE).result());
                assertEquals(header, headerCollector.getHeaders().get("X-Magic").get(0));
                assertArrayEquals(data, output.bytes());
            }
        } finally {
            curl_easy_cleanup(curl);
            curl_global_cleanup();
        }
    }

    @Test
    public void testCABundle() throws IOException {
        byte[] data = randomBytes(32);

        curl_global_init(CURL_GLOBAL_DEFAULT);
        MemorySegment curl = curl_easy_init();

        try (TestWebServer server = new TestWebServer("/selfsigned.jks", "password")) {
            server.addHandler("/", r -> {
                assertEquals(NanoHTTPD.Method.GET, r.getMethod());
                return bytesResponse(OK, data);
            });

            curl_easy_reset(curl);

            new CABundle(server.getCertBytes("key")).apply(curl);

            curl_easy_setopt(curl, CURLOPT_URL, server.addr("/"));
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "GET");

            curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, true);

            try (MemoryCurlOutput output = MemoryCurlOutput.create()) {
                curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, output.callback());

                int result = curl_easy_perform(curl);

                assertEquals(CURLE_OK, result, () -> curl_easy_strerror(result));
                assertEquals(200, curl_easy_getinfo_long(curl, CURLINFO_RESPONSE_CODE).result());
                assertArrayEquals(data, output.bytes());
            }
        } finally {
            curl_easy_cleanup(curl);
            curl_global_cleanup();
        }
    }

    @Test
    public void testMimeBody() throws IOException {
        byte[] data = randomBytes(32);

        curl_global_init(CURL_GLOBAL_DEFAULT);
        MemorySegment curl = curl_easy_init();

        try (TestWebServer server = new TestWebServer()) {
            server.addHandler("/", r -> {
                assertEquals(NanoHTTPD.Method.POST, r.getMethod());
                Map<String, String> body = new HashMap<>();
                r.parseBody(body);
                assertEquals(2, body.size());

                for (String value : body.values()) {
                    assertFalse(value.isEmpty());
                    Path path = Paths.get(value);
                    assertTrue(Files.exists(path));
                    assertArrayEquals(data, Files.readAllBytes(path));
                }

                return NanoHTTPD.newFixedLengthResponse(OK, null, null, -1);
            });

            curl_easy_reset(curl);

            curl_easy_setopt(curl, CURLOPT_URL, server.addr("/"));
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "POST");

            CurlMimeBody.Builder mimeBodyBuilder = CurlMimeBody.builder(curl)
                    // Setting fileName on these reveals a bug in NanoHTTPD, it incorrectly appends increments to the end of one of the names.
                    .addPart("byteArray").type("application/octet-stream").body(data).build()
                    .addPart("dynamicInput").type("application/octet-stream").body(CurlInput.fromBytes(data)).build();

            try (MemoryCurlOutput output = MemoryCurlOutput.create();
                 CurlMimeBody body = mimeBodyBuilder.build()) {
                curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, output.callback());

                curl_easy_setopt(curl, CURLOPT_MIMEPOST, body.getMime());

                int result = curl_easy_perform(curl);

                assertEquals(CURLE_OK, result, () -> curl_easy_strerror(result));
                assertEquals(200, curl_easy_getinfo_long(curl, CURLINFO_RESPONSE_CODE).result());
            }
        } finally {
            curl_easy_cleanup(curl);
            curl_global_cleanup();
        }
    }
}
