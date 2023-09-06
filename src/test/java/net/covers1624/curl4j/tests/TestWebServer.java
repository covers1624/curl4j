package net.covers1624.curl4j.tests;

import fi.iki.elonen.NanoHTTPD;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.KeyManagerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.*;

/**
 * Created by covers1624 on 4/9/23.
 */
public class TestWebServer extends NanoHTTPD implements AutoCloseable {

    private final int port;
    private final KeyStore keyStore;
    private final Map<String, Handler> funcMap = new HashMap<>();

    public TestWebServer() throws IOException {
        this(getRandomEphemeralPort(), null, null);
    }

    public TestWebServer(String keystore, @Nullable String password) throws IOException {
        this(getRandomEphemeralPort(), keystore, password);
    }

    private TestWebServer(int port, @Nullable String keystore, @Nullable String password) throws IOException {
        super("127.0.0.1", port);
        this.port = port;
        if (keystore != null) {
            keyStore = loadKeyStore(keystore, password);
            makeSecure(makeSSLSocketFactory(keyStore, loadKeyManager(keyStore, password)), null);
        } else {
            keyStore = null;
        }
        start();
    }

    public void addHandler(String path, Handler func) {
        funcMap.put(path, func);
    }

    public byte[] getCertBytes(String alias) throws IOException {
        if (keyStore == null) throw new IllegalStateException("SSL not configured.");
        try {
            return toPemCertificate(keyStore.getCertificate(alias).getEncoded());
        } catch (Throwable ex) {
            throw new IOException(ex);
        }
    }

    public String addr(String path) {
        String proto = keyStore != null ? "https" : "http";
        return proto + "://127.0.0.1:" + port + path;
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

    private static KeyStore loadKeyStore(String resource, String password) throws IOException {
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(TestWebServer.class.getResourceAsStream(resource), password != null ? password.toCharArray() : new char[0]);
            return ks;
        } catch (Throwable ex) {
            throw new IOException(ex);
        }
    }

    private static KeyManagerFactory loadKeyManager(KeyStore keyStore, String password) throws IOException {
        try {
            KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            factory.init(keyStore, password.toCharArray());
            return factory;
        } catch (Throwable ex) {
            throw new IOException(ex);
        }
    }

    public static byte[] toPemCertificate(byte[] keyData) {
        List<String> lines = new LinkedList<>();
        lines.add("-----BEGIN CERTIFICATE-----");
        String str = Base64.getEncoder().encodeToString(keyData);
        // lines of max length 64, matches openssl output format.
        for (int i = 0; i < str.length(); i += 64) {
            lines.add(str.substring(i, Math.min(i + 64, str.length())));
        }
        lines.add("-----END CERTIFICATE-----");
        return String.join("\n", lines).getBytes(StandardCharsets.UTF_8);
    }

    public interface Handler {

        Response handle(IHTTPSession session) throws IOException;
    }
}
