package net.covers1624.curl4j;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.invoke.VarHandle;
import java.util.Set;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;
import static net.covers1624.curl4j.util.ForeignUtils.readNTString;
import static net.covers1624.curl4j.util.ForeignUtils.readNTStringArray;

/**
 * @author covers1624
 */
public record curl_version_info_data(MemorySegment address) {

    public static final StructLayout CURL_VERSION_INFO_DATA = LibCurl.STRUCT_PARSER.parseStruct("""
            struct curl_version_info_data {
                CURLversion age;          /* age of the returned struct */
                const char *version;      /* LIBCURL_VERSION */
                unsigned int version_num; /* LIBCURL_VERSION_NUM */
                const char *host;         /* OS/host/cpu/machine when configured */
                int features;             /* bitmask, see defines below */
                const char *ssl_version;  /* human readable string */
                long ssl_version_num;     /* not used anymore, always 0 */
                const char *libz_version; /* human readable string */
                /* protocols is terminated by an entry with a NULL protoname */
                const char * const *protocols;
            
                /* The fields below this were added in CURLVERSION_SECOND */
                const char *ares;
                int ares_num;
            
                /* This field was added in CURLVERSION_THIRD */
                const char *libidn;
            
                /* These field were added in CURLVERSION_FOURTH */
            
                /* Same as '_libiconv_version' if built with HAVE_ICONV */
                int iconv_ver_num;
            
                const char *libssh_version; /* human readable string */
            
                /* These fields were added in CURLVERSION_FIFTH */
                unsigned int brotli_ver_num; /* Numeric Brotli version (MAJOR << 24) | (MINOR << 12) | PATCH */
                const char *brotli_version; /* human readable string. */
            
                /* These fields were added in CURLVERSION_SIXTH */
                unsigned int nghttp2_ver_num; /* Numeric nghttp2 version (MAJOR << 16) | (MINOR << 8) | PATCH */
                const char *nghttp2_version; /* human readable string. */
                const char *quic_version;    /* human readable quic (+ HTTP/3) library + version or NULL */
            
                /* These fields were added in CURLVERSION_SEVENTH */
                const char *cainfo;          /* the built-in default CURLOPT_CAINFO, might be NULL */
                const char *capath;          /* the built-in default CURLOPT_CAPATH, might be NULL */
            
                /* These fields were added in CURLVERSION_EIGHTH */
                unsigned int zstd_ver_num; /* Numeric Zstd version (MAJOR << 24) | (MINOR << 12) | PATCH */
                const char *zstd_version; /* human readable string. */
            
                /* These fields were added in CURLVERSION_NINTH */
                const char *hyper_version; /* human readable string. */
            
                /* These fields were added in CURLVERSION_TENTH */
                const char *gsasl_version; /* human readable string. */
            
                /* These fields were added in CURLVERSION_ELEVENTH */
                /* feature_names is terminated by an entry with a NULL feature name */
                const char * const *feature_names;
            
                /* These fields were added in CURLVERSION_TWELFTH */
                const char *rtmp_version; /* human readable string. */
            };
            """);

    public static final VarHandle AGE = CURL_VERSION_INFO_DATA.varHandle(groupElement("age"));
    public static final VarHandle VERSION = CURL_VERSION_INFO_DATA.varHandle(groupElement("version"));
    public static final VarHandle VERSION_NUM = CURL_VERSION_INFO_DATA.varHandle(groupElement("version_num"));
    public static final VarHandle HOST = CURL_VERSION_INFO_DATA.varHandle(groupElement("host"));
    public static final VarHandle FEATURES = CURL_VERSION_INFO_DATA.varHandle(groupElement("features"));
    public static final VarHandle SSL_VERSION = CURL_VERSION_INFO_DATA.varHandle(groupElement("ssl_version"));
    public static final VarHandle SSL_VERSION_NUM = CURL_VERSION_INFO_DATA.varHandle(groupElement("ssl_version_num"));
    public static final VarHandle LIBZ_VERSION = CURL_VERSION_INFO_DATA.varHandle(groupElement("libz_version"));
    public static final VarHandle PROTOCOLS = CURL_VERSION_INFO_DATA.varHandle(groupElement("protocols"));
    public static final VarHandle ARES = CURL_VERSION_INFO_DATA.varHandle(groupElement("ares"));
    public static final VarHandle ARES_NUM = CURL_VERSION_INFO_DATA.varHandle(groupElement("ares_num"));
    public static final VarHandle LIBIDN = CURL_VERSION_INFO_DATA.varHandle(groupElement("libidn"));
    public static final VarHandle ICONV_VER_NUM = CURL_VERSION_INFO_DATA.varHandle(groupElement("iconv_ver_num"));
    public static final VarHandle LIBSSH_VERSION = CURL_VERSION_INFO_DATA.varHandle(groupElement("libssh_version"));
    public static final VarHandle BROTLI_VER_NUM = CURL_VERSION_INFO_DATA.varHandle(groupElement("brotli_ver_num"));
    public static final VarHandle BROTLI_VERSION = CURL_VERSION_INFO_DATA.varHandle(groupElement("brotli_version"));
    public static final VarHandle NGHTTP2_VER_NUM = CURL_VERSION_INFO_DATA.varHandle(groupElement("nghttp2_ver_num"));
    public static final VarHandle NGHTTP2_VERSION = CURL_VERSION_INFO_DATA.varHandle(groupElement("nghttp2_version"));
    public static final VarHandle QUIC_VERSION = CURL_VERSION_INFO_DATA.varHandle(groupElement("quic_version"));
    public static final VarHandle CAINFO = CURL_VERSION_INFO_DATA.varHandle(groupElement("cainfo"));
    public static final VarHandle CAPATH = CURL_VERSION_INFO_DATA.varHandle(groupElement("capath"));
    public static final VarHandle ZSTD_VER_NUM = CURL_VERSION_INFO_DATA.varHandle(groupElement("zstd_ver_num"));
    public static final VarHandle ZSTD_VERSION = CURL_VERSION_INFO_DATA.varHandle(groupElement("zstd_version"));
    public static final VarHandle HYPER_VERSION = CURL_VERSION_INFO_DATA.varHandle(groupElement("hyper_version"));
    public static final VarHandle GSASL_VERSION = CURL_VERSION_INFO_DATA.varHandle(groupElement("gsasl_version"));
    public static final VarHandle FEATURE_NAMES = CURL_VERSION_INFO_DATA.varHandle(groupElement("feature_names"));

    public curl_version_info_data {
        address = address.reinterpret(CURL_VERSION_INFO_DATA.byteSize());
    }

    // @formatter:off
    public int getAge() { return (int) AGE.get(address, 0); }
    public String getVersion() { return readNTString((MemorySegment) VERSION.get(address, 0)); }
    public int getVersion_num() { return (int) VERSION_NUM.get(address, 0); }
    public String getHost() { return readNTString((MemorySegment) HOST.get(address, 0)); }
    public int getFeatures() { return (int) FEATURES.get(address, 0); }
    public String getSsl_version() { return readNTString((MemorySegment) SSL_VERSION.get(address, 0)); }
    public long getSsl_version_num() { return (long) SSL_VERSION_NUM.get(address, 0); }
    public String getLibz_version() { return readNTString((MemorySegment) LIBZ_VERSION.get(address, 0)); }
    public Set<String> getProtocols() { return readNTStringArray((MemorySegment) PROTOCOLS.get(address, 0)); }
    public String getAres() { return readNTString((MemorySegment) ARES.get(address, 0)); }
    public int getAres_num() { return (int) ARES_NUM.get(address, 0); }
    public String getLibidn() { return readNTString((MemorySegment) LIBIDN.get(address, 0)); }
    public int getIconv_ver_num() { return (int) ICONV_VER_NUM.get(address, 0); }
    public String getLibssh_version() { return readNTString((MemorySegment) LIBSSH_VERSION.get(address, 0)); }
    public int getBrotli_ver_num() { return (int) BROTLI_VER_NUM.get(address, 0); }
    public String getBrotli_version() { return readNTString((MemorySegment) BROTLI_VERSION.get(address, 0)); }
    public int getNghttp2_ver_num() { return (int) NGHTTP2_VER_NUM.get(address, 0); }
    public String getNghttp2_version() { return readNTString((MemorySegment) NGHTTP2_VERSION.get(address, 0)); }
    public String getQuic_version() { return readNTString((MemorySegment) QUIC_VERSION.get(address, 0)); }
    public String getCainfo() { return readNTString((MemorySegment) CAINFO.get(address, 0)); }
    public String getCapath() { return readNTString((MemorySegment) CAPATH.get(address, 0)); }
    public int getZstd_ver_num() { return (int) ZSTD_VER_NUM.get(address, 0); }
    public String getZstd_version() { return readNTString((MemorySegment) ZSTD_VERSION.get(address, 0)); }
    public String getHyper_version() { return readNTString((MemorySegment) HYPER_VERSION.get(address, 0)); }
    public String getGsasl_version() { return readNTString((MemorySegment) GSASL_VERSION.get(address, 0)); }
    public Set<String> getFeature_names() { return readNTStringArray((MemorySegment) FEATURE_NAMES.get(address, 0)); }
    // @formatter:on
}
