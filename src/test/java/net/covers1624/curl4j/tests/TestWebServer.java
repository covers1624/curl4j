package net.covers1624.curl4j.tests;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
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

    public interface Handler {

        Response handle(IHTTPSession session) throws IOException;
    }
}
