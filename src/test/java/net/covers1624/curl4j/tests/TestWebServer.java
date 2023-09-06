package net.covers1624.curl4j.tests;

import fi.iki.elonen.NanoHTTPD;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by covers1624 on 4/9/23.
 */
public class TestWebServer extends NanoHTTPD implements AutoCloseable {

    private final int port;
    private final Map<String, Handler> funcMap = new HashMap<>();

    public TestWebServer() throws IOException {
        this(getRandomEphemeralPort());
    }

    private TestWebServer(int port) throws IOException {
        super("127.0.0.1", port);
        this.port = port;
        start();
    }

    public void addHandler(String path, Handler func) {
        funcMap.put(path, func);
    }

    public String addr(String path) {
        return "http://127.0.0.1:" + port + path;
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            return funcMap.get(session.getUri()).handle(session);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        stop();
    }

    private static int getRandomEphemeralPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to get random port.", ex);
        }
    }

    // Thanks NanoHTTPD, your _fantastic_ at this. /s
    public static byte[] getBody(NanoHTTPD.IHTTPSession session) throws IOException {
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

    public interface Handler {

        Response handle(IHTTPSession session) throws IOException;
    }
}
