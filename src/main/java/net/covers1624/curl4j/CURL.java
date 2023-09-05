package net.covers1624.curl4j;

import net.covers1624.curl4j.core.*;
import org.jetbrains.annotations.Nullable;

import static net.covers1624.curl4j.CURL.Functions.*;

/**
 * Native bindings to the <a href="https://curl.se/libcurl">libcurl</a> library.
 *
 * <p>libcurl is a free and easy-to-use client-side URL transfer library, supporting
 * DICT, FILE, FTP, FTPS, GOPHER, GOPHERS, HTTP, HTTPS, IMAP, IMAPS, LDAP, LDAPS,
 * MQTT, POP3, POP3S, RTMP, RTMPS, RTSP, SCP, SFTP, SMB, SMBS, SMTP, SMTPS, TELNET and TFTP.
 * libcurl supports SSL certificates, HTTP POST, HTTP PUT, FTP uploading, HTTP form based upload,
 * proxies, HTTP/2, HTTP/3, cookies, user+password authentication (Basic, Digest, NTLM, Negotiate, Kerberos),
 * file transfer resume, http proxy tunneling and more!</p>
 * <p>
 * This library is structured very much like other LWJGL native bindings and uses a very similar pattern.
 * <p>
 * This class does not contain any high-level abstractions around cURL, these are designed to be raw low-level bindings
 * similar, if not identical, to cURL.
 *
 * @author covers1624
 */
@SuppressWarnings ("unused") // Hey! This is library.
public class CURL {

    private static @Nullable String LIB_CURL_OVERRIDE;
    private static @Nullable Library CURL;

    private CURL() { }

    /**
     * Call this before using any cURL functions to override the curl library name if 'libcurl' is
     * not valid for your platform/use case.
     * <p>
     * This may be an absolute path to a libcurl library if required.
     *
     * @param name The name.
     */
    public static void setLibCurlName(String name) {
        if (CURL != null) {
            throw new IllegalStateException("CURL already initialized.");
        }
        LIB_CURL_OVERRIDE = name;
    }

    /**
     * Get the {@link Library} handle for libCURL.
     * <p>
     * You can use this to lookup functions that are not exposed here if required.
     * <p>
     * This function will initialize curl, making it impossible to override the library name.
     *
     * @return The {@link Library} for curl.
     */
    public static Library getLbCURL() {
        if (CURL == null) {
            String lib = System.getProperty("net.covers1624.curl4j.libcurl.name", "curl");
            if (LIB_CURL_OVERRIDE != null) {
                lib = LIB_CURL_OVERRIDE;
            }
            CURL = LibraryLoader.loadLibrary(lib);
        }
        return CURL;
    }

    // region curl_global_init constants
    /**
     * <a href="https://curl.se/libcurl/c/curl_global_init.html">curl_global_init</a> constants.
     */
    @SuppressWarnings ("PointlessBitwiseExpression")
    public static final long CURL_GLOBAL_SSL = 1 << 0;
    public static final long CURL_GLOBAL_WIN32 = 1 << 1;
    public static final long CURL_GLOBAL_ALL = CURL_GLOBAL_SSL | CURL_GLOBAL_WIN32;
    public static final long CURL_GLOBAL_NOTHING = 0;
    public static final long CURL_GLOBAL_DEFAULT = CURL_GLOBAL_ALL;
    public static final long CURL_GLOBAL_ACK_EINTR = 1 << 2;
    // endregion

    //region CURLoption
    private static final int CURLOPTTYPE_LONG = 0;
    private static final int CURLOPTTYPE_OBJECTPOINT = 10000;
    private static final int CURLOPTTYPE_FUNCTIONPOINT = 20000;
    private static final int CURLOPTTYPE_OFF_T = 30000;
    private static final int CURLOPTTYPE_BLOB = 40000;

    /**
     * 'char *' argument to a string with trailing zero
     */
    private static final int CURLOPTTYPE_STRINGPOINT = CURLOPTTYPE_OBJECTPOINT;
    /**
     * 'struct curl_slist *' argument
     */
    private static final int CURLOPTTYPE_SLISTPOINT = CURLOPTTYPE_OBJECTPOINT;
    /**
     * 'void *' argument passed untouched to callback
     */
    private static final int CURLOPTTYPE_CBPOINT = CURLOPTTYPE_OBJECTPOINT;
    /**
     * 'long' argument with a set of values/bitmask
     */
    private static final int CURLOPTTYPE_VALUES = CURLOPTTYPE_LONG;

    /**
     * This is the FILE * or void * the regular output should be written to.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_WRITEDATA.html">documentation</a>.
     */
    public static final int CURLOPT_WRITEDATA = CURLOPTTYPE_CBPOINT + 1;
    /**
     * The full URL to get/put.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_URL.html">documentation</a>.
     */
    public static final int CURLOPT_URL = CURLOPTTYPE_STRINGPOINT + 2;
    /**
     * Port number to connect to, if other than default.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PORT.html">documentation</a>.
     */
    public static final int CURLOPT_PORT = CURLOPTTYPE_LONG + 3;
    /**
     * Name of proxy to use.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY = CURLOPTTYPE_STRINGPOINT + 4;
    /**
     * "user:password;options" to use when fetching.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_USERPWD.html">documentation</a>.
     */
    public static final int CURLOPT_USERPWD = CURLOPTTYPE_STRINGPOINT + 5;
    /**
     * "user:password" to use with proxy.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXYUSERPWD.html">documentation</a>.
     */
    public static final int CURLOPT_PROXYUSERPWD = CURLOPTTYPE_STRINGPOINT + 6;
    /**
     * // Range to get, specified as an ASCII string
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RANGE.html">documentation</a>.
     */
    public static final int CURLOPT_RANGE = CURLOPTTYPE_STRINGPOINT + 7;

    /**
     * Specified file stream to upload from (use as input):
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_READDATA.html">documentation</a>.
     */
    public static final int CURLOPT_READDATA = CURLOPTTYPE_CBPOINT + 9;

    /**
     * Buffer to receive error messages in, must be at least CURL_ERROR_SIZE bytes big.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_ERRORBUFFER.html">documentation</a>.
     */
    public static final int CURLOPT_ERRORBUFFER = CURLOPTTYPE_OBJECTPOINT + 10;

    /**
     * Function that will be called to store the output (instead of fwrite). The
     * parameters will use fwrite() syntax, make sure to follow them.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_WRITEFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 11;
    /**
     * Function that will be called to read the input (instead of fread). The
     * parameters will use fread() syntax, make sure to follow them.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_READFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 12;

    /**
     * Time-out the read operation after this amount of seconds
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TIMEOUT.html">documentation</a>.
     */
    public static final int CURLOPT_TIMEOUT = CURLOPTTYPE_LONG + 13;

    /**
     * If {@link #CURLOPT_READDATA} is used, this can be used to inform libcurl about
     * how large the file being sent really is. That allows better error
     * checking and better verifies that the upload was successful. -1 means
     * unknown size.
     * <p>
     * For large file support, there is also a _LARGE version of the key
     * which takes an off_t type, allowing platforms with larger off_t
     * sizes to handle larger files.  See below for {@link #CURLOPT_INFILESIZE_LARGE}.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_INFILESIZE.html">documentation</a>.
     */
    public static final int CURLOPT_INFILESIZE = CURLOPTTYPE_LONG + 14;

    /**
     * POST static input fields.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_POSTFIELDS.html">documentation</a>.
     */
    public static final int CURLOPT_POSTFIELDS = CURLOPTTYPE_OBJECTPOINT + 15;

    /**
     * Set the referrer page (needed by some CGIs)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_REFERER.html">documentation</a>.
     */
    public static final int CURLOPT_REFERER = CURLOPTTYPE_STRINGPOINT + 16;

    /**
     * Set the FTP PORT string (interface name, named or numerical IP address)
     * Use i.e '-' to use default address.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FTPPORT.html">documentation</a>.
     */
    public static final int CURLOPT_FTPPORT = CURLOPTTYPE_STRINGPOINT + 17;

    /**
     * Set the User-Agent string (examined by some CGIs)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_USERAGENT.html">documentation</a>.
     */
    public static final int CURLOPT_USERAGENT = CURLOPTTYPE_STRINGPOINT + 18;

    /*
     * If the download receives less than "low speed limit" bytes/second
     * during "low speed time" seconds, the operations is aborted.
     * You could i.e if you have a pretty high speed connection, abort if
     * it is less than 2000 bytes/sec during 20 seconds.
     */

    /**
     * Set the "low speed limit"
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_LOW_SPEED_LIMIT.html">documentation</a>.
     */
    public static final int CURLOPT_LOW_SPEED_LIMIT = CURLOPTTYPE_LONG + 19;

    /**
     * Set the "low speed time"
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_LOW_SPEED_TIME.html">documentation</a>.
     */
    public static final int CURLOPT_LOW_SPEED_TIME = CURLOPTTYPE_LONG + 19;

    /**
     * Set the continuation offset.
     * <p>
     * Note there is also a _LARGE version of this key which use
     * off_t types, allowing for large file offsets on platforms which
     * use larger-than-32-bit off_t's. Look bellow for {@link #CURLOPT_RESUME_FROM_LARGE}
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RESUME_FROM.html">documentation</a>.
     */
    public static final int CURLOPT_RESUME_FROM = CURLOPTTYPE_LONG + 21;

    /**
     * Set a cookie in request:
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_COOKIE.html">documentation</a>.
     */
    public static final int CURLOPT_COOKIE = CURLOPTTYPE_STRINGPOINT + 22;

    /**
     * This points to a linked list of headers, struct curl_slist kind. This
     * list is also used for RTSP (in spite of its name)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HTTPHEADER.html">documentation</a>.
     */
    public static final int CURLOPT_HTTPHEADER = CURLOPTTYPE_SLISTPOINT + 23;

    /**
     * This points to a linked list of post entries, struct curl_httppost
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HTTPPOST.html">documentation</a>.
     */
    @Deprecated // Use CURLOPT_MIMEPOST
    public static final int CURLOPT_HTTPPOST = CURLOPTTYPE_OBJECTPOINT + 24;

    /**
     * name of the file keeping your private SSL-certificate
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSLCERT.html">documentation</a>.
     */
    public static final int CURLOPT_SSLCERT = CURLOPTTYPE_STRINGPOINT + 25;

    /**
     * password for the SSL or SSH private key.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_KEYPASSWD.html">documentation</a>.
     */
    public static final int CURLOPT_KEYPASSWD = CURLOPTTYPE_STRINGPOINT + 26;

    /**
     * send TYPE parameter?
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CRLF.html">documentation</a>.
     */
    public static final int CURLOPT_CRLF = CURLOPTTYPE_LONG + 27;

    /**
     * send linked-list of QUOTE commands
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_QUOTE.html">documentation</a>.
     */
    public static final int CURLOPT_QUOTE = CURLOPTTYPE_SLISTPOINT + 28;

    /**
     * send FILE * or void * to store headers to, if you use a callback it
     * is simply passed to the callback unmodified
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HEADERDATA.html">documentation</a>.
     */
    public static final int CURLOPT_HEADERDATA = CURLOPTTYPE_CBPOINT + 29;

    /**
     * point to a file to read the initial cookies from, also enables
     * "cookie awareness"
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_COOKIEFILE.html">documentation</a>.
     */
    public static final int CURLOPT_COOKIEFILE = CURLOPTTYPE_STRINGPOINT + 31;

    /**
     * What version to specifically ry to use.
     * See CURL_SSLVERSION defines below.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSLVERSION.html">documentation</a>.
     */
    public static final int CURLOPT_SSLVERSION = CURLOPTTYPE_VALUES + 32;

    /**
     * What kind of HTTP time condition to use, see defines
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TIMECONDITION.html">documentation</a>.
     */
    public static final int CURLOPT_TIMECONDITION = CURLOPTTYPE_VALUES + 33;

    /**
     * Time to use with the above conditions. Specified in number of seconds
     * since 1 Jan 1970.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TIMEVALUE.html">documentation</a>.
     */
    public static final int CURLOPT_TIMEVALUE = CURLOPTTYPE_LONG + 34;

    /**
     * Custom request, for customizing the get command like
     * HTTP: DELETE, TRACE and others
     * FTP: to use a different list command
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CUSTOMREQUEST.html">documentation</a>.
     */
    public static final int CURLOPT_CUSTOMREQUEST = CURLOPTTYPE_STRINGPOINT + 36;

    /**
     * send linked-list of post-transfer QUOTE commands
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_POSTQUOTE.html">documentation</a>.
     */
    public static final int CURLOPT_POSTQUOTE = CURLOPTTYPE_SLISTPOINT + 39;

    /**
     * talk a lot
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_VERBOSE.html">documentation</a>.
     */
    public static final int CURLOPT_VERBOSE = CURLOPTTYPE_LONG + 41;

    /**
     * throw the header out too
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HEADER.html">documentation</a>.
     */
    public static final int CURLOPT_HEADER = CURLOPTTYPE_LONG + 42;

    /**
     * shut off the progress meter
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_NOPROGRESS.html">documentation</a>.
     */
    public static final int CURLOPT_NOPROGRESS = CURLOPTTYPE_LONG + 43;

    /**
     * use HEAD to get http document
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_NOBODY.html">documentation</a>.
     */
    public static final int CURLOPT_NOBODY = CURLOPTTYPE_LONG + 44;

    /**
     * no output on http error codes >= 400
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FAILONERROR.html">documentation</a>.
     */
    public static final int CURLOPT_FAILONERROR = CURLOPTTYPE_LONG + 45;

    /**
     * this is an upload
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_UPLOAD.html">documentation</a>.
     */
    public static final int CURLOPT_UPLOAD = CURLOPTTYPE_LONG + 46;

    /**
     * HTTP POST method
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_POST.html">documentation</a>.
     */
    public static final int CURLOPT_POST = CURLOPTTYPE_LONG + 47;

    /**
     * bare names when listing directories
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DIRLISTONLY.html">documentation</a>.
     */
    public static final int CURLOPT_DIRLISTONLY = CURLOPTTYPE_LONG + 48;

    /**
     * Append instead of overwrite on upload!
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_APPEND.html">documentation</a>.
     */
    public static final int CURLOPT_APPEND = CURLOPTTYPE_LONG + 50;

    /**
     * Specify weather to read the user+password from the .netrc or the URL.
     * This must be one of the CURL_NETRC_* enums below.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_NETRC.html">documentation</a>.
     */
    public static final int CURLOPT_NETRC = CURLOPTTYPE_VALUES + 51;

    /**
     * use Location: Luke!
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FOLLOWLOCATION.html">documentation</a>.
     */
    public static final int CURLOPT_FOLLOWLOCATION = CURLOPTTYPE_LONG + 52;

    /**
     * transfer data in text/ASCII format
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TRANSFERTEXT.html">documentation</a>.
     */
    public static final int CURLOPT_TRANSFERTEXT = CURLOPTTYPE_LONG + 53;

    /**
     * HTTP PUT
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PUT.html">documentation</a>.
     */
    @Deprecated // Use CURLOPT_UPLOAD
    public static final int CURLOPT_PUT = CURLOPTTYPE_LONG + 54;

    /**
     * Function that will be called instead of the internal progress display
     * function. this function should be defined as the curl_progress_callback
     * prototype defines.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROGRESSFUNCTION.html">documentation</a>.
     */
    @Deprecated // Use CURLOPT_XFERINFOFUNCTION
    public static final int CURLOPT_PROGRESSFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 56;

    /**
     * Data passed to the {@link #CURLOPT_PROGRESSFUNCTION} and {@link #CURLOPT_XFERINFOFUNCTION}
     * callbacks
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_XFERINFODATA.html">documentation</a>.
     */
    public static final int CURLOPT_XFERINFODATA = CURLOPTTYPE_CBPOINT + 57;
    public static final int CURLOPT_PROGRESSDATA = CURLOPT_XFERINFODATA;

    /**
     * We want the referrer field set automatically when following locations
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_AUTOREFERER.html">documentation</a>.
     */
    public static final int CURLOPT_AUTOREFERER = CURLOPTTYPE_LONG + 58;

    /**
     * Port of the proxy, can be set in the proxy string as well with:
     * "[host]:[port]"
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXYPORT.html">documentation</a>.
     */
    public static final int CURLOPT_PROXYPORT = CURLOPTTYPE_LONG + 59;

    /**
     * size of the POST input data, if strlen() is not good to use
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_POSTFIELDSIZE.html">documentation</a>.
     */
    public static final int CURLOPT_POSTFIELDSIZE = CURLOPTTYPE_LONG + 60;

    /**
     * tunnel non-http operations through an HTTP proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HTTPPROXYTUNNEL.html">documentation</a>.
     */
    public static final int CURLOPT_HTTPPROXYTUNNEL = CURLOPTTYPE_LONG + 61;

    /**
     * Set the interface string to use as outgoing network interface
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_INTERFACE.html">documentation</a>.
     */
    public static final int CURLOPT_INTERFACE = CURLOPTTYPE_STRINGPOINT + 62;

    /**
     * Set the krb4/5 security level, this also enables krb4/5 awareness.  This
     * is a string, 'clear', 'safe', 'confidential' or 'private'.  If the string
     * is set but doesn't match one of these, 'private' will be used.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_KRBLEVEL.html">documentation</a>.
     */
    public static final int CURLOPT_KRBLEVEL = CURLOPTTYPE_STRINGPOINT + 63;

    /**
     * Set if we should verify the peer in ssl handshake, set 1 to verify.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSL_VERIFYPEER.html">documentation</a>.
     */
    public static final int CURLOPT_SSL_VERIFYPEER = CURLOPTTYPE_LONG + 64;

    /**
     * The CApath or CAfile used to validate the peer certificate
     * this option is used only if SSL_VERIFYPEER is true
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CAINFO.html">documentation</a>.
     */
    public static final int CURLOPT_CAINFO = CURLOPTTYPE_STRINGPOINT + 65;

    /**
     * Maximum number of http redirects to follow
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_MAXREDIRS.html">documentation</a>.
     */
    public static final int CURLOPT_MAXREDIRS = CURLOPTTYPE_LONG + 68;

    /**
     * Pass a long set to 1 to get the date of the requested document (if
     * possible)! Pass a zero to shut it off.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FILETIME.html">documentation</a>.
     */
    public static final int CURLOPT_FILETIME = CURLOPTTYPE_LONG + 69;

    /**
     * This points to a linked list of telnet options
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TELNETOPTIONS.html">documentation</a>.
     */
    public static final int CURLOPT_TELNETOPTIONS = CURLOPTTYPE_SLISTPOINT + 70;

    /**
     * Max amount of cached alive connections
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_MAXCONNECTS.html">documentation</a>.
     */
    public static final int CURLOPT_MAXCONNECTS = CURLOPTTYPE_LONG + 71;

    /**
     * Set to explicitly use a new connection for the upcoming transfer.
     * Do not use this unless you're absolutely sure of this, as it makes the
     * operation slower and is less friendly for the network.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FRESH_CONNECT.html">documentation</a>.
     */
    public static final int CURLOPT_FRESH_CONNECT = CURLOPTTYPE_LONG + 74;

    /**
     * Set to explicitly forbid the upcoming transfer's connection to be re-used
     * when done. Do not use this unless you're absolutely sure of this, as it
     * makes the operation slower and is less friendly for the network.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FORBID_REUSE.html">documentation</a>.
     */
    public static final int CURLOPT_FORBID_REUSE = CURLOPTTYPE_LONG + 75;

    /**
     * Set to a file name that contains random data for libcurl to use to
     * seed the random engine when doing SSL connects.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RANDOM_FILE.html">documentation</a>.
     */
    @Deprecated // Serves no purpose anymore
    public static final int CURLOPT_RANDOM_FILE = CURLOPTTYPE_STRINGPOINT + 76;

    /**
     * Set to the Entropy Gathering Daemon socket pathname
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_EGDSOCKET.html">documentation</a>.
     */
    @Deprecated // Serves no purpose anymore
    public static final int CURLOPT_EGDSOCKET = CURLOPTTYPE_STRINGPOINT + 77;

    /**
     * Time-out connect operations after this amount of seconds, if connects are
     * OK within this time, then fine... This only aborts the connect phase.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CONNECTTIMEOUT.html">documentation</a>.
     */
    public static final int CURLOPT_CONNECTTIMEOUT = CURLOPTTYPE_LONG + 78;

    /**
     * Function that will be called to store headers (instead of fwrite). The
     * parameters will use fwrite() syntax, make sure to follow them.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HEADERFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_HEADERFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 79;

    /**
     * Set this to force the HTTP request to get back to GET. Only really usable
     * if POST, PUT or a custom request have been used first.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HTTPGET.html">documentation</a>.
     */
    public static final int CURLOPT_HTTPGET = CURLOPTTYPE_LONG + 80;

    /**
     * Set if we should verify the Common name from the peer certificate in ssl
     * handshake, set 1 to check existence, 2 to ensure that it matches the
     * provided hostname.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSL_VERIFYHOST.html">documentation</a>.
     */
    public static final int CURLOPT_SSL_VERIFYHOST = CURLOPTTYPE_LONG + 81;

    /**
     * Specify which file name to write all known cookies in after completed
     * operation. Set file name to "-" (dash) to make it go to stdout.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_COOKIEJAR.html">documentation</a>.
     */
    public static final int CURLOPT_COOKIEJAR = CURLOPTTYPE_STRINGPOINT + 82;

    /**
     * Specify which SSL ciphers to use
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSL_CIPHER_LIST.html">documentation</a>.
     */
    public static final int CURLOPT_SSL_CIPHER_LIST = CURLOPTTYPE_STRINGPOINT + 83;

    /**
     * Specify which HTTP version to use! This must be set to one of the
     * CURL_HTTP_VERSION* enums set below.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HTTP_VERSION.html">documentation</a>.
     */
    public static final int CURLOPT_HTTP_VERSION = CURLOPTTYPE_VALUES + 84;

    /**
     * Specifically switch on or off the FTP engine's use of the EPSV command. By
     * default, that one will always be attempted before the more traditional
     * PASV command.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FTP_USE_EPSV.html">documentation</a>.
     */
    public static final int CURLOPT_FTP_USE_EPSV = CURLOPTTYPE_LONG + 85;

    /**
     * type of the file keeping your SSL-certificate ("DER", "PEM", "ENG")
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSLCERTTYPE.html">documentation</a>.
     */
    public static final int CURLOPT_SSLCERTTYPE = CURLOPTTYPE_STRINGPOINT + 86;

    /**
     * name of the file keeping your private SSL-key
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSLKEY.html">documentation</a>.
     */
    public static final int CURLOPT_SSLKEY = CURLOPTTYPE_STRINGPOINT + 87;

    /**
     * type of the file keeping your private SSL-key ("DER", "PEM", "ENG")
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSLKEYTYPE.html">documentation</a>.
     */
    public static final int CURLOPT_SSLKEYTYPE = CURLOPTTYPE_STRINGPOINT + 88;

    /**
     * crypto engine for the SSL-sub system
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSLENGINE.html">documentation</a>.
     */
    public static final int CURLOPT_SSLENGINE = CURLOPTTYPE_STRINGPOINT + 89;

    /**
     * set the crypto engine for the SSL-sub system as default
     * the param has no meaning...
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSLENGINE_DEFAULT.html">documentation</a>.
     */
    public static final int CURLOPT_SSLENGINE_DEFAULT = CURLOPTTYPE_LONG + 90;

    /**
     * Non-zero value means to use the global dns cache
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DNS_USE_GLOBAL_CACHE.html">documentation</a>.
     */
    @Deprecated // Use CURLOPT_SHARE
    public static final int CURLOPT_DNS_USE_GLOBAL_CACHE = CURLOPTTYPE_LONG + 91;

    /**
     * DNS cache timeout
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DNS_CACHE_TIMEOUT.html">documentation</a>.
     */
    public static final int CURLOPT_DNS_CACHE_TIMEOUT = CURLOPTTYPE_LONG + 92;

    /**
     * send linked-list of pre-transfer QUOTE commands
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PREQUOTE.html">documentation</a>.
     */
    public static final int CURLOPT_PREQUOTE = CURLOPTTYPE_SLISTPOINT + 93;

    /**
     * set the debug function
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DEBUGFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_DEBUGFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 94;

    /**
     * set the data for the debug function
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DEBUGDATA.html">documentation</a>.
     */
    public static final int CURLOPT_DEBUGDATA = CURLOPTTYPE_CBPOINT + 95;

    /**
     * mark this as start of a cookie session
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_COOKIESESSION.html">documentation</a>.
     */
    public static final int CURLOPT_COOKIESESSION = CURLOPTTYPE_LONG + 96;

    /**
     * The CApath directory used to validate the peer certificate
     * this option is used only if SSL_VERIFYPEER is true
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CAPATH.html">documentation</a>.
     */
    public static final int CURLOPT_CAPATH = CURLOPTTYPE_STRINGPOINT + 97;

    /**
     * Instruct libcurl to use a smaller receive buffer
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_BUFFERSIZE.html">documentation</a>.
     */
    public static final int CURLOPT_BUFFERSIZE = CURLOPTTYPE_LONG + 98;

    /**
     * Instruct libcurl to not use any signal/alarm handlers, even when using
     * timeouts. This option is useful for multi-threaded applications.
     * See libcurl-the-guide for more background information.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_NOSIGNAL.html">documentation</a>.
     */
    public static final int CURLOPT_NOSIGNAL = CURLOPTTYPE_LONG + 99;

    /**
     * Provide a CURLShare for mutexing non-ts data
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SHARE.html">documentation</a>.
     */
    public static final int CURLOPT_SHARE = CURLOPTTYPE_OBJECTPOINT + 100;

    /**
     * indicates type of proxy. accepted values are {@link #CURLPROXY_HTTP} (default),
     * {@link CURLPROXY_HTTPS}, {@link CURLPROXY_SOCKS4}, {@link CURLPROXY_SOCKS4A} and
     * {@link CURLPROXY_SOCKS5}.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXYTYPE.html">documentation</a>.
     */
    public static final int CURLOPT_PROXYTYPE = CURLOPTTYPE_VALUES + 101;

    /**
     * Set the Accept-Encoding string. Use this to tell a server you would like
     * the response to be compressed. Before 7.21.6, this was known as
     * CURLOPT_ENCODING
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_ACCEPT_ENCODING.html">documentation</a>.
     */
    public static final int CURLOPT_ACCEPT_ENCODING = CURLOPTTYPE_STRINGPOINT + 102;

    /**
     * Set pointer to private data
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PRIVATE.html">documentation</a>.
     */
    public static final int CURLOPT_PRIVATE = CURLOPTTYPE_OBJECTPOINT + 103;

    /**
     * Set aliases for HTTP 200 in the HTTP Response header
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HTTP200ALIASES.html">documentation</a>.
     */
    public static final int CURLOPT_HTTP200ALIASES = CURLOPTTYPE_SLISTPOINT + 104;

    /**
     * Continue to send authentication (user+password) when following locations,
     * even when hostname changed. This can potentially send off the name
     * and password to whatever host the server decides.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_UNRESTRICTED_AUTH.html">documentation</a>.
     */
    public static final int CURLOPT_UNRESTRICTED_AUTH = CURLOPTTYPE_LONG + 105;

    /**
     * Specifically switch on or off the FTP engine's use of the EPRT command (
     * it also disables the LPRT attempt). By default, those ones will always be
     * attempted before the good old traditional PORT command.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FTP_USE_EPRT.html">documentation</a>.
     */
    public static final int CURLOPT_FTP_USE_EPRT = CURLOPTTYPE_LONG + 106;

    /**
     * Set this to a bitmask value to enable the particular authentications
     * methods you like. Use this in combination with {@link #CURLOPT_USERPWD}.
     * Note that setting multiple bits may cause extra network round-trips.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HTTPAUTH.html">documentation</a>.
     */
    public static final int CURLOPT_HTTPAUTH = CURLOPTTYPE_VALUES + 107;

    /**
     * Set the ssl context callback function, currently only for OpenSSL or
     * WolfSSL ssl_ctx, or mbedTLS mbedtls_ssl_config in the second argument.
     * The function must match the curl_ssl_ctx_callback prototype.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSL_CTX_FUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_SSL_CTX_FUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 108;

    /**
     * Set the userdata for the ssl context callback function's third
     * argument
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSL_CTX_DATA.html">documentation</a>.
     */
    public static final int CURLOPT_SSL_CTX_DATA = CURLOPTTYPE_CBPOINT + 109;

    /**
     * FTP Option that causes missing dirs to be created on the remote server.
     * In 7.19.4 we introduced the convenience enums for this option using the
     * CURLFTP_CREATE_DIR prefix.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FTP_CREATE_MISSING_DIRS.html">documentation</a>.
     */
    public static final int CURLOPT_FTP_CREATE_MISSING_DIRS = CURLOPTTYPE_LONG + 110;

    /**
     * Set this to a bitmask value to enable the particular authentications
     * methods you like. Use this in combination with {@link #CURLOPT_PROXYUSERPWD}.
     * Note that setting multiple bits may cause extra network round-trips.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXYAUTH.html">documentation</a>.
     */
    public static final int CURLOPT_PROXYAUTH = CURLOPTTYPE_VALUES + 111;

    /**
     * Option that changes the timeout, in seconds, associated with getting a
     * response.  This is different from transfer timeout time and essentially
     * places a demand on the server to acknowledge commands in a timely
     * manner. For FTP, SMTP, IMAP and POP3.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SERVER_RESPONSE_TIMEOUT.html">documentation</a>.
     */
    public static final int CURLOPT_SERVER_RESPONSE_TIMEOUT = CURLOPTTYPE_LONG + 112;

    /**
     * Set this option to one of the CURL_IPRESOLVE_* defines (see below) to
     * tell libcurl to use those IP versions only. This only has effect on
     * systems with support for more than one, i.e IPv4 _and_ IPv6.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_IPRESOLVE.html">documentation</a>.
     */
    public static final int CURLOPT_IPRESOLVE = CURLOPTTYPE_VALUES + 113;

    /**
     * Set this option to limit the size of a file that will be downloaded from
     * an HTTP or FTP server.
     * <p>
     * Note there is also _LARGE version which adds large file support for
     * platforms which have larger off_t sizes.  See {@link #CURLOPT_MAXFILESIZE_LARGE} below.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_MAXFILESIZE.html">documentation</a>.
     */
    public static final int CURLOPT_MAXFILESIZE = CURLOPTTYPE_LONG + 114;

    /**
     * See the comment for {@link #CURLOPT_INFILESIZE} above, but in short, specifies
     * the size of the file being uploaded.  -1 means unknown.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_INFILESIZE_LARGE.html">documentation</a>.
     */
    public static final int CURLOPT_INFILESIZE_LARGE = CURLOPTTYPE_OFF_T + 115;

    /**
     * Sets the continuation offset.  There is also a CURLOPTTYPE_LONG version
     * of this; look above for {@link #CURLOPT_RESUME_FROM}.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RESUME_FROM_LARGE.html">documentation</a>.
     */
    public static final int CURLOPT_RESUME_FROM_LARGE = CURLOPTTYPE_OFF_T + 116;

    /**
     * Sets the maximum size of data that will be downloaded from
     * an HTTP or FTP server.  See {@link #CURLOPT_MAXFILESIZE} above for the LONG version.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_MAXFILESIZE_LARGE.html">documentation</a>.
     */
    public static final int CURLOPT_MAXFILESIZE_LARGE = CURLOPTTYPE_OFF_T + 117;

    /**
     * Set this option to the file name of your .netrc file you want libcurl
     * to parse (using the {@link #CURLOPT_NETRC} option). If not set, libcurl will do
     * a poor attempt to find the user's home directory and check for a .netrc
     * file in there.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_NETRC_FILE.html">documentation</a>.
     */
    public static final int CURLOPT_NETRC_FILE = CURLOPTTYPE_STRINGPOINT + 118;

    /**
     * Enable SSL/TLS for FTP, pick one of:
     * {@link #CURLUSESSL_TRY}     - try using SSL, proceed anyway otherwise
     * {@link #CURLUSESSL_CONTROL} - SSL for the control connection or fail
     * {@link #CURLUSESSL_ALL}     - SSL for all communication or fail
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_USE_SSL.html">documentation</a>.
     */
    public static final int CURLOPT_USE_SSL = CURLOPTTYPE_VALUES + 119;

    /**
     * The _LARGE version of the standard {@link #CURLOPT_POSTFIELDSIZE} option
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_POSTFIELDSIZE_LARGE.html">documentation</a>.
     */
    public static final int CURLOPT_POSTFIELDSIZE_LARGE = CURLOPTTYPE_OFF_T + 120;

    /**
     * Enable/disable the TCP Nagle algorithm
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TCP_NODELAY.html">documentation</a>.
     */
    public static final int CURLOPT_TCP_NODELAY = CURLOPTTYPE_LONG + 121;

    /**
     * When FTP over SSL/TLS is selected (with {@link #CURLOPT_USE_SSL}), this option
     * can be used to change libcurl's default action which is to first try
     * "AUTH SSL" and then "AUTH TLS" in this order, and proceed when a OK
     * response has been received.
     * <p>
     * Available parameters are:
     * {@link #CURLFTPAUTH_DEFAULT} - let libcurl decide
     * {@link #CURLFTPAUTH_SSL}     - try "AUTH SSL" first, then TLS
     * {@link #CURLFTPAUTH_TLS}     - try "AUTH TLS" first, then SSL
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FTPSSLAUTH.html">documentation</a>.
     */
    public static final int CURLOPT_FTPSSLAUTH = CURLOPTTYPE_VALUES + 129;

    @Deprecated // Use CURLOPT_SEEKFUNCTION
    public static final int CURLOPT_IOCTLFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 130;

    @Deprecated // Use CURLOPT_SEEKDATA
    public static final int CURLOPT_IOCTLDATA = CURLOPTTYPE_CBPOINT + 131;

    /**
     * null-terminated string for pass on to the FTP server when asked for
     * "account" info
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FTP_ACCOUNT.html">documentation</a>.
     */
    public static final int CURLOPT_FTP_ACCOUNT = CURLOPTTYPE_STRINGPOINT + 134;

    /**
     * feed cookie into cookie engine
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_COOKIELIST.html">documentation</a>.
     */
    public static final int CURLOPT_COOKIELIST = CURLOPTTYPE_STRINGPOINT + 135;

    /**
     * ignore Content-Length
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_IGNORE_CONTENT_LENGTH.html">documentation</a>.
     */
    public static final int CURLOPT_IGNORE_CONTENT_LENGTH = CURLOPTTYPE_LONG + 136;

    /**
     * Set to non-zero to skip the IP address received in a 227 PASV FTP server
     * response. Typically used for FTP-SSL purposes but is not restricted to
     * that. libcurl will then instead use the same IP address it used for the
     * control connection.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FTP_SKIP_PASV_IP.html">documentation</a>.
     */
    public static final int CURLOPT_FTP_SKIP_PASV_IP = CURLOPTTYPE_LONG + 137;

    /**
     * Select "file method" to use when doing FTP, see the curl_ftpmethod
     * above.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FTP_FILEMETHOD.html">documentation</a>.
     */
    public static final int CURLOPT_FTP_FILEMETHOD = CURLOPTTYPE_VALUES + 138;

    /**
     * Local port number to bind the socket to
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_LOCALPORT.html">documentation</a>.
     */
    public static final int CURLOPT_LOCALPORT = CURLOPTTYPE_LONG + 139;

    /**
     * Number of ports to try, including the first one set with {@link #CURLOPT_LOCALPORT}.
     * Thus, setting it to 1 will make no additional attempts but the first.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_LOCALPORTRANGE.html">documentation</a>.
     */
    public static final int CURLOPT_LOCALPORTRANGE = CURLOPTTYPE_LONG + 140;

    /**
     * no transfer, set up connection and let application use the socket by
     * extracting it with CURLINFO_LASTSOCKET
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CONNECT_ONLY.html">documentation</a>.
     */
    public static final int CURLOPT_CONNECT_ONLY = CURLOPTTYPE_LONG + 141;

    /**
     * Function that will be called to convert from the
     * network encoding (instead of using the iconv calls in libcurl)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CONV_FROM_NETWORK_FUNCTION.html">documentation</a>.
     */
    @Deprecated // Serves no purpose anymore
    public static final int CURLOPT_CONV_FROM_NETWORK_FUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 242;

    /**
     * Function that will be called to convert to the
     * network encoding (instead of using the iconv calls in libcurl)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CONV_TO_NETWORK_FUNCTION.html">documentation</a>.
     */
    @Deprecated // Serves no purpose anymore
    public static final int CURLOPT_CONV_TO_NETWORK_FUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 243;

    /**
     * Function that will be called to convert from UTF8
     * (instead of using the iconv calls in libcurl)
     * Note that this is used only for SSL certificate processing
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CONV_FROM_UTF8_FUNCTION.html">documentation</a>.
     */
    @Deprecated // Serves no purpose anymore
    public static final int CURLOPT_CONV_FROM_UTF8_FUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 244;

    /**
     * if the connection proceeds too quickly then need to slow it down
     * limit-rate: maximum number of bytes per second to send or receive
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_MAX_SEND_SPEED_LARGE.html">documentation</a>.
     */
    public static final int CURLOPT_MAX_SEND_SPEED_LARGE = CURLOPTTYPE_OFF_T + 145;
    public static final int CURLOPT_MAX_RECV_SPEED_LARGE = CURLOPTTYPE_OFF_T + 146;

    /**
     * Pointer to command string to send if USER/PASS fails.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FTP_ALTERNATIVE_TO_USER.html">documentation</a>.
     */
    public static final int CURLOPT_FTP_ALTERNATIVE_TO_USER = CURLOPTTYPE_STRINGPOINT + 147;

    /**
     * callback function for setting socket options
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SOCKOPTFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_SOCKOPTFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 148;
    public static final int CURLOPT_SOCKOPTDATA = CURLOPTTYPE_CBPOINT + 149;

    /**
     * set to 0 to disable session ID re-use for this transfer, default is
     * enabled (== 1)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSL_SESSIONID_CACHE.html">documentation</a>.
     */
    public static final int CURLOPT_SSL_SESSIONID_CACHE = CURLOPTTYPE_LONG + 150;

    /**
     * allowed SSH authentication methods
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSH_AUTH_TYPES.html">documentation</a>.
     */
    public static final int CURLOPT_SSH_AUTH_TYPES = CURLOPTTYPE_VALUES + 151;

    /**
     * Used by scp/sftp to do public/private key authentication
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSH_PUBLIC_KEYFILE.html">documentation</a>.
     */
    public static final int CURLOPT_SSH_PUBLIC_KEYFILE = CURLOPTTYPE_STRINGPOINT + 152;
    public static final int CURLOPT_SSH_PRIVATE_KEYFILE = CURLOPTTYPE_STRINGPOINT + 153;

    /**
     * Send CCC (Clear Command Channel) after authentication
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FTP_SSL_CCC.html">documentation</a>.
     */
    public static final int CURLOPT_FTP_SSL_CCC = CURLOPTTYPE_LONG + 154;

    /**
     * Same as {@link #CURLOPT_TIMEOUT} and {@link #CURLOPT_CONNECTTIMEOUT}, but with ms resolution
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TIMEOUT_MS.html">documentation</a>.
     */
    public static final int CURLOPT_TIMEOUT_MS = CURLOPTTYPE_LONG + 155;
    public static final int CURLOPT_CONNECTTIMEOUT_MS = CURLOPTTYPE_LONG + 156;

    /**
     * set to zero to disable the libcurl's decoding and thus pass the raw body
     * data to the application even when it is encoded/compressed
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HTTP_TRANSFER_DECODING.html">documentation</a>.
     */
    public static final int CURLOPT_HTTP_TRANSFER_DECODING = CURLOPTTYPE_LONG + 157;
    public static final int CURLOPT_HTTP_CONTENT_DECODING = CURLOPTTYPE_LONG + 158;

    /**
     * Permission used when creating new files and directories on the remote
     * server for protocols that support it, SFTP/SCP/FILE
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_NEW_FILE_PERMS.html">documentation</a>.
     */
    public static final int CURLOPT_NEW_FILE_PERMS = CURLOPTTYPE_LONG + 159;
    public static final int CURLOPT_NEW_DIRECTORY_PERMS = CURLOPTTYPE_LONG + 160;

    /**
     * Set the behavior of POST when redirecting. Values must be set to one
     * of CURL_REDIR* defines below. This used to be called CURLOPT_POST301
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_POSTREDIR.html">documentation</a>.
     */
    public static final int CURLOPT_POSTREDIR = CURLOPTTYPE_VALUES + 161;

    /**
     * used by scp/sftp to verify the host's public key
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSH_HOST_PUBLIC_KEY_MD5.html">documentation</a>.
     */
    public static final int CURLOPT_SSH_HOST_PUBLIC_KEY_MD5 = CURLOPTTYPE_STRINGPOINT + 162;

    /**
     * Callback function for opening socket (instead of socket(2)). Optionally,
     * callback is able change the address or refuse to connect returning
     * CURL_SOCKET_BAD.  The callback should have type
     * curl_opensocket_callback
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_OPENSOCKETFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_OPENSOCKETFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 163;
    public static final int CURLOPT_OPENSOCKETDATA = CURLOPTTYPE_CBPOINT + 164;

    /**
     * POST volatile input fields.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_COPYPOSTFIELDS.html">documentation</a>.
     */
    public static final int CURLOPT_COPYPOSTFIELDS = CURLOPTTYPE_OBJECTPOINT + 165;

    /**
     * set transfer mode (;type=<a|i>) when doing FTP via an HTTP proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_TRANSFER_MODE.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_TRANSFER_MODE = CURLOPTTYPE_LONG + 166;

    /**
     * Callback function for seeking in the input stream
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SEEKFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_SEEKFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 167;
    public static final int CURLOPT_SEEKDATA = CURLOPTTYPE_CBPOINT + 168;

    /**
     * CRL file
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CRLFILE.html">documentation</a>.
     */
    public static final int CURLOPT_CRLFILE = CURLOPTTYPE_STRINGPOINT + 169;

    /**
     * Issuer certificate
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_ISSUERCERT.html">documentation</a>.
     */
    public static final int CURLOPT_ISSUERCERT = CURLOPTTYPE_STRINGPOINT + 170;

    /**
     * (IPv6) Address scope
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_ADDRESS_SCOPE.html">documentation</a>.
     */
    public static final int CURLOPT_ADDRESS_SCOPE = CURLOPTTYPE_LONG + 171;

    /**
     * Collect certificate chain info and allow it to get retrievable with
     * CURLINFO_CERTINFO after the transfer is complete.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CERTINFO.html">documentation</a>.
     */
    public static final int CURLOPT_CERTINFO = CURLOPTTYPE_LONG + 172;

    /**
     * "name" and "pwd" to use when fetching.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_USERNAME.html">documentation</a>.
     */
    public static final int CURLOPT_USERNAME = CURLOPTTYPE_STRINGPOINT + 173;
    public static final int CURLOPT_PASSWORD = CURLOPTTYPE_STRINGPOINT + 174;

    /**
     * "name" and "pwd" to use with Proxy when fetching.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXYUSERNAME.html">documentation</a>.
     */
    public static final int CURLOPT_PROXYUSERNAME = CURLOPTTYPE_STRINGPOINT + 175;
    public static final int CURLOPT_PROXYPASSWORD = CURLOPTTYPE_STRINGPOINT + 176;

    /**
     * Comma separated list of hostnames defining no-proxy zones. These should
     * match both hostnames directly, and hostnames within a domain. For
     * example, local.com will match local.com and www.local.com, but NOT
     * notlocal.com or www.notlocal.com. For compatibility with other
     * implementations of this, .local.com will be considered to be the same as
     * local.com. A single * is the only valid wildcard, and effectively
     * disables the use of proxy.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_NOPROXY.html">documentation</a>.
     */
    public static final int CURLOPT_NOPROXY = CURLOPTTYPE_STRINGPOINT + 177;

    /**
     * block size for TFTP transfers
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TFTP_BLKSIZE.html">documentation</a>.
     */
    public static final int CURLOPT_TFTP_BLKSIZE = CURLOPTTYPE_LONG + 178;

    /**
     * Socks Service
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SOCKS5_GSSAPI_SERVICE.html">documentation</a>.
     */
    @Deprecated // Use CURLOPT_PROXY_SERVICE_NAME
    public static final int CURLOPT_SOCKS5_GSSAPI_SERVICE = CURLOPTTYPE_LONG + 179;

    /**
     * Socks Service
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SOCKS5_GSSAPI_NEC.html">documentation</a>.
     */
    public static final int CURLOPT_SOCKS5_GSSAPI_NEC = CURLOPTTYPE_LONG + 180;

    /**
     * set the bitmask for the protocols that are allowed to be used for the
     * transfer, which thus helps the app which takes URLs from users or other
     * external inputs and want to restrict what protocol(s) to deal
     * with. Defaults to CURLPROTO_ALL.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROTOCOLS.html">documentation</a>.
     */
    @Deprecated // Use CURLOPT_PROTOCOLS_STR
    public static final int CURLOPT_PROTOCOLS = CURLOPTTYPE_LONG + 181;

    /**
     * set the bitmask for the protocols that libcurl is allowed to follow to,
     * as a subset of the CURLOPT_PROTOCOLS ones. That means the protocol needs
     * to be set in both bitmasks to be allowed to get redirected to.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_REDIR_PROTOCOLS.html">documentation</a>.
     */
    @Deprecated // Use CURLOPT_REDIR_PROTOCOLS_STR
    public static final int CURLOPT_REDIR_PROTOCOLS = CURLOPTTYPE_LONG + 182;

    /**
     * set the SSH knownhost file name to use
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSH_KNOWNHOSTS.html">documentation</a>.
     */
    public static final int CURLOPT_SSH_KNOWNHOSTS = CURLOPTTYPE_STRINGPOINT + 183;

    /**
     * set the SSH host key callback, must point to a curl_sshkeycallback
     * function
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSH_KEYFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_SSH_KEYFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 184;

    /**
     * set the SSH host key callback custom pointer
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSH_KEYDATA.html">documentation</a>.
     */
    public static final int CURLOPT_SSH_KEYDATA = CURLOPTTYPE_CBPOINT + 185;

    /**
     * set the SMTP mail originator
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_MAIL_FROM.html">documentation</a>.
     */
    public static final int CURLOPT_MAIL_FROM = CURLOPTTYPE_STRINGPOINT + 186;

    /**
     * set the list of SMTP mail receiver(s)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_MAIL_RCPT.html">documentation</a>.
     */
    public static final int CURLOPT_MAIL_RCPT = CURLOPTTYPE_SLISTPOINT + 187;

    /**
     * FTP: send PRET before PASV
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FTP_USE_PRET.html">documentation</a>.
     */
    public static final int CURLOPT_FTP_USE_PRET = CURLOPTTYPE_LONG + 188;

    /**
     * RTSP request method (OPTIONS, SETUP, PLAY, etc...)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RTSP_REQUEST.html">documentation</a>.
     */
    public static final int CURLOPT_RTSP_REQUEST = CURLOPTTYPE_VALUES + 189;

    /**
     * The RTSP session identifier
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RTSP_SESSION_ID.html">documentation</a>.
     */
    public static final int CURLOPT_RTSP_SESSION_ID = CURLOPTTYPE_STRINGPOINT + 190;

    /**
     * The RTSP stream URI
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RTSP_STREAM_URI.html">documentation</a>.
     */
    public static final int CURLOPT_RTSP_STREAM_URI = CURLOPTTYPE_STRINGPOINT + 191;

    /**
     * The Transport: header to use in RTSP requests
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RTSP_TRANSPORT.html">documentation</a>.
     */
    public static final int CURLOPT_RTSP_TRANSPORT = CURLOPTTYPE_STRINGPOINT + 192;

    /**
     * Manually initialize the client RTSP CSeq for this handle
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RTSP_CLIENT_CSEQ.html">documentation</a>.
     */
    public static final int CURLOPT_RTSP_CLIENT_CSEQ = CURLOPTTYPE_LONG + 193;

    /**
     * Manually initialize the server RTSP CSeq for this handle
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RTSP_SERVER_CSEQ.html">documentation</a>.
     */
    public static final int CURLOPT_RTSP_SERVER_CSEQ = CURLOPTTYPE_LONG + 194;

    /**
     * The stream to pass to INTERLEAVEFUNCTION.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_INTERLEAVEDATA.html">documentation</a>.
     */
    public static final int CURLOPT_INTERLEAVEDATA = CURLOPTTYPE_CBPOINT + 195;

    /**
     * Let the application define a custom write method for RTP data
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_INTERLEAVEFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_INTERLEAVEFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 196;

    /**
     * Turn on wildcard matching
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_WILDCARDMATCH.html">documentation</a>.
     */
    public static final int CURLOPT_WILDCARDMATCH = CURLOPTTYPE_LONG + 197;

    /**
     * Directory matching callback called before downloading of an
     * individual file (chunk) started
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CHUNK_BGN_FUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_CHUNK_BGN_FUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 198;

    /**
     * Directory matching callback called after the file (chunk)
     * was downloaded, or skipped
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CHUNK_END_FUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_CHUNK_END_FUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 199;

    /**
     * Change match (fnmatch-like) callback for wildcard matching
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FNMATCH_FUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_FNMATCH_FUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 200;

    /**
     * Let the application define custom chunk data pointer
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CHUNK_DATA.html">documentation</a>.
     */
    public static final int CURLOPT_CHUNK_DATA = CURLOPTTYPE_CBPOINT + 201;

    /**
     * FNMATCH_FUNCTION user pointer
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_FNMATCH_DATA.html">documentation</a>.
     */
    public static final int CURLOPT_FNMATCH_DATA = CURLOPTTYPE_CBPOINT + 202;

    /**
     * send linked-list of name:port:address sets
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RESOLVE.html">documentation</a>.
     */
    public static final int CURLOPT_RESOLVE = CURLOPTTYPE_SLISTPOINT + 203;

    /**
     * Set a username for authenticated TLS
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TLSAUTH_USERNAME.html">documentation</a>.
     */
    public static final int CURLOPT_TLSAUTH_USERNAME = CURLOPTTYPE_STRINGPOINT + 204;

    /**
     * Set a password for authenticated TLS
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TLSAUTH_PASSWORD.html">documentation</a>.
     */
    public static final int CURLOPT_TLSAUTH_PASSWORD = CURLOPTTYPE_STRINGPOINT + 205;

    /**
     * Set authentication type for authenticated TLS
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TLSAUTH_TYPE.html">documentation</a>.
     */
    public static final int CURLOPT_TLSAUTH_TYPE = CURLOPTTYPE_STRINGPOINT + 206;

    /**
     * Set to 1 to enable the "TE:" header in HTTP requests to ask for
     * compressed transfer-encoded responses. Set to 0 to disable the use of TE:
     * in outgoing requests. The current default is 0, but it might change in a
     * future libcurl release.
     * <p>
     * libcurl will ask for the compressed methods it knows of, and if that
     * isn't any, it will not ask for transfer-encoding at all even if this
     * option is set to 1.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TRANSFER_ENCODING.html">documentation</a>.
     */
    public static final int CURLOPT_TRANSFER_ENCODING = CURLOPTTYPE_LONG + 207;

    /**
     * Callback function for closing socket (instead of close(2)). The callback
     * should have type curl_closesocket_callback
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CLOSESOCKETFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_CLOSESOCKETFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 208;
    public static final int CURLOPT_CLOSESOCKETDATA = CURLOPTTYPE_CBPOINT + 209;

    /**
     * allow GSSAPI credential delegation
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_GSSAPI_DELEGATION.html">documentation</a>.
     */
    public static final int CURLOPT_GSSAPI_DELEGATION = CURLOPTTYPE_VALUES + 210;

    /**
     * Set the name servers to use for DNS resolution
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DNS_SERVERS.html">documentation</a>.
     */
    public static final int CURLOPT_DNS_SERVERS = CURLOPTTYPE_STRINGPOINT + 211;

    /**
     * Time-out accept operations (currently for FTP only) after this amount
     * of milliseconds.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_ACCEPTTIMEOUT_MS.html">documentation</a>.
     */
    public static final int CURLOPT_ACCEPTTIMEOUT_MS = CURLOPTTYPE_LONG + 212;

    /**
     * Set TCP keepalive
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TCP_KEEPALIVE.html">documentation</a>.
     */
    public static final int CURLOPT_TCP_KEEPALIVE = CURLOPTTYPE_LONG + 213;

    /**
     * non-universal keepalive knobs (Linux, AIX, HP-UX, more)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TCP_KEEPIDLE.html">documentation</a>.
     */
    public static final int CURLOPT_TCP_KEEPIDLE = CURLOPTTYPE_LONG + 214;
    public static final int CURLOPT_TCP_KEEPINTVL = CURLOPTTYPE_LONG + 215;

    /**
     * Enable/disable specific SSL features with a bitmask, see CURLSSLOPT_*
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSL_OPTIONS.html">documentation</a>.
     */
    public static final int CURLOPT_SSL_OPTIONS = CURLOPTTYPE_VALUES + 216;

    /**
     * Set the SMTP auth originator
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_MAIL_AUTH.html">documentation</a>.
     */
    public static final int CURLOPT_MAIL_AUTH = CURLOPTTYPE_STRINGPOINT + 217;

    /**
     * Enable/disable SASL initial response
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SASL_IR.html">documentation</a>.
     */
    public static final int CURLOPT_SASL_IR = CURLOPTTYPE_LONG + 218;

    /**
     * Function that will be called instead of the internal progress display
     * function. This function should be defined as the curl_xferinfo_callback
     * prototype defines. (Deprecates CURLOPT_PROGRESSFUNCTION)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_XFERINFOFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_XFERINFOFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 219;

    /**
     * The XOAUTH2 bearer token
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_XOAUTH2_BEARER.html">documentation</a>.
     */
    public static final int CURLOPT_XOAUTH2_BEARER = CURLOPTTYPE_STRINGPOINT + 220;

    /**
     * Set the interface string to use as outgoing network
     * interface for DNS requests.
     * Only supported by the c-ares DNS backend
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DNS_INTERFACE.html">documentation</a>.
     */
    public static final int CURLOPT_DNS_INTERFACE = CURLOPTTYPE_STRINGPOINT + 221;

    /**
     * Set the local IPv4 address to use for outgoing DNS requests.
     * Only supported by the c-ares DNS backend
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DNS_LOCAL_IP4.html">documentation</a>.
     */
    public static final int CURLOPT_DNS_LOCAL_IP4 = CURLOPTTYPE_STRINGPOINT + 222;

    /**
     * Set the local IPv6 address to use for outgoing DNS requests.
     * Only supported by the c-ares DNS backend
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DNS_LOCAL_IP6.html">documentation</a>.
     */
    public static final int CURLOPT_DNS_LOCAL_IP6 = CURLOPTTYPE_STRINGPOINT + 223;

    /**
     * Set authentication options directly
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_LOGIN_OPTIONS.html">documentation</a>.
     */
    public static final int CURLOPT_LOGIN_OPTIONS = CURLOPTTYPE_STRINGPOINT + 224;

    /**
     * Enable/disable TLS NPN extension (http2 over ssl might fail without)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSL_ENABLE_NPN.html">documentation</a>.
     */
    public static final int CURLOPT_SSL_ENABLE_NPN = CURLOPTTYPE_LONG + 225;

    /**
     * Enable/disable TLS ALPN extension (http2 over ssl might fail without)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSL_ENABLE_ALPN.html">documentation</a>.
     */
    public static final int CURLOPT_SSL_ENABLE_ALPN = CURLOPTTYPE_LONG + 226;

    /**
     * Time to wait for a response to an HTTP request containing an
     * Expect: 100-continue header before sending the data anyway.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_EXPECT_100_TIMEOUT_MS.html">documentation</a>.
     */
    public static final int CURLOPT_EXPECT_100_TIMEOUT_MS = CURLOPTTYPE_LONG + 227;

    /**
     * This points to a linked list of headers used for proxy requests only,
     * struct curl_slist kind
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXYHEADER.html">documentation</a>.
     */
    public static final int CURLOPT_PROXYHEADER = CURLOPTTYPE_SLISTPOINT + 228;

    /**
     * Pass in a bitmask of "header options"
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HEADEROPT.html">documentation</a>.
     */
    public static final int CURLOPT_HEADEROPT = CURLOPTTYPE_VALUES + 229;

    /**
     * The public key in DER form used to validate the peer public key
     * this option is used only if SSL_VERIFYPEER is true
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PINNEDPUBLICKEY.html">documentation</a>.
     */
    public static final int CURLOPT_PINNEDPUBLICKEY = CURLOPTTYPE_STRINGPOINT + 230;

    /**
     * Path to Unix domain socket
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_UNIX_SOCKET_PATH.html">documentation</a>.
     */
    public static final int CURLOPT_UNIX_SOCKET_PATH = CURLOPTTYPE_STRINGPOINT + 231;

    /**
     * Set if we should verify the certificate status.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSL_VERIFYSTATUS.html">documentation</a>.
     */
    public static final int CURLOPT_SSL_VERIFYSTATUS = CURLOPTTYPE_LONG + 232;

    /**
     * Set if we should enable TLS false start.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSL_FALSESTART.html">documentation</a>.
     */
    public static final int CURLOPT_SSL_FALSESTART = CURLOPTTYPE_LONG + 233;

    /**
     * Do not squash dot-dot sequences
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PATH_AS_IS.html">documentation</a>.
     */
    public static final int CURLOPT_PATH_AS_IS = CURLOPTTYPE_LONG + 234;

    /**
     * Proxy Service Name
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_SERVICE_NAME.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_SERVICE_NAME = CURLOPTTYPE_STRINGPOINT + 235;

    /**
     * Service Name
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SERVICE_NAME.html">documentation</a>.
     */
    public static final int CURLOPT_SERVICE_NAME = CURLOPTTYPE_STRINGPOINT + 236;

    /**
     * Wait/don't wait for pipe/mutex to clarify
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PIPEWAIT.html">documentation</a>.
     */
    public static final int CURLOPT_PIPEWAIT = CURLOPTTYPE_LONG + 237;

    /**
     * Set the protocol used when curl is given a URL without a protocol
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DEFAULT_PROTOCOL.html">documentation</a>.
     */
    public static final int CURLOPT_DEFAULT_PROTOCOL = CURLOPTTYPE_STRINGPOINT + 238;

    /**
     * Set stream weight, 1 - 256 (default is 16)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_STREAM_WEIGHT.html">documentation</a>.
     */
    public static final int CURLOPT_STREAM_WEIGHT = CURLOPTTYPE_LONG + 239;

    /**
     * Set stream dependency on another CURL handle
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_STREAM_DEPENDS.html">documentation</a>.
     */
    public static final int CURLOPT_STREAM_DEPENDS = CURLOPTTYPE_OBJECTPOINT + 240;

    /**
     * Set E-xclusive stream dependency on another CURL handle
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_STREAM_DEPENDS_E.html">documentation</a>.
     */
    public static final int CURLOPT_STREAM_DEPENDS_E = CURLOPTTYPE_OBJECTPOINT + 241;

    /**
     * Do not send any tftp option requests to the server
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TFTP_NO_OPTIONS.html">documentation</a>.
     */
    public static final int CURLOPT_TFTP_NO_OPTIONS = CURLOPTTYPE_LONG + 242;

    /**
     * Linked-list of host:port:connect-to-host:connect-to-port,
     * overrides the URL's host:port (only for the network layer)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CONNECT_TO.html">documentation</a>.
     */
    public static final int CURLOPT_CONNECT_TO = CURLOPTTYPE_SLISTPOINT + 243;

    /**
     * Set TCP Fast Open
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TCP_FASTOPEN.html">documentation</a>.
     */
    public static final int CURLOPT_TCP_FASTOPEN = CURLOPTTYPE_LONG + 244;

    /**
     * Continue to send data if the server responds early with an
     * HTTP status code >= 300
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_KEEP_SENDING_ON_ERROR.html">documentation</a>.
     */
    public static final int CURLOPT_KEEP_SENDING_ON_ERROR = CURLOPTTYPE_LONG + 245;

    /**
     * The CApath or CAfile used to validate the proxy certificate
     * this option is used only if PROXY_SSL_VERIFYPEER is true
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_CAINFO.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_CAINFO = CURLOPTTYPE_STRINGPOINT + 246;

    /**
     * The CApath directory used to validate the proxy certificate
     * this option is used only if PROXY_SSL_VERIFYPEER is true
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_CAPATH.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_CAPATH = CURLOPTTYPE_STRINGPOINT + 247;

    /**
     * Set if we should verify the proxy in ssl handshake,
     * set 1 to verify.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_SSL_VERIFYPEER.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_SSL_VERIFYPEER = CURLOPTTYPE_LONG + 248;

    /**
     * Set if we should verify the Common name from the proxy certificate in ssl
     * handshake, set 1 to check existence, 2 to ensure that it matches
     * the provided hostname.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_SSL_VERIFYHOST.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_SSL_VERIFYHOST = CURLOPTTYPE_LONG + 249;

    /**
     * What version to specifically try to use for proxy.
     * See CURL_SSLVERSION defines below.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_SSLVERSION.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_SSLVERSION = CURLOPTTYPE_VALUES + 250;

    /**
     * Set a username for authenticated TLS for proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_TLSAUTH_USERNAME.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_TLSAUTH_USERNAME = CURLOPTTYPE_STRINGPOINT + 251;

    /**
     * Set a password for authenticated TLS for proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_TLSAUTH_PASSWORD.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_TLSAUTH_PASSWORD = CURLOPTTYPE_STRINGPOINT + 252;

    /**
     * Set authentication type for authenticated TLS for proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_TLSAUTH_TYPE.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_TLSAUTH_TYPE = CURLOPTTYPE_STRINGPOINT + 253;

    /**
     * name of the file keeping your private SSL-certificate for proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_SSLCERT.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_SSLCERT = CURLOPTTYPE_STRINGPOINT + 254;

    /**
     * type of the file keeping your SSL-certificate ("DER", "PEM", "ENG") for
     * proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_SSLCERTTYPE.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_SSLCERTTYPE = CURLOPTTYPE_STRINGPOINT + 255;

    /**
     * name of the file keeping your private SSL-key for proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_SSLKEY.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_SSLKEY = CURLOPTTYPE_STRINGPOINT + 256;

    /**
     * type of the file keeping your private SSL-key ("DER", "PEM", "ENG") for
     * proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_SSLKEYTYPE.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_SSLKEYTYPE = CURLOPTTYPE_STRINGPOINT + 257;

    /**
     * password for the SSL private key for proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_KEYPASSWD.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_KEYPASSWD = CURLOPTTYPE_STRINGPOINT + 258;

    /**
     * Specify which SSL ciphers to use for proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_SSL_CIPHER_LIST.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_SSL_CIPHER_LIST = CURLOPTTYPE_STRINGPOINT + 259;

    /**
     * CRL file for proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_CRLFILE.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_CRLFILE = CURLOPTTYPE_STRINGPOINT + 260;

    /**
     * Enable/disable specific SSL features with a bitmask for proxy, see
     * CURLSSLOPT_*
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_SSL_OPTIONS.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_SSL_OPTIONS = CURLOPTTYPE_LONG + 261;

    /**
     * Name of pre proxy to use.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PRE_PROXY.html">documentation</a>.
     */
    public static final int CURLOPT_PRE_PROXY = CURLOPTTYPE_STRINGPOINT + 262;

    /**
     * The public key in DER form used to validate the proxy public key
     * this option is used only if PROXY_SSL_VERIFYPEER is true
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_PINNEDPUBLICKEY.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_PINNEDPUBLICKEY = CURLOPTTYPE_STRINGPOINT + 263;

    /**
     * Path to an abstract Unix domain socket
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_ABSTRACT_UNIX_SOCKET.html">documentation</a>.
     */
    public static final int CURLOPT_ABSTRACT_UNIX_SOCKET = CURLOPTTYPE_STRINGPOINT + 264;

    /**
     * Suppress proxy CONNECT response headers from user callbacks
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SUPPRESS_CONNECT_HEADERS.html">documentation</a>.
     */
    public static final int CURLOPT_SUPPRESS_CONNECT_HEADERS = CURLOPTTYPE_LONG + 265;

    /**
     * The request target, instead of extracted from the URL
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_REQUEST_TARGET.html">documentation</a>.
     */
    public static final int CURLOPT_REQUEST_TARGET = CURLOPTTYPE_STRINGPOINT + 266;

    /**
     * bitmask of allowed auth methods for connections to SOCKS5 proxies
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SOCKS5_AUTH.html">documentation</a>.
     */
    public static final int CURLOPT_SOCKS5_AUTH = CURLOPTTYPE_LONG + 267;

    /**
     * Enable/disable SSH compression
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSH_COMPRESSION.html">documentation</a>.
     */
    public static final int CURLOPT_SSH_COMPRESSION = CURLOPTTYPE_LONG + 268;

    /**
     * Post MIME data.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_MIMEPOST.html">documentation</a>.
     */
    public static final int CURLOPT_MIMEPOST = CURLOPTTYPE_OBJECTPOINT + 269;

    /**
     * Time to use with the CURLOPT_TIMECONDITION. Specified in number of
     * seconds since 1 Jan 1970.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TIMEVALUE_LARGE.html">documentation</a>.
     */
    public static final int CURLOPT_TIMEVALUE_LARGE = CURLOPTTYPE_OFF_T + 270;

    /**
     * Head start in milliseconds to give happy eyeballs.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HAPPY_EYEBALLS_TIMEOUT_MS.html">documentation</a>.
     */
    public static final int CURLOPT_HAPPY_EYEBALLS_TIMEOUT_MS = CURLOPTTYPE_LONG + 271;

    /**
     * Function that will be called before a resolver request is made
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RESOLVER_START_FUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_RESOLVER_START_FUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 272;

    /**
     * User data to pass to the resolver start callback.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_RESOLVER_START_DATA.html">documentation</a>.
     */
    public static final int CURLOPT_RESOLVER_START_DATA = CURLOPTTYPE_CBPOINT + 273;

    /**
     * send HAProxy PROXY protocol header?
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HAPROXYPROTOCOL.html">documentation</a>.
     */
    public static final int CURLOPT_HAPROXYPROTOCOL = CURLOPTTYPE_LONG + 274;

    /**
     * shuffle addresses before use when DNS returns multiple
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DNS_SHUFFLE_ADDRESSES.html">documentation</a>.
     */
    public static final int CURLOPT_DNS_SHUFFLE_ADDRESSES = CURLOPTTYPE_LONG + 275;

    /**
     * Specify which TLS 1.3 ciphers suites to use
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TLS13_CIPHERS.html">documentation</a>.
     */
    public static final int CURLOPT_TLS13_CIPHERS = CURLOPTTYPE_STRINGPOINT + 276;
    public static final int CURLOPT_PROXY_TLS13_CIPHERS = CURLOPTTYPE_STRINGPOINT + 277;

    /**
     * Disallow specifying username/login in URL.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DISALLOW_USERNAME_IN_URL.html">documentation</a>.
     */
    public static final int CURLOPT_DISALLOW_USERNAME_IN_URL = CURLOPTTYPE_LONG + 278;

    /**
     * DNS-over-HTTPS URL
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DOH_URL.html">documentation</a>.
     */
    public static final int CURLOPT_DOH_URL = CURLOPTTYPE_STRINGPOINT + 279;

    /**
     * Preferred buffer size to use for uploads
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_UPLOAD_BUFFERSIZE.html">documentation</a>.
     */
    public static final int CURLOPT_UPLOAD_BUFFERSIZE = CURLOPTTYPE_LONG + 280;

    /**
     * Time in ms between connection upkeep calls for long-lived connections.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_UPKEEP_INTERVAL_MS.html">documentation</a>.
     */
    public static final int CURLOPT_UPKEEP_INTERVAL_MS = CURLOPTTYPE_LONG + 281;

    /**
     * Specify URL using CURL URL API.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CURLU.html">documentation</a>.
     */
    public static final int CURLOPT_CURLU = CURLOPTTYPE_OBJECTPOINT + 282;

    /**
     * add trailing data just after no more data is available
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TRAILERFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_TRAILERFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 283;

    /**
     * pointer to be passed to HTTP_TRAILER_FUNCTION
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_TRAILERDATA.html">documentation</a>.
     */
    public static final int CURLOPT_TRAILERDATA = CURLOPTTYPE_CBPOINT + 284;

    /**
     * set this to 1L to allow HTTP/0.9 responses or 0L to disallow
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HTTP09_ALLOWED.html">documentation</a>.
     */
    public static final int CURLOPT_HTTP09_ALLOWED = CURLOPTTYPE_LONG + 285;

    /**
     * alt-svc control bitmask
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_ALTSVC_CTRL.html">documentation</a>.
     */
    public static final int CURLOPT_ALTSVC_CTRL = CURLOPTTYPE_LONG + 286;

    /**
     * alt-svc cache file name to possibly read from/write to
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_ALTSVC.html">documentation</a>.
     */
    public static final int CURLOPT_ALTSVC = CURLOPTTYPE_STRINGPOINT + 287;

    /**
     * maximum age (idle time) of a connection to consider it for reuse
     * (in seconds)
     */

    public static final int CURLOPT_MAXAGE_CONN = CURLOPTTYPE_LONG + 288;

    /**
     * SASL authorization identity
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SASL_AUTHZID.html">documentation</a>.
     */
    public static final int CURLOPT_SASL_AUTHZID = CURLOPTTYPE_STRINGPOINT + 289;

    /**
     * allow RCPT TO command to fail for some recipients
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_MAIL_RCPT_ALLOWFAILS.html">documentation</a>.
     */
    public static final int CURLOPT_MAIL_RCPT_ALLOWFAILS = CURLOPTTYPE_LONG + 290;

    /**
     * the private SSL-certificate as a "blob"
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSLCERT_BLOB.html">documentation</a>.
     */
    public static final int CURLOPT_SSLCERT_BLOB = CURLOPTTYPE_BLOB + 291;
    public static final int CURLOPT_SSLKEY_BLOB = CURLOPTTYPE_BLOB + 292;
    public static final int CURLOPT_PROXY_SSLCERT_BLOB = CURLOPTTYPE_BLOB + 293;
    public static final int CURLOPT_PROXY_SSLKEY_BLOB = CURLOPTTYPE_BLOB + 294;
    public static final int CURLOPT_ISSUERCERT_BLOB = CURLOPTTYPE_BLOB + 295;

    /**
     * Issuer certificate for proxy
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_ISSUERCERT.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_ISSUERCERT = CURLOPTTYPE_STRINGPOINT + 296;
    public static final int CURLOPT_PROXY_ISSUERCERT_BLOB = CURLOPTTYPE_BLOB + 297;

    /**
     * the EC curves requested by the TLS client (RFC 8422, 5.1);
     * OpenSSL support via 'set_groups'/'set_curves':
     * <a href="https://www.openssl.org/docs/manmaster/man3/SSL_CTX_set1_groups.html">OpenSSL docs</a>
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSL_EC_CURVES.html">documentation</a>.
     */
    public static final int CURLOPT_SSL_EC_CURVES = CURLOPTTYPE_STRINGPOINT + 298;

    /**
     * HSTS bitmask
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HSTS_CTRL.html">documentation</a>.
     */
    public static final int CURLOPT_HSTS_CTRL = CURLOPTTYPE_LONG + 299;
    /**
     * HSTS file name
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HSTS.html">documentation</a>.
     */
    public static final int CURLOPT_HSTS = CURLOPTTYPE_STRINGPOINT + 300;

    /**
     * HSTS read callback
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HSTSREADFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_HSTSREADFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 301;
    public static final int CURLOPT_HSTSREADDATA = CURLOPTTYPE_CBPOINT + 302;

    /**
     * HSTS write callback
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HSTSWRITEFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_HSTSWRITEFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 303;
    public static final int CURLOPT_HSTSWRITEDATA = CURLOPTTYPE_CBPOINT + 304;

    /**
     * Parameters for V4 signature
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_AWS_SIGV4.html">documentation</a>.
     */
    public static final int CURLOPT_AWS_SIGV4 = CURLOPTTYPE_STRINGPOINT + 305;

    /**
     * Same as CURLOPT_SSL_VERIFYPEER but for DoH (DNS-over-HTTPS) servers.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DOH_SSL_VERIFYPEER.html">documentation</a>.
     */
    public static final int CURLOPT_DOH_SSL_VERIFYPEER = CURLOPTTYPE_LONG + 306;

    /**
     * Same as CURLOPT_SSL_VERIFYHOST but for DoH (DNS-over-HTTPS) servers.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DOH_SSL_VERIFYHOST.html">documentation</a>.
     */
    public static final int CURLOPT_DOH_SSL_VERIFYHOST = CURLOPTTYPE_LONG + 307;

    /**
     * Same as CURLOPT_SSL_VERIFYSTATUS but for DoH (DNS-over-HTTPS) servers.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_DOH_SSL_VERIFYSTATUS.html">documentation</a>.
     */
    public static final int CURLOPT_DOH_SSL_VERIFYSTATUS = CURLOPTTYPE_LONG + 308;

    /**
     * The CA certificates as "blob" used to validate the peer certificate
     * this option is used only if SSL_VERIFYPEER is true
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CAINFO_BLOB.html">documentation</a>.
     */
    public static final int CURLOPT_CAINFO_BLOB = CURLOPTTYPE_BLOB + 309;

    /**
     * The CA certificates as "blob" used to validate the proxy certificate
     * this option is used only if PROXY_SSL_VERIFYPEER is true
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROXY_CAINFO_BLOB.html">documentation</a>.
     */
    public static final int CURLOPT_PROXY_CAINFO_BLOB = CURLOPTTYPE_BLOB + 310;

    /**
     * used by scp/sftp to verify the host's public key
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSH_HOST_PUBLIC_KEY_SHA256.html">documentation</a>.
     */
    public static final int CURLOPT_SSH_HOST_PUBLIC_KEY_SHA256 = CURLOPTTYPE_STRINGPOINT + 311;

    /**
     * Function that will be called immediately before the initial request
     * is made on a connection (after any protocol negotiation step).
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PREREQFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_PREREQFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 312;

    /**
     * Data passed to the CURLOPT_PREREQFUNCTION callback
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PREREQDATA.html">documentation</a>.
     */
    public static final int CURLOPT_PREREQDATA = CURLOPTTYPE_CBPOINT + 313;

    /**
     * maximum age (since creation) of a connection to consider it for reuse
     * (in seconds)
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_MAXLIFETIME_CONN.html">documentation</a>.
     */
    public static final int CURLOPT_MAXLIFETIME_CONN = CURLOPTTYPE_LONG + 314;

    /**
     * Set MIME option flags.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_MIME_OPTIONS.html">documentation</a>.
     */
    public static final int CURLOPT_MIME_OPTIONS = CURLOPTTYPE_LONG + 315;

    /**
     * set the SSH host key callback, must point to a curl_sshkeycallback
     * function
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSH_HOSTKEYFUNCTION.html">documentation</a>.
     */
    public static final int CURLOPT_SSH_HOSTKEYFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 316;

    /**
     * set the SSH host key callback custom pointer
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_SSH_HOSTKEYDATA.html">documentation</a>.
     */
    public static final int CURLOPT_SSH_HOSTKEYDATA = CURLOPTTYPE_CBPOINT + 317;

    /**
     * specify which protocols that are allowed to be used for the transfer,
     * which thus helps the app which takes URLs from users or other external
     * inputs and want to restrict what protocol(s) to deal with. Defaults to
     * all built-in protocols.
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_PROTOCOLS_STR.html">documentation</a>.
     */
    public static final int CURLOPT_PROTOCOLS_STR = CURLOPTTYPE_STRINGPOINT + 318;

    /**
     * specify which protocols that libcurl is allowed to follow directs to
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_REDIR_PROTOCOLS_STR.html">documentation</a>.
     */
    public static final int CURLOPT_REDIR_PROTOCOLS_STR = CURLOPTTYPE_STRINGPOINT + 319;

    /**
     * websockets options
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_WS_OPTIONS.html">documentation</a>.
     */
    public static final int CURLOPT_WS_OPTIONS = CURLOPTTYPE_LONG + 320;

    /**
     * CA cache timeout
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_CA_CACHE_TIMEOUT.html">documentation</a>.
     */
    public static final int CURLOPT_CA_CACHE_TIMEOUT = CURLOPTTYPE_LONG + 321;

    /**
     * Can leak things, gonna exit() soon
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_QUICK_EXIT.html">documentation</a>.
     */
    public static final int CURLOPT_QUICK_EXIT = CURLOPTTYPE_LONG + 322;

    /**
     * set a specific client IP for HAProxy PROXY protocol header?
     * <p>
     * See the <a href="https://curl.se/libcurl/c/CURLOPT_HAPROXY_CLIENT_IP.html">documentation</a>.
     */
    public static final int CURLOPT_HAPROXY_CLIENT_IP = CURLOPTTYPE_STRINGPOINT + 323;
    //endregion

    // region CURLINFO
    public static final int CURLINFO_STRING = 0x100000;
    public static final int CURLINFO_LONG = 0x200000;
    public static final int CURLINFO_DOUBLE = 0x300000;
    public static final int CURLINFO_SLIST = 0x400000;
    public static final int CURLINFO_PTR = 0x400000;
    public static final int CURLINFO_SOCKET = 0x500000;
    public static final int CURLINFO_OFF_T = 0x600000;
    public static final int CURLINFO_MASK = 0x0fffff;
    public static final int CURLINFO_TYPEMASK = 0xf00000;

    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_EFFECTIVE_URL.html">documentation</a>.
     */
    public static final int CURLINFO_EFFECTIVE_URL = CURLINFO_STRING + 1;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_RESPONSE_CODE.html">documentation</a>.
     */
    public static final int CURLINFO_RESPONSE_CODE = CURLINFO_LONG + 2;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_TOTAL_TIME.html">documentation</a>.
     */
    public static final int CURLINFO_TOTAL_TIME = CURLINFO_DOUBLE + 3;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_NAMELOOKUP_TIME.html">documentation</a>.
     */
    public static final int CURLINFO_NAMELOOKUP_TIME = CURLINFO_DOUBLE + 4;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_CONNECT_TIME.html">documentation</a>.
     */
    public static final int CURLINFO_CONNECT_TIME = CURLINFO_DOUBLE + 5;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_PRETRANSFER_TIME.html">documentation</a>.
     */
    public static final int CURLINFO_PRETRANSFER_TIME = CURLINFO_DOUBLE + 6;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_SIZE_UPLOAD.html">documentation</a>.
     */
    @Deprecated // Use CURLINFO_SIZE_UPLOAD_T
    public static final int CURLINFO_SIZE_UPLOAD = CURLINFO_DOUBLE + 7;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_SIZE_UPLOAD_T.html">documentation</a>.
     */
    public static final int CURLINFO_SIZE_UPLOAD_T = CURLINFO_OFF_T + 7;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_SIZE_DOWNLOAD.html">documentation</a>.
     */
    @Deprecated // Use CURLINFO_SIZE_DOWNLOAD_T
    public static final int CURLINFO_SIZE_DOWNLOAD = CURLINFO_DOUBLE + 8;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_SIZE_DOWNLOAD_T.html">documentation</a>.
     */
    public static final int CURLINFO_SIZE_DOWNLOAD_T = CURLINFO_OFF_T + 8;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_SPEED_DOWNLOAD.html">documentation</a>.
     */
    @Deprecated // Use CURLINFO_SPEED_DOWNLOAD_T
    public static final int CURLINFO_SPEED_DOWNLOAD = CURLINFO_DOUBLE + 9;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_SPEED_DOWNLOAD_T.html">documentation</a>.
     */
    public static final int CURLINFO_SPEED_DOWNLOAD_T = CURLINFO_OFF_T + 9;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_SPEED_UPLOAD.html">documentation</a>.
     */
    @Deprecated // Use CURLINFO_SPEED_UPLOAD_T
    public static final int CURLINFO_SPEED_UPLOAD = CURLINFO_DOUBLE + 10;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_SPEED_UPLOAD_T.html">documentation</a>.
     */
    public static final int CURLINFO_SPEED_UPLOAD_T = CURLINFO_OFF_T + 10;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_HEADER_SIZE.html">documentation</a>.
     */
    public static final int CURLINFO_HEADER_SIZE = CURLINFO_LONG + 11;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_REQUEST_SIZE.html">documentation</a>.
     */
    public static final int CURLINFO_REQUEST_SIZE = CURLINFO_LONG + 12;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_SSL_VERIFYRESULT.html">documentation</a>.
     */
    public static final int CURLINFO_SSL_VERIFYRESULT = CURLINFO_LONG + 13;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_FILETIME.html">documentation</a>.
     */
    public static final int CURLINFO_FILETIME = CURLINFO_LONG + 14;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_FILETIME_T.html">documentation</a>.
     */
    public static final int CURLINFO_FILETIME_T = CURLINFO_OFF_T + 14;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_CONTENT_LENGTH_DOWNLOAD.html">documentation</a>.
     */
    @Deprecated // Use CURLINFO_CONTENT_LENGTH_DOWNLOAD_T
    public static final int CURLINFO_CONTENT_LENGTH_DOWNLOAD = CURLINFO_DOUBLE + 15;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_CONTENT_LENGTH_DOWNLOAD_T.html">documentation</a>.
     */
    public static final int CURLINFO_CONTENT_LENGTH_DOWNLOAD_T = CURLINFO_OFF_T + 15;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_CONTENT_LENGTH_UPLOAD.html">documentation</a>.
     */
    @Deprecated // Use CURLINFO_CONTENT_LENGTH_UPLOAD_T
    public static final int CURLINFO_CONTENT_LENGTH_UPLOAD = CURLINFO_DOUBLE + 16;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_CONTENT_LENGTH_UPLOAD_T.html">documentation</a>.
     */
    public static final int CURLINFO_CONTENT_LENGTH_UPLOAD_T = CURLINFO_OFF_T + 16;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_STARTTRANSFER_TIME.html">documentation</a>.
     */
    public static final int CURLINFO_STARTTRANSFER_TIME = CURLINFO_DOUBLE + 17;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_CONTENT_TYPE.html">documentation</a>.
     */
    public static final int CURLINFO_CONTENT_TYPE = CURLINFO_STRING + 18;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_REDIRECT_TIME.html">documentation</a>.
     */
    public static final int CURLINFO_REDIRECT_TIME = CURLINFO_DOUBLE + 19;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_REDIRECT_COUNT.html">documentation</a>.
     */
    public static final int CURLINFO_REDIRECT_COUNT = CURLINFO_LONG + 20;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_PRIVATE.html">documentation</a>.
     */
    public static final int CURLINFO_PRIVATE = CURLINFO_STRING + 21;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_HTTP_CONNECTCODE.html">documentation</a>.
     */
    public static final int CURLINFO_HTTP_CONNECTCODE = CURLINFO_LONG + 22;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_HTTPAUTH_AVAIL.html">documentation</a>.
     */
    public static final int CURLINFO_HTTPAUTH_AVAIL = CURLINFO_LONG + 23;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_PROXYAUTH_AVAIL.html">documentation</a>.
     */
    public static final int CURLINFO_PROXYAUTH_AVAIL = CURLINFO_LONG + 24;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_OS_ERRNO.html">documentation</a>.
     */
    public static final int CURLINFO_OS_ERRNO = CURLINFO_LONG + 25;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_NUM_CONNECTS.html">documentation</a>.
     */
    public static final int CURLINFO_NUM_CONNECTS = CURLINFO_LONG + 26;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_SSL_ENGINES.html">documentation</a>.
     */
    public static final int CURLINFO_SSL_ENGINES = CURLINFO_SLIST + 27;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_COOKIELIST.html">documentation</a>.
     */
    public static final int CURLINFO_COOKIELIST = CURLINFO_SLIST + 28;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_LASTSOCKET.html">documentation</a>.
     */
    @Deprecated // Use CURLINFO_ACTIVESOCKET
    public static final int CURLINFO_LASTSOCKET = CURLINFO_LONG + 29;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_FTP_ENTRY_PATH.html">documentation</a>.
     */
    public static final int CURLINFO_FTP_ENTRY_PATH = CURLINFO_STRING + 30;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_REDIRECT_URL.html">documentation</a>.
     */
    public static final int CURLINFO_REDIRECT_URL = CURLINFO_STRING + 31;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_PRIMARY_IP.html">documentation</a>.
     */
    public static final int CURLINFO_PRIMARY_IP = CURLINFO_STRING + 32;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_APPCONNECT_TIME.html">documentation</a>.
     */
    public static final int CURLINFO_APPCONNECT_TIME = CURLINFO_DOUBLE + 33;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_CERTINFO.html">documentation</a>.
     */
    public static final int CURLINFO_CERTINFO = CURLINFO_PTR + 34;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_CONDITION_UNMET.html">documentation</a>.
     */
    public static final int CURLINFO_CONDITION_UNMET = CURLINFO_LONG + 35;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_RTSP_SESSION_ID.html">documentation</a>.
     */
    public static final int CURLINFO_RTSP_SESSION_ID = CURLINFO_STRING + 36;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_RTSP_CLIENT_CSEQ.html">documentation</a>.
     */
    public static final int CURLINFO_RTSP_CLIENT_CSEQ = CURLINFO_LONG + 37;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_RTSP_SERVER_CSEQ.html">documentation</a>.
     */
    public static final int CURLINFO_RTSP_SERVER_CSEQ = CURLINFO_LONG + 38;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_RTSP_CSEQ_RECV.html">documentation</a>.
     */
    public static final int CURLINFO_RTSP_CSEQ_RECV = CURLINFO_LONG + 39;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_PRIMARY_PORT.html">documentation</a>.
     */
    public static final int CURLINFO_PRIMARY_PORT = CURLINFO_LONG + 40;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_LOCAL_IP.html">documentation</a>.
     */
    public static final int CURLINFO_LOCAL_IP = CURLINFO_STRING + 41;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_LOCAL_PORT.html">documentation</a>.
     */
    public static final int CURLINFO_LOCAL_PORT = CURLINFO_LONG + 42;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_TLS_SESSION.html">documentation</a>.
     */
    @Deprecated // Use CURLINFO_TLS_SSL_PTR
    public static final int CURLINFO_TLS_SESSION = CURLINFO_PTR + 43;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_ACTIVESOCKET.html">documentation</a>.
     */
    public static final int CURLINFO_ACTIVESOCKET = CURLINFO_SOCKET + 44;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_TLS_SSL_PTR.html">documentation</a>.
     */
    public static final int CURLINFO_TLS_SSL_PTR = CURLINFO_PTR + 45;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_HTTP_VERSION.html">documentation</a>.
     */
    public static final int CURLINFO_HTTP_VERSION = CURLINFO_LONG + 46;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_PROXY_SSL_VERIFYRESULT.html">documentation</a>.
     */
    public static final int CURLINFO_PROXY_SSL_VERIFYRESULT = CURLINFO_LONG + 47;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_PROTOCOL.html">documentation</a>.
     */
    @Deprecated // Use CURLINFO_SCHEME
    public static final int CURLINFO_PROTOCOL = CURLINFO_LONG + 48;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_SCHEME.html">documentation</a>.
     */
    public static final int CURLINFO_SCHEME = CURLINFO_STRING + 49;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_TOTAL_TIME_T.html">documentation</a>.
     */
    public static final int CURLINFO_TOTAL_TIME_T = CURLINFO_OFF_T + 50;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_NAMELOOKUP_TIME_T.html">documentation</a>.
     */
    public static final int CURLINFO_NAMELOOKUP_TIME_T = CURLINFO_OFF_T + 51;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_CONNECT_TIME_T.html">documentation</a>.
     */
    public static final int CURLINFO_CONNECT_TIME_T = CURLINFO_OFF_T + 52;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_PRETRANSFER_TIME_T.html">documentation</a>.
     */
    public static final int CURLINFO_PRETRANSFER_TIME_T = CURLINFO_OFF_T + 53;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_STARTTRANSFER_TIME_T.html">documentation</a>.
     */
    public static final int CURLINFO_STARTTRANSFER_TIME_T = CURLINFO_OFF_T + 54;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_REDIRECT_TIME_T.html">documentation</a>.
     */
    public static final int CURLINFO_REDIRECT_TIME_T = CURLINFO_OFF_T + 55;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_APPCONNECT_TIME_T.html">documentation</a>.
     */
    public static final int CURLINFO_APPCONNECT_TIME_T = CURLINFO_OFF_T + 56;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_RETRY_AFTER.html">documentation</a>.
     */
    public static final int CURLINFO_RETRY_AFTER = CURLINFO_OFF_T + 57;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_EFFECTIVE_METHOD.html">documentation</a>.
     */
    public static final int CURLINFO_EFFECTIVE_METHOD = CURLINFO_STRING + 58;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_PROXY_ERROR.html">documentation</a>.
     */
    public static final int CURLINFO_PROXY_ERROR = CURLINFO_LONG + 59;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_REFERER.html">documentation</a>.
     */
    public static final int CURLINFO_REFERER = CURLINFO_STRING + 60;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_CAINFO.html">documentation</a>.
     */
    public static final int CURLINFO_CAINFO = CURLINFO_STRING + 61;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_CAPATH.html">documentation</a>.
     */
    public static final int CURLINFO_CAPATH = CURLINFO_STRING + 62;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_XFER_ID.html">documentation</a>.
     */
    public static final int CURLINFO_XFER_ID = CURLINFO_OFF_T + 63;
    /**
     * See the <a href="https://curl.se/libcurl/c/CURLINFO_CONN_ID.html">documentation</a>.
     */
    public static final int CURLINFO_CONN_ID = CURLINFO_OFF_T + 64;
    //endregion

    //region CURLE results.
    /**
     * All possible error codes from all sorts of curl functions. Future versions
     * may return other values, stay prepared.
     */
    public static final int CURLE_OK = 0;
    public static final int CURLE_UNSUPPORTED_PROTOCOL = 1;
    public static final int CURLE_FAILED_INIT = 2;
    public static final int CURLE_URL_MALFORMAT = 3;
    public static final int CURLE_NOT_BUILT_IN = 4;
    public static final int CURLE_COULDNT_RESOLVE_PROXY = 5;
    public static final int CURLE_COULDNT_RESOLVE_HOST = 6;
    public static final int CURLE_COULDNT_CONNECT = 7;
    public static final int CURLE_WEIRD_SERVER_REPLY = 8;
    public static final int CURLE_REMOTE_ACCESS_DENIED = 9;
    public static final int CURLE_FTP_ACCEPT_FAILED = 10;
    public static final int CURLE_FTP_WEIRD_PASS_REPLY = 11;
    public static final int CURLE_FTP_ACCEPT_TIMEOUT = 12;
    public static final int CURLE_FTP_WEIRD_PASV_REPLY = 13;
    public static final int CURLE_FTP_WEIRD_227_FORMAT = 14;
    public static final int CURLE_FTP_CANT_GET_HOST = 15;
    public static final int CURLE_HTTP2 = 16;
    public static final int CURLE_FTP_COULDNT_SET_TYPE = 17;
    public static final int CURLE_PARTIAL_FILE = 18;
    public static final int CURLE_FTP_COULDNT_RETR_FILE = 19;
    public static final int CURLE_OBSOLETE20 = 20;
    public static final int CURLE_QUOTE_ERROR = 21;
    public static final int CURLE_HTTP_RETURNED_ERROR = 22;
    public static final int CURLE_WRITE_ERROR = 23;
    public static final int CURLE_OBSOLETE24 = 24;
    public static final int CURLE_UPLOAD_FAILED = 25;
    public static final int CURLE_READ_ERROR = 26;
    public static final int CURLE_OUT_OF_MEMORY = 27;
    public static final int CURLE_OPERATION_TIMEDOUT = 28;
    public static final int CURLE_OBSOLETE29 = 29;
    public static final int CURLE_FTP_PORT_FAILED = 30;
    public static final int CURLE_FTP_COULDNT_USE_REST = 31;
    public static final int CURLE_OBSOLETE32 = 32;
    public static final int CURLE_RANGE_ERROR = 33;
    public static final int CURLE_HTTP_POST_ERROR = 34;
    public static final int CURLE_SSL_CONNECT_ERROR = 35;
    public static final int CURLE_BAD_DOWNLOAD_RESUME = 36;
    public static final int CURLE_FILE_COULDNT_READ_FILE = 37;
    public static final int CURLE_LDAP_CANNOT_BIND = 38;
    public static final int CURLE_LDAP_SEARCH_FAILED = 39;
    public static final int CURLE_OBSOLETE40 = 40;
    public static final int CURLE_FUNCTION_NOT_FOUND = 41;
    public static final int CURLE_ABORTED_BY_CALLBACK = 42;
    public static final int CURLE_BAD_FUNCTION_ARGUMENT = 43;
    public static final int CURLE_OBSOLETE44 = 44;
    public static final int CURLE_INTERFACE_FAILED = 45;
    public static final int CURLE_OBSOLETE46 = 46;
    public static final int CURLE_TOO_MANY_REDIRECTS = 47;
    public static final int CURLE_UNKNOWN_OPTION = 48;
    public static final int CURLE_SETOPT_OPTION_SYNTAX = 49;
    public static final int CURLE_OBSOLETE50 = 50;
    public static final int CURLE_OBSOLETE51 = 51;
    public static final int CURLE_GOT_NOTHING = 52;
    public static final int CURLE_SSL_ENGINE_NOTFOUND = 53;
    public static final int CURLE_SSL_ENGINE_SETFAILED = 54;
    public static final int CURLE_SEND_ERROR = 55;
    public static final int CURLE_RECV_ERROR = 56;
    public static final int CURLE_OBSOLETE57 = 57;
    public static final int CURLE_SSL_CERTPROBLEM = 58;
    public static final int CURLE_SSL_CIPHER = 59;
    public static final int CURLE_PEER_FAILED_VERIFICATION = 60;
    public static final int CURLE_BAD_CONTENT_ENCODING = 61;
    public static final int CURLE_OBSOLETE62 = 62;
    public static final int CURLE_FILESIZE_EXCEEDED = 63;
    public static final int CURLE_USE_SSL_FAILED = 64;
    public static final int CURLE_SEND_FAIL_REWIND = 65;
    public static final int CURLE_SSL_ENGINE_INITFAILED = 66;
    public static final int CURLE_LOGIN_DENIED = 67;
    public static final int CURLE_TFTP_NOTFOUND = 68;
    public static final int CURLE_TFTP_PERM = 69;
    public static final int CURLE_REMOTE_DISK_FULL = 70;
    public static final int CURLE_TFTP_ILLEGAL = 71;
    public static final int CURLE_TFTP_UNKNOWNID = 72;
    public static final int CURLE_REMOTE_FILE_EXISTS = 73;
    public static final int CURLE_TFTP_NOSUCHUSER = 74;
    public static final int CURLE_CONV_FAILED = 75;
    public static final int CURLE_OBSOLETE76 = 76;
    public static final int CURLE_SSL_CACERT_BADFILE = 77;
    public static final int CURLE_REMOTE_FILE_NOT_FOUND = 78;
    public static final int CURLE_SSH = 79;
    public static final int CURLE_SSL_SHUTDOWN_FAILED = 80;
    public static final int CURLE_AGAIN = 81;
    public static final int CURLE_SSL_CRL_BADFILE = 82;
    public static final int CURLE_SSL_ISSUER_ERROR = 83;
    public static final int CURLE_FTP_PRET_FAILED = 84;
    public static final int CURLE_RTSP_CSEQ_ERROR = 85;
    public static final int CURLE_RTSP_SESSION_ERROR = 86;
    public static final int CURLE_FTP_BAD_FILE_LIST = 87;
    public static final int CURLE_CHUNK_FAILED = 88;
    public static final int CURLE_NO_CONNECTION_AVAILABLE = 89;
    public static final int CURLE_SSL_PINNEDPUBKEYNOTMATCH = 90;
    public static final int CURLE_SSL_INVALIDCERTSTATUS = 91;
    public static final int CURLE_HTTP2_STREAM = 92;
    public static final int CURLE_RECURSIVE_API_CALL = 93;
    public static final int CURLE_AUTH_ERROR = 94;
    public static final int CURLE_HTTP3 = 95;
    public static final int CURLE_QUIC_CONNECT_ERROR = 96;
    public static final int CURLE_PROXY = 97;
    public static final int CURLE_SSL_CLIENTCERT = 98;
    //endregion

    // region CURLVERSION
    public static final int CURLVERSION_FIRST = 0;
    public static final int CURLVERSION_SECOND = 1;
    public static final int CURLVERSION_THIRD = 2;
    public static final int CURLVERSION_FOURTH = 3;
    public static final int CURLVERSION_FIFTH = 4;
    public static final int CURLVERSION_SIXTH = 5;
    public static final int CURLVERSION_SEVENTH = 6;
    public static final int CURLVERSION_EIGHTH = 7;
    public static final int CURLVERSION_NINTH = 8;
    public static final int CURLVERSION_TENTH = 9;
    public static final int CURLVERSION_ELEVENTH = 11;
    public static final int CURLVERSION_NOW = CURLVERSION_ELEVENTH;

    /**
     * IPv6-enabled
     */
    @SuppressWarnings ("PointlessBitwiseExpression")
    public static final int CURL_VERSION_IPV6 = (1 << 0);
    /**
     * Kerberos V4 auth is supported (deprecated)
     */
    @Deprecated
    public static final int CURL_VERSION_KERBEROS4 = (1 << 1);
    /**
     * SSL options are present
     */
    public static final int CURL_VERSION_SSL = (1 << 2);
    /**
     * libz features are present
     */
    public static final int CURL_VERSION_LIBZ = (1 << 3);
    /**
     * NTLM auth is supported
     */
    public static final int CURL_VERSION_NTLM = (1 << 4);
    /**
     * Negotiate auth is supported (deprecated)
     */
    @Deprecated
    public static final int CURL_VERSION_GSSNEGOTIATE = (1 << 5);
    /**
     * Built with debug capabilities
     */
    public static final int CURL_VERSION_DEBUG = (1 << 6);
    /**
     * Asynchronous DNS resolves
     */
    public static final int CURL_VERSION_ASYNCHDNS = (1 << 7);
    /**
     * SPNEGO auth is supported
     */
    public static final int CURL_VERSION_SPNEGO = (1 << 8);
    /**
     * Supports files larger than 2GB
     */
    public static final int CURL_VERSION_LARGEFILE = (1 << 9);
    /**
     * Internationized Domain Names are supported
     */
    public static final int CURL_VERSION_IDN = (1 << 10);
    /**
     * Built against Windows SSPI
     */
    public static final int CURL_VERSION_SSPI = (1 << 11);
    /**
     * Character conversions supported
     */
    public static final int CURL_VERSION_CONV = (1 << 12);
    /**
     * Debug memory tracking supported
     */
    public static final int CURL_VERSION_CURLDEBUG = (1 << 13);
    /**
     * TLS-SRP auth is supported
     */
    public static final int CURL_VERSION_TLSAUTH_SRP = (1 << 14);
    /**
     * NTLM delegation to winbind helper is supported
     */
    public static final int CURL_VERSION_NTLM_WB = (1 << 15);
    /**
     * HTTP2 support built-in
     */
    public static final int CURL_VERSION_HTTP2 = (1 << 16);
    /**
     * Built against a GSS-API library
     */
    public static final int CURL_VERSION_GSSAPI = (1 << 17);
    /**
     * Kerberos V5 auth is supported
     */
    public static final int CURL_VERSION_KERBEROS5 = (1 << 18);
    /**
     * Unix domain sockets support
     */
    public static final int CURL_VERSION_UNIX_SOCKETS = (1 << 19);
    /**
     * Mozilla's Public Suffix List, used for cookie domain verification
     */
    public static final int CURL_VERSION_PSL = (1 << 20);
    /**
     * HTTPS-proxy support built-in
     */
    public static final int CURL_VERSION_HTTPS_PROXY = (1 << 21);
    /**
     * Multiple SSL backends available
     */
    public static final int CURL_VERSION_MULTI_SSL = (1 << 22);
    /**
     * Brotli features are present.
     */
    public static final int CURL_VERSION_BROTLI = (1 << 23);
    /**
     * Alt-Svc handling built-in
     */
    public static final int CURL_VERSION_ALTSVC = (1 << 24);
    /**
     * HTTP3 support built-in
     */
    public static final int CURL_VERSION_HTTP3 = (1 << 25);
    /**
     * zstd features are present
     */
    public static final int CURL_VERSION_ZSTD = (1 << 26);
    /**
     * Unicode support on Windows
     */
    public static final int CURL_VERSION_UNICODE = (1 << 27);
    /**
     * HSTS is supported
     */
    public static final int CURL_VERSION_HSTS = (1 << 28);
    /**
     * libgsasl is supported
     */
    public static final int CURL_VERSION_GSASL = (1 << 29);
    //endregion

    /**
     * See the curl <a href="https://curl.se/libcurl/c/curl_version.html">documentation</a>.
     *
     * @return a static ascii string of the libcurl version.
     */
    public static String curl_version() {
        return ncurl_version(Functions.curl_version);
    }

    /**
     * This function returns a static copy of the version info struct.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_version_info.html">documentation</a>.
     *
     * @return The curl version data.
     */
    @NativeType ("curl_version_info_data *")
    public static curl_version_info_data curl_version_info() {
        return new curl_version_info_data(ncurl_version_info(Functions.curl_version_info));
    }

    /**
     * curl_global_init() should be invoked exactly once for each application that
     * uses libcurl and before any call of other libcurl functions.
     * <p>
     * This function is thread-safe if {@link #CURL_VERSION_THREADSAFE} is set in the
     * {@link curl_version_info_data#features()} flag (fetch by {@link #curl_version_info}).
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_global_init.html">documentation</a>.
     */
    public static void curl_global_init(long flags) {
        ncurl_global_init(Functions.curl_global_init, flags);
    }

    /**
     * curl_global_cleanup() should be invoked exactly once for each application
     * that uses libcurl
     * <p>
     * Companion to {@link #curl_global_init}.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_global_cleanup.html">documentation</a>.
     */
    public static void curl_global_cleanup() {
        ncurl_global_cleanup(Functions.curl_global_cleanup);
    }

    /**
     * Create and return a new CURL handle.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_easy_init.html">documentation</a>.
     *
     * @return The CURL handle.
     */
    @NativeType ("CURL *")
    public static long curl_easy_init() {
        return ncurl_easy_init(Functions.curl_easy_init);
    }

    /**
     * Re-initializes a CURL handle to the default values. This puts back the
     * handle to the same state as it was in when it was just created.
     * <p>
     * It does keep: live connections, the Session ID cache, the DNS cache and the
     * cookies.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_easy_reset.html">documentation</a>.
     *
     * @param curl The CURL handle.
     */
    public static void curl_easy_reset(@NativeType ("CURL *") long curl) {
        ncurl_easy_reset(Functions.curl_easy_reset, curl);
    }

    /**
     * Set a CURL option.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_easy_setopt.html">documentation</a>.
     *
     * @param curl  The CURL handle.
     * @param opt   The option being set.
     * @param value The value.
     */
    public static int curl_easy_setopt(@NativeType ("CURL *") long curl, @NativeType ("CURLoption") int opt, long value) {
        return ncurl_easy_setopt(Functions.curl_easy_setopt, curl, opt, value);
    }

    /**
     * Set a boolean curl option.
     * <p>
     * This is equivalent to {@code curl_easy_setopt(curl, opt, value ? 1 : 0)}.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_easy_setopt.html">documentation</a>.
     *
     * @param curl  The CURL handle.
     * @param opt   The option being set.
     * @param value The value.
     */
    public static void curl_easy_setopt(@NativeType ("CURL *") long curl, @NativeType ("CURLoption") int opt, boolean value) {
        curl_easy_setopt(curl, opt, value ? 1 : 0);
    }

    /**
     * Set a curl {@link #CURLOPTTYPE_STRINGPOINT} option.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_easy_setopt.html">documentation</a>.
     *
     * @param curl  The CURL handle.
     * @param opt   The option being set.
     * @param value The value to set.
     */
    public static int curl_easy_setopt(@NativeType ("CURL *") long curl, @NativeType ("CURLoption") int opt, String value) {
        return ncurl_easy_setopt(Functions.curl_easy_setopt, curl, opt, value);
    }

    /**
     * Set a curl {@link #CURLOPTTYPE_FUNCTIONPOINT} option.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_easy_setopt.html">documentation</a>.
     *
     * @param curl The CURL handle.
     * @param opt  The option being set.
     * @param func The function.
     */
    public static int curl_easy_setopt(@NativeType ("CURL *") long curl, @NativeType ("CURLoption") int opt, CurlCallback func) {
        return ncurl_easy_setopt(Functions.curl_easy_setopt, curl, opt, func.getFunctionAddress());
    }

    /**
     * Set a curl {@link #CURLOPTTYPE_SLISTPOINT} option.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_easy_setopt.html">documentation</a>.
     *
     * @param curl  The CURL handle.
     * @param opt   The option being set.
     * @param slist The {@link curl_slist}.
     */
    public static int curl_easy_setopt(@NativeType ("CURL *") long curl, @NativeType ("CURLoption") int opt, @Nullable curl_slist slist) {
        return ncurl_easy_setopt(Functions.curl_easy_setopt, curl, opt, slist != null ? slist.address : Memory.NULL);
    }

    /**
     * Request internal information from the curl session with this function.
     * The data pointed to will be filled in accordingly and can be relied upon
     * only if the function returns {@link #CURLE_OK}. This function is intended to get
     * used <strong>AFTER</strong> a performed transfer, all results from this function are
     * undefined until the transfer is completed.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_easy_getinfo.html">documentation</a>.
     * <p>
     * This function has overloads for specific CURLINFO types for ease of use. See bellow.
     *
     * @param curl  The CURL handle.
     * @param opt   The info to select.
     * @param value Pointer to store the value in.
     */
    @NativeType ("CURLcode")
    public static int curl_easy_getinfo(@NativeType ("CURL *") long curl, @NativeType ("CURLINFO") int opt, Pointer value) {
        return ncurl_easy_getinfo(Functions.curl_easy_getinfo, curl, opt, value.address);
    }

    /**
     * Overload of {@link #curl_easy_getinfo(long, int, Pointer)} for {@link #CURLINFO_STRING} types.
     *
     * @param curl The CURL handle.
     * @param opt  The info to select.
     * @return The string.
     */
    @Nullable
    public static String curl_easy_getinfo_String(@NativeType ("CURL *") long curl, @NativeType ("CURLINFO") int opt) {
        assert (opt & CURLINFO_TYPEMASK) == CURLINFO_STRING;

        try (Memory.Stack stack = Memory.pushStack()) {
            Pointer pointer = stack.mallocPointer();
            int ret = curl_easy_getinfo(curl, opt, pointer);
            if (ret != CURLE_OK) {
                throw new IllegalStateException("CURL error querying info: " + curl_easy_strerror(ret));
            }
            return pointer.readUtf8Safe();
        }
    }

    /**
     * Overload of {@link #curl_easy_getinfo(long, int, Pointer)} for
     * {@link #CURLINFO_LONG} or {@link #CURLINFO_OFF_T} types.
     *
     * @param curl The CURL handle.
     * @param opt  The info to select.
     * @return The string.
     */
    public static long curl_easy_getinfo_long(@NativeType ("CURL *") long curl, @NativeType ("CURLINFO") int opt) {
        boolean isLong = (opt & CURLINFO_TYPEMASK) == CURLINFO_LONG;
        assert isLong || (opt & CURLINFO_TYPEMASK) == CURLINFO_OFF_T;

        try (Memory.Stack stack = Memory.pushStack()) {
            Pointer pointer = stack.mallocPointer();
            int ret = curl_easy_getinfo(curl, opt, pointer);
            if (ret != CURLE_OK) {
                throw new IllegalStateException("CURL error querying info: " + curl_easy_strerror(ret));
            }

            return isLong ? pointer.readCLong() : pointer.readSizeT();
        }
    }

    /**
     * Perform the curl operation.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_easy_perform.html">documentation</a>.
     *
     * @param curl The CURL handle.
     * @return The curl exit code.
     */
    @NativeType ("CURLcode")
    public static int curl_easy_perform(@NativeType ("CURL *") long curl) {
        return ncurl_easy_perform(curl_easy_perform, curl);
    }

    /**
     * The curl_easy_strerror function may be used to turn a CURLcode value
     * into the equivalent human-readable error string. This is useful
     * for printing meaningful error messages.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_easy_perform.html">documentation</a>.
     *
     * @param errornum The CURLcode
     * @return The string.
     */
    public static String curl_easy_strerror(@NativeType ("CURLcode") int errornum) {
        return ncurl_easy_strerror(Functions.curl_easy_strerror, errornum);
    }

    /**
     * Destroy the CURL handle.
     *
     * @param curl The curl handle destroy.
     */
    public static void curl_easy_cleanup(@NativeType ("CURL *") long curl) {
        ncurl_easy_cleanup(Functions.curl_easy_cleanup, curl);
    }

    /**
     * If the bound CURL library supports curl-impersonate features.
     *
     * @return If curl-impersonate is supported.
     */
    public static boolean isCurlImpersonateSupported() {
        return Functions.curl_easy_impersonate != Memory.NULL;
    }

    /**
     * Enable curl impersonate.
     *
     * @param curl            The CURL handle.
     * @param target          The impersonation target. E.g. "chrome110"
     * @param default_headers If the default headers should be applied. You probably want true here.
     */
    public static int curl_easy_impersonate(long curl, String target, boolean default_headers) {
        return ncurl_easy_impersonate(Functions.curl_easy_impersonate, curl, target, default_headers);
    }

    /**
     * Appends a string to a linked list. If no list exists, it will be created
     * first. Returns the new list, after appending.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_slist_append.html">documentation</a>.
     *
     * @param list The {@link curl_slist} handle. {@code null} to construct a new one.
     * @param data The String to append.
     * @return The list with the string appended.
     */
    @Nullable
    public static curl_slist curl_slist_append(@Nullable curl_slist list, String data) {
        long listPtr = list != null ? list.address : Memory.NULL;

        long ptr = ncurl_slist_append(Functions.curl_slist_append, listPtr, data);
        if (ptr == listPtr) {
            return list;
        }
        if (ptr == Memory.NULL) return null;
        return new curl_slist(ptr);
    }

    /**
     * Free a previously built {@link curl_slist}.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_slist_free_all.html">documentation</a>.
     *
     * @param list The list to free.
     */
    public static void curl_slist_free_all(@Nullable curl_slist list) {
        if (list == null) return;
        ncurl_slist_free_all(Functions.curl_slist_free_all, list.address);
    }

    /**
     * Create a mime context and return its handle. The curl parameter is the
     * target handle.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_mime_init.html">documentation</a>.
     *
     * @param curl The CURL handle.
     * @return The curl_mime handle.
     */
    @NativeType ("curl_mime *")
    public static long curl_mime_init(@NativeType ("CURL *") long curl) {
        return ncurl_mime_init(Functions.curl_mime_init, curl);
    }

    /**
     * release a mime handle and its substructures.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_mime_free.html">documentation</a>.
     *
     * @param mime The curl_mime handle.
     */
    public static void curl_mime_free(@NativeType ("curl_mime *") long mime) {
        ncurl_mime_free(Functions.curl_mime_free, mime);
    }

    /**
     * Append a new empty part to the given mime context and return a handle to
     * the created part.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_mime_addpart.html">documentation</a>.
     *
     * @param mime The curl_mime handle.
     * @return The new curl_mimepart handle.
     */
    @NativeType ("curl_mimepart *")
    public static long curl_mime_addpart(@NativeType ("curl_mime *") long mime) {
        return ncurl_mime_addpart(Functions.curl_mime_addpart, mime);
    }

    /**
     * Set mime/form part name.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_mime_name.html">documentation</a>.
     *
     * @param mime The curl_mime handle.
     * @param name The name.
     * @return The CURLcode response.
     */
    @NativeType ("CURLcode")
    public static int curl_mime_name(@NativeType ("curl_mime *") long mime, @Nullable String name) {
        return ncurl_mime_name(Functions.curl_mime_name, mime, name);
    }

    /**
     * Set mime part remote file name.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_mime_filename.html">documentation</a>.
     *
     * @param mime     The curl_mime handle.
     * @param filename The remote file name.
     * @return The CURLcode response.
     */
    @NativeType ("CURLcode")
    public static int curl_mime_filename(@NativeType ("curl_mime *") long mime, @Nullable String filename) {
        return ncurl_mime_filename(Functions.curl_mime_filename, mime, filename);
    }

    /**
     * Set mime part type.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_mime_type.html">documentation</a>.
     *
     * @param mime     The curl_mime handle.
     * @param mimetype The Content-Type for this part.
     * @return The CURLcode response.
     */
    @NativeType ("CURLcode")
    public static int curl_mime_type(@NativeType ("curl_mime *") long mime, String mimetype) {
        return ncurl_mime_type(Functions.curl_mime_type, mime, mimetype);
    }

    /**
     * Set mime part data source from memory data.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_mime_data.html">documentation</a>.
     *
     * @param mime The curl_mime handle.
     * @param data The data.
     * @return The CURLcode response.
     */
    @NativeType ("CURLcode")
    public static int curl_mime_data(@NativeType ("curl_mime *") long mime, byte[] data) {
        return ncurl_mime_data(Functions.curl_mime_filename, mime, data);
    }

    /**
     * Set mime part data source from callback function.
     * <p>
     * See the curl <a href="https://curl.se/libcurl/c/curl_mime_data_cb.html">documentation</a>.
     *
     * @param mime     The curl_mime handle.
     * @param datasize The size of the body.
     * @param readfunc The read function.
     * @return The CURLcode response.
     */
    @NativeType ("CURLcode")
    public static int curl_mime_data_cb(@NativeType ("curl_mime *") long mime, long datasize, CurlReadCallback readfunc) {
        return ncurl_mime_data_cb(Functions.curl_mime_data_cb, mime, datasize, readfunc.getFunctionAddress(), Memory.NULL, Memory.NULL, Memory.NULL);
    }

    /**
     * Class to hold all the libCURL function pointers.
     * <p>
     * Due to class loading rules, this will only be loaded (and thus, libcurl loaded), when
     * a curl function is called.
     */
    public static final class Functions {

        private Functions() { }

        private static final Library CURL = getLbCURL();

        public static final long curl_version = CURL.getFunction("curl_version");
        public static final long curl_version_info = CURL.getFunction("curl_version_info");
        public static final long curl_global_init = CURL.getFunction("curl_global_init");
        public static final long curl_global_cleanup = CURL.getFunction("curl_global_cleanup");
        public static final long curl_easy_init = CURL.getFunction("curl_easy_init");
        public static final long curl_easy_reset = CURL.getFunction("curl_easy_reset");
        public static final long curl_easy_perform = CURL.getFunction("curl_easy_perform");
        public static final long curl_easy_setopt = CURL.getFunction("curl_easy_setopt");
        public static final long curl_easy_getinfo = CURL.getFunction("curl_easy_getinfo");
        public static final long curl_easy_strerror = CURL.getFunction("curl_easy_strerror");
        public static final long curl_easy_cleanup = CURL.getFunction("curl_easy_cleanup");
        public static final long curl_easy_impersonate = CURL.getOptionalFunction("curl_easy_impersonate");
        public static final long curl_slist_append = CURL.getFunction("curl_slist_append");
        public static final long curl_slist_free_all = CURL.getFunction("curl_slist_free_all");
        public static final long curl_mime_init = CURL.getFunction("curl_mime_init");
        public static final long curl_mime_free = CURL.getFunction("curl_mime_free");
        public static final long curl_mime_addpart = CURL.getFunction("curl_mime_addpart");
        public static final long curl_mime_name = CURL.getFunction("curl_mime_name");
        public static final long curl_mime_filename = CURL.getFunction("curl_mime_filename");
        public static final long curl_mime_type = CURL.getFunction("curl_mime_type");
        public static final long curl_mime_data = CURL.getFunction("curl_mime_data");
        public static final long curl_mime_data_cb = CURL.getFunction("curl_mime_data_cb");

        // @formatter:off
        public static native String ncurl_version(long func);
        public static native long ncurl_version_info(long func);
        public static native int ncurl_global_init(long func, long flags);
        public static native void ncurl_global_cleanup(long func);
        public static native long ncurl_easy_init(long func);
        public static native void ncurl_easy_reset(long func, long curl);
        public static native int ncurl_easy_perform(long func, long curl);
        public static native int ncurl_easy_setopt(long func, long curl, int opt, long value);
        public static native int ncurl_easy_setopt(long func, long curl, int opt, String value);
        public static native int ncurl_easy_getinfo(long func, long curl, int info, long value);
        public static native String ncurl_easy_strerror(long func, int code);
        public static native void ncurl_easy_cleanup(long func, long curl);
        public static native int ncurl_easy_impersonate(long func, long curl, String target, boolean defaultHeaders);
        public static native long ncurl_slist_append(long func, long list, String data);
        public static native void ncurl_slist_free_all(long func, long list);
        public static native long ncurl_mime_init(long func, long curl);
        public static native void ncurl_mime_free(long func, long mime);
        public static native long ncurl_mime_addpart(long func, long mime);
        public static native int ncurl_mime_name(long func, long part, String name);
        public static native int ncurl_mime_filename(long func, long part, String fileName);
        public static native int ncurl_mime_type(long func, long part, String mimeType);
        public static native int ncurl_mime_data(long func, long part, byte[] data);
        public static native int ncurl_mime_data_cb(long func, long part, long dataSize, long readFunc, long seekFunc, long freeFunc, long userData);
        // @formatter:on
    }
}
