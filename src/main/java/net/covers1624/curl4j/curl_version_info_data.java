package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Pointer;
import net.covers1624.curl4j.core.Struct;

import java.util.Set;

/**
 * @author covers1624
 */
public class curl_version_info_data extends Pointer {

    private static final Struct STRUCT = new Struct("curl_version_info_data");

    public static final Struct.Member<Integer> AGE = STRUCT.intMember("age");
    public static final Struct.Member<String> VERSION = STRUCT.stringMember("version");
    public static final Struct.Member<Integer> VERSION_NUM = STRUCT.intMember("version_num");
    public static final Struct.Member<String> HOST = STRUCT.stringMember("host");
    public static final Struct.Member<Integer> FEATURES = STRUCT.intMember("features");
    public static final Struct.Member<String> SSL_VERSION = STRUCT.stringMember("ssl_version");
    public static final Struct.Member<Long> SSL_VERSION_NUM = STRUCT.longMember("ssl_version_num");
    public static final Struct.Member<String> LIBZ_VERSION = STRUCT.stringMember("libz_version");
    public static final Struct.Member<Set<String>> PROTOCOLS = STRUCT.stringListMember("protocols");
    public static final Struct.Member<String> ARES = STRUCT.stringMember("ares");
    public static final Struct.Member<Integer> ARES_NUM = STRUCT.intMember("ares_num");
    public static final Struct.Member<String> LIBIDN = STRUCT.stringMember("libidn");
    public static final Struct.Member<Integer> ICONV_VER_NUM = STRUCT.intMember("iconv_ver_num");
    public static final Struct.Member<String> LIBSSH_VERSION = STRUCT.stringMember("libssh_version");
    public static final Struct.Member<Integer> BROTLI_VER_NUM = STRUCT.intMember("brotli_ver_num");
    public static final Struct.Member<String> BROTLI_VERSION = STRUCT.stringMember("brotli_version");
    public static final Struct.Member<Integer> NGHTTP2_VER_NUM = STRUCT.intMember("nghttp2_ver_num");
    public static final Struct.Member<String> NGHTTP2_VERSION = STRUCT.stringMember("nghttp2_version");
    public static final Struct.Member<String> QUIC_VERSION = STRUCT.stringMember("quic_version");
    public static final Struct.Member<String> CAINFO = STRUCT.stringMember("cainfo");
    public static final Struct.Member<String> CAPATH = STRUCT.stringMember("capath");
    public static final Struct.Member<Integer> ZSTD_VER_NUM = STRUCT.intMember("zstd_ver_num");
    public static final Struct.Member<String> ZSTD_VERSION = STRUCT.stringMember("zstd_version");
    public static final Struct.Member<String> HYPER_VERSION = STRUCT.stringMember("hyper_version");
    public static final Struct.Member<String> GSASL_VERSION = STRUCT.stringMember("gsasl_version");
    public static final Struct.Member<Set<String>> FEATURE_NAMES = STRUCT.stringListMember("feature_names");

    public curl_version_info_data(long address) {
        super(address);
    }

    // @formatter:off
    public int getAge() { return AGE.read(this); }
    public String getVersion() { return VERSION.read(this); }
    public int getVersion_num() { return VERSION_NUM.read(this); }
    public String getHost() { return HOST.read(this); }
    public int getFeatures() { return FEATURES.read(this); }
    public String getSsl_version() { return SSL_VERSION.read(this); }
    public long getSsl_version_num() { return SSL_VERSION_NUM.read(this); }
    public String getLibz_version() { return LIBZ_VERSION.read(this); }
    public Set<String> getProtocols() { return PROTOCOLS.read(this); }
    public String getAres() { return ARES.read(this); }
    public int getAres_num() { return ARES_NUM.read(this); }
    public String getLibidn() { return LIBIDN.read(this); }
    public int getIconv_ver_num() { return ICONV_VER_NUM.read(this); }
    public String getLibssh_version() { return LIBSSH_VERSION.read(this); }
    public int getBrotli_ver_num() { return BROTLI_VER_NUM.read(this); }
    public String getBrotli_version() { return BROTLI_VERSION.read(this); }
    public int getNghttp2_ver_num() { return NGHTTP2_VER_NUM.read(this); }
    public String getNghttp2_version() { return NGHTTP2_VERSION.read(this); }
    public String getQuic_version() { return QUIC_VERSION.read(this); }
    public String getCainfo() { return CAINFO.read(this); }
    public String getCapath() { return CAPATH.read(this); }
    public int getZstd_ver_num() { return ZSTD_VER_NUM.read(this); }
    public String getZstd_version() { return ZSTD_VERSION.read(this); }
    public String getHyper_version() { return HYPER_VERSION.read(this); }
    public String getGsasl_version() { return GSASL_VERSION.read(this); }
    public Set<String> getFeature_names() { return FEATURE_NAMES.read(this); }
    // @formatter:on
}
