package net.covers1624.curl4j;

import fi.iki.elonen.NanoHTTPD.Response;
import net.covers1624.curl4j.core.Callback;
import net.covers1624.curl4j.tests.TestBase;
import net.covers1624.curl4j.tests.TestWebServer;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static net.covers1624.curl4j.CURL.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by covers1624 on 1/10/23.
 */
public class CallbackExceptionTests extends TestBase {

    @Test
    public void testCallbackBubble() throws Throwable {
        byte[] data = randomBytes(32);

        curl_global_init(CURL_GLOBAL_DEFAULT);
        long curl = curl_easy_init();

        try (TestWebServer server = new TestWebServer()) {
            server.addHandler("/", r -> bytesResponse(Response.Status.OK, data));

            curl_easy_reset(curl);

            curl_easy_setopt(curl, CURLOPT_URL, server.addr("/"));
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "GET");

            try (CurlWriteCallback output = new CurlWriteCallback((ptr, size, nmemb, userdata) -> {
                throw new RuntimeException("Thrown inside callback!");
            })) {
                output.setExceptionHandler(Callback.CallbackExceptionHandler.RETHROW);
                curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, output);

                RuntimeException ex = assertThrows(RuntimeException.class, () -> curl_easy_perform(curl));
                assertEquals("Thrown inside callback!", ex.getMessage());
            }
        }

    }

    @Test
    public void testCallbackCustomPolicy() throws Throwable {
        byte[] data = randomBytes(32);

        curl_global_init(CURL_GLOBAL_DEFAULT);
        long curl = curl_easy_init();

        try (TestWebServer server = new TestWebServer()) {
            server.addHandler("/", r -> bytesResponse(Response.Status.OK, data));

            curl_easy_reset(curl);

            curl_easy_setopt(curl, CURLOPT_URL, server.addr("/"));
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "GET");

            try (CurlWriteCallback output = new CurlWriteCallback((ptr, size, nmemb, userdata) -> {
                throw new RuntimeException("Thrown inside callback!");
            })) {
                List<Throwable> excs = new LinkedList<>();
                output.setExceptionHandler(excs::add);
                curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, output);

                int result = curl_easy_perform(curl);
                assertEquals(CURLE_WRITE_ERROR, result);
                assertEquals(1, excs.size());
                assertEquals(RuntimeException.class, excs.get(0).getClass());
                assertEquals("Thrown inside callback!", excs.get(0).getMessage());
            }
        }

    }
}
