# Examples

## Simple usage
In the following example we use a simple in-memory callback for the curl output buffer, various other
helpers exist for common curl functions/resources.

```java
import net.covers1624.curl4j.CURL;
import net.covers1624.curl4j.util.MemoryCurlOutput;

public class EggSample {

    public static void main(String[] args) throws Throwable {
        // Call this once to initialize curl's globals.
        CURL.curl_global_init(CURL.CURL_GLOBAL_DEFAULT);
        // Call to allocate a new curl handle.
        long curl = CURL.curl_easy_init();

        try (MemoryCurlOutput output = MemoryCurlOutput.create()) {
            CURL.curl_easy_setopt(curl, CURL.CURLOPT_WRITEFUNCTION, output.callback());

            CURL.curl_easy_setopt(curl, CURL.CURLOPT_URL, "https://httpbin.org/anything");
            CURL.curl_easy_setopt(curl, CURL.CURLOPT_CUSTOMREQUEST, "GET");

            int result = CURL.curl_easy_perform(curl);
            if (result != CURL.CURLE_OK) throw new RuntimeException("CURL Failed: " + CURL.curl_easy_strerror(result));

            long responseCode = CURL.curl_easy_getinfo_long(curl, CURL.CURLINFO_RESPONSE_CODE);
            System.out.println("Http code: " + responseCode);
            System.out.println("Body:\n" + output.string());
        }

        // Handles should be re-used, but for the sake of this example, clean it up.
        CURL.curl_easy_cleanup(curl);
        // Cleanup curl globals, only do this when you finished with curl.
        CURL.curl_global_cleanup();
    }
}
```

## Native libraries

The curl4j JNI bindings may be overridden with the `net.covers1624.curl4j.libcurl4j.name` system property.

The libcurl library may be overridden with the `net.covers1624.curl4j.libcurl.name` system property or
via the `CURL.setLibCurlName` function. This may be set any time prior to any other CURL operation being performed.
If both the system property and the function override are used, the function override takes priority. If the native
library has already been loaded (by invoking a CURL operation), setting the name will nave no affect.

All override properties and functions support custom library names and absolute paths.

curl4j will automatically attempt to locate and extract its dependencies from the classpath, this may
be disabled with the `net.covers1624.curl4j.no_embedded` system property.

curl4j can be configured to load its native libraries from a specific directory. This may be set with the
`net.covers1624.curl4j.lib_path` system property. This is expected to point to a folder containing the contents
of the `/META-INF/natives` directory inside the `libcurl` maven artifact. This is provided for those who
have custom requirements, and would like to lock down where curl4j loads natives from.

NOTE: All platforms will automatically have `lib` prefixed to the library name and their platform specific extension
appended prior to loading. The only exception to this is absolute paths, they are not modified.

curl4j will attempt to load in the following order:
- if `libname` is an absolute path, this is loaded directly.
- if the `lib_path` system property is set, attempt to find the library inside. 
- if `no_embedded` is not set, attempt to find the library on the classpath.
- try and just load the library name using the default semantics of the operating system.

## curl impersonate

curl4j supports [curl-impersonate](https://github.com/lwthiker/curl-impersonate).

In order to use curl-impersonate, you will need to override the library name and/or path as described above.

curl4j provides the `CURL.isCurlImpersonateSupported()` function for testing if curl-impersonate specific functions
are available.

In the following example we will show how to override the curl library with the absolute path of curl-impersonate,
and trigger a curl operation impersonating chrome99.

```java
import net.covers1624.curl4j.CURL;
import net.covers1624.curl4j.util.MemoryCurlOutput;

public class EggSample {

    public static void main(String[] args) throws Throwable {
        // Override the curl library with libcurl-impersonate chrome.
        CURL.setLibCurlName("./libcurl-impersonate-v0.5.4/libcurl-impersonate-chrome.so");

        // Call this once to initialize curl's globals.
        CURL.curl_global_init(CURL.CURL_GLOBAL_DEFAULT);
        // Call to allocate a new curl handle.
        long curl = CURL.curl_easy_init();

        try (MemoryCurlOutput output = MemoryCurlOutput.create()) {

            // Enable curl-impersonate chrome99.
            // NOTE: Like all curl operations, this state is reset when curl_easy_reset is called.
            CURL.curl_easy_impersonate(curl, "chrome99", true);

            CURL.curl_easy_setopt(curl, CURL.CURLOPT_WRITEFUNCTION, output.callback());

            CURL.curl_easy_setopt(curl, CURL.CURLOPT_URL, "https://httpbin.org/anything");
            CURL.curl_easy_setopt(curl, CURL.CURLOPT_CUSTOMREQUEST, "GET");

            int result = CURL.curl_easy_perform(curl);
            if (result != CURL.CURLE_OK) throw new RuntimeException("CURL Failed: " + CURL.curl_easy_strerror(result));

            long responseCode = CURL.curl_easy_getinfo_long(curl, CURL.CURLINFO_RESPONSE_CODE);
            System.out.println("Http code: " + responseCode);
            System.out.println("Body:\n" + output.string());
        }

        // Handles should be re-used, but for the sake of this example, clean it up.
        CURL.curl_easy_cleanup(curl);
        // Cleanup curl globals, only do this when you finished with curl.
        CURL.curl_global_cleanup();
    }
}
```
