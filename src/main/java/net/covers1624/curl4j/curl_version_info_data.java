package net.covers1624.curl4j;

import org.lwjgl.system.Struct;

import java.nio.ByteBuffer;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by covers1624 on 3/15/22.
 */
public class curl_version_info_data extends Struct {

    public static final int SIZEOF;
    public static final int ALIGNOF;

    public static final int AGE;
    public static final int VERSION;
    public static final int VERSION_NUM;
    public static final int HOST;
    public static final int FEATURES;
    public static final int SSL_VERSION;
    public static final int SSL_VERSION_NUM;
    public static final int LIBZ_VERSION;
    public static final int PROTOCOLS;
    public static final int ARES;
    public static final int ARES_NUM;
    public static final int LIBIDN;
    public static final int ICONV_VER_NUM;
    public static final int LIBSSH_VERSION;
    public static final int BROTLI_VER_NUM;
    public static final int BROTLI_VERSION;
    public static final int NGHTTP2_VER_NUM;
    public static final int NGHTTP2_VERSION;
    public static final int QUIC_VERSION;
    public static final int CAINFO;
    public static final int CAPATH;
    public static final int ZSTD_VER_NUM;
    public static final int ZSTD_VERSION;
    public static final int HYPER_VERSION;
    public static final int GSASL_VERSION;
    public static final int FEATURE_NAMES;

    static {
        Layout layout = __struct(
                __member(4),        // age
                __member(POINTER_SIZE),  // version
                __member(4),        // version_num
                __member(POINTER_SIZE),  // host
                __member(4),        // features
                __member(POINTER_SIZE),  // ssl_version
                __member(CLONG_SIZE),    // ssl_version_num
                __member(POINTER_SIZE),  // zlib_version
                __member(POINTER_SIZE),  // protocols
                __member(POINTER_SIZE),  // ares
                __member(4),        // ares_num
                __member(POINTER_SIZE),  // libidn
                __member(4),        // iconv_ver_num
                __member(POINTER_SIZE),  // libssh_version
                __member(4),        // brotli_ver_num
                __member(POINTER_SIZE),  // brotli_version
                __member(4),        // nghttp2_ver_num
                __member(POINTER_SIZE),  // nghttp2_version
                __member(POINTER_SIZE),  // quic_version
                __member(POINTER_SIZE),  // cainfo
                __member(POINTER_SIZE),  // capath
                __member(4),        // zstd_ver_num
                __member(POINTER_SIZE),  // zstd_version
                __member(POINTER_SIZE),  // hyper_version
                __member(POINTER_SIZE),  // gsasl_version
                __member(POINTER_SIZE)   // feature_names
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        AGE = layout.offsetof(0);
        VERSION = layout.offsetof(1);
        VERSION_NUM = layout.offsetof(2);
        HOST = layout.offsetof(3);
        FEATURES = layout.offsetof(4);
        SSL_VERSION = layout.offsetof(5);
        SSL_VERSION_NUM = layout.offsetof(6);
        LIBZ_VERSION = layout.offsetof(7);
        PROTOCOLS = layout.offsetof(8);
        ARES = layout.offsetof(9);
        ARES_NUM = layout.offsetof(10);
        LIBIDN = layout.offsetof(11);
        ICONV_VER_NUM = layout.offsetof(12);
        LIBSSH_VERSION = layout.offsetof(13);
        BROTLI_VER_NUM = layout.offsetof(14);
        BROTLI_VERSION = layout.offsetof(15);
        NGHTTP2_VER_NUM = layout.offsetof(16);
        NGHTTP2_VERSION = layout.offsetof(17);
        QUIC_VERSION = layout.offsetof(18);
        CAINFO = layout.offsetof(19);
        CAPATH = layout.offsetof(20);
        ZSTD_VER_NUM = layout.offsetof(21);
        ZSTD_VERSION = layout.offsetof(22);
        HYPER_VERSION = layout.offsetof(23);
        GSASL_VERSION = layout.offsetof(24);
        FEATURE_NAMES = layout.offsetof(25);
    }

    public curl_version_info_data(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public static curl_version_info_data create(long address) {
        return wrap(curl_version_info_data.class, address);
    }

    public static curl_version_info_data createSafe(long address) {
        return address == NULL ? null : wrap(curl_version_info_data.class, address);
    }

    public int age() {
        return memGetInt(address() + AGE);
    }

    public String version() {
        return memUTF8(memGetAddress(address() + VERSION));
    }

    public int version_num() {
        return memGetInt(address() + VERSION_NUM);
    }

    public String host() {
        return memUTF8(memGetAddress(address() + HOST));
    }

    public int features() {
        return memGetInt(address() + FEATURES);
    }

    public String ssl_version() {
        return memUTF8(memGetAddress(address() + SSL_VERSION));
    }

    public long ssl_version_num() {
        return memGetCLong(address() + SSL_VERSION_NUM);
    }

    public String libz_version() {
        return memUTF8(memGetAddress(address() + LIBZ_VERSION));
    }

    public Set<String> protocols() {
        Set<String> protocols = new LinkedHashSet<>();
        long ptr = memGetAddress(address() + PROTOCOLS);
        while (memGetByte(ptr) != '\0') {
            protocols.add(memUTF8Safe(memGetAddress(ptr)));
            ptr += POINTER_SIZE;
        }

        return protocols;
    }

    public String ares() {
        return memUTF8(memGetAddress(address() + ARES));
    }

    public int ares_num() {
        return memGetInt(address() + ARES_NUM);
    }

    public String libidn() {
        return memUTF8(memGetAddress(address() + LIBIDN));
    }

    public int iconv_ver_num() {
        return memGetInt(address() + ICONV_VER_NUM);
    }

    public String libssh_version() {
        return memUTF8(memGetAddress(address() + LIBSSH_VERSION));
    }

    public int brotli_ver_num() {
        return memGetInt(address() + BROTLI_VER_NUM);
    }

    public String brotli_version() {
        return memUTF8(memGetAddress(address() + BROTLI_VERSION));
    }

    public int nghttp2_ver_num() {
        return memGetInt(address() + NGHTTP2_VER_NUM);
    }

    public String nghttp2_version() {
        return memUTF8(memGetAddress(address() + NGHTTP2_VERSION));
    }

    public String quic_version() {
        return memUTF8(memGetAddress(address() + QUIC_VERSION));
    }

    public String cainfo() {
        return memUTF8(memGetAddress(address() + CAINFO));
    }

    public String capath() {
        return memUTF8(memGetAddress(address() + CAPATH));
    }

    public int zstd_ver_num() {
        return memGetInt(address() + ZSTD_VER_NUM);
    }

    public String zstd_version() {
        return memUTF8(memGetAddress(address() + ZSTD_VERSION));
    }

    public String hyper_version() {
        return memUTF8(memGetAddress(address() + HYPER_VERSION));
    }

    public String gsasl_version() {
        return memUTF8(memGetAddress(address() + GSASL_VERSION));
    }

    public Set<String> feature_names() {
        Set<String> protocols = new LinkedHashSet<>();
        long ptr = memGetAddress(address() + FEATURE_NAMES);
        while (memGetByte(ptr) != '\0') {
            protocols.add(memUTF8Safe(memGetAddress(ptr)));
            ptr += POINTER_SIZE;
        }

        return protocols;
    }
}
