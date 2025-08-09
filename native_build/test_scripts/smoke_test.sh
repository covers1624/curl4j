#!/bin/sh

set -e

cat > smoke.c << EOF
#include <stdio.h>

// #include <curl/curl.h>

static void *st_dlopen(const char *path);

static void *st_dlsym(void *handle, const char *name);

static void st_dlclose(void *handle);

struct curl_version_info_data {
    int age;
    const char *version;
    unsigned int version_num;
    const char *host;
    int features;
    const char *ssl_version;
    long ssl_version_num;
    const char *libz_version;
    const char *const *protocols;
    const char *ares;
    int ares_num;
    const char *libidn;
    int iconv_ver_num;
    const char *libssh_version;
    unsigned int brotli_ver_num;
    const char *brotli_version;
    unsigned int nghttp2_ver_num;
    const char *nghttp2_version;
    const char *quic_version;
    const char *cainfo;
    const char *capath;
    unsigned int zstd_ver_num;
    const char *zstd_version;
    const char *hyper_version;
    const char *gsasl_version;
    const char *const *feature_names;
};

#ifndef WIN32

#include <dlfcn.h>

static void *st_dlopen(const char *path) {
    void *handle = dlopen(path, RTLD_NOW);
    if (handle == NULL) {
        fprintf(stderr, "Failed to open. %s\n", dlerror());
        return NULL;
    }
    return handle;
}

static void *st_dlsym(void *handle, const char *name) {
    return dlsym(handle, name);
}

static void st_dlclose(void *handle) {
    dlclose(handle);
}

#else

#include "libloaderapi.h"
#include "errhandlingapi.h"

static void *st_dlopen(const char *path) {
    void *handle = (void *) LoadLibraryA(path);
    if (handle == NULL) {
        fprintf(stderr, "Failed to open. %d\n", GetLastError());
    }
    return handle;
}

static void *st_dlsym(void *handle, const char *name) {
    return GetProcAddress((HMODULE) handle, name);
}

static void st_dlclose(void *handle) {
    FreeLibrary((HMODULE) handle);
}

#endif

int main(int argc, char **argv) {
    if (argc != 2) {
        fprintf(stderr, "Usage: %s <lib>\n", argv[0]);
        return 1;
    }

    fprintf(stderr, "Opening: %s\n", argv[1]);
    void *handle = st_dlopen(argv[1]);
    if (!handle) {
        return 1;
    }

    void *f_curl_version = st_dlsym(handle, "curl_version");
    if (!f_curl_version) {
        fprintf(stderr, "Unable to get curl_version.\n");
        return 1;
    }

    void *f_curl_version_info = st_dlsym(handle, "curl_version_info");
    if (!f_curl_version_info) {
        fprintf(stderr, "Unable to get curl_version_info.\n");
        return 1;
    }

    char *curl_version = ((char *(*)(void)) f_curl_version)();
    struct curl_version_info_data *data = ((struct curl_version_info_data *(*)(void)) f_curl_version_info)();

    fprintf(stderr, "%s\n", curl_version);
    fprintf(stderr, "Protocols:");
    for (const char *const *p = data->protocols; *p != NULL; p++) {
        fprintf(stderr, " %s", *p);
    }
    fprintf(stderr, "\n");

    fprintf(stderr, "Features:");
    for (const char *const *p = data->feature_names; *p != NULL; p++) {
        fprintf(stderr, " %s", *p);
    }
    fprintf(stderr, "\n");

    st_dlclose(handle);
    return 0;
}

typedef struct curl_version_info_data curl_version_info_data;
EOF

case "$PLATFORM" in
  windows_x64)
    x86_64-w64-mingw32-gcc smoke.c -o smoke.exe

    rc=0
    WINEDEBUG=-all wine smoke.exe "$(printf '%s' "$1" | tr '/' '\\')" || rc=$?
    WINEDEBUG=-all wineserver -w || :
    exit "$rc"
    ;;
  macos_arm64)
    clang --target=aarch64-apple-darwin20.6.0 smoke.c -o smoke
    ./smoke "$1"
    ;;
  macos_x64)
    clang --target=x86_64-apple-darwin20.6.0 smoke.c -o smoke
    ./smoke "$1"
    ;;
  linux_*)
    gcc smoke.c -o smoke
    ./smoke "$1"
    ;;
  "")
    echo "PLATFORM env var must be set."
    exit 1
    ;;
  *)
    echo "Unknown platform: $PLATFORM"
    exit 1
    ;;
esac

rm -rf smoke.c || true
rm -rf smoke || true
rm -rf smoke.exe || true

