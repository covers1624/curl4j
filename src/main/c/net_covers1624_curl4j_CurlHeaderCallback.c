#include <stdlib.h>
#include <string.h>
#include "net_covers1624_curl4j_CurlHeaderCallback.h"
#include "ffi.h"
#include "utils.h"

static jmethodID callback;

static void header_callback(ffi_cif *, void *ret, void **args, void *user_data) {
    JNIEnv *env = getEnv();
    char *ptr = *(char **) args[0];
    jsize size = *(jsize *) args[1];
    jsize nmemb = *(jsize *) args[2];
    void *userdata = *(void **) args[3];

    // We need to add null termination to the string
    // for the JNI call.. Thanks java.
    jsize rs = size * nmemb;
    char *str = malloc(rs + 1);
    memcpy(str, ptr, rs);
    str[rs] = '\0';

    jstring jstr = (*env)->NewStringUTF(env, str);
    free(str);

    (*env)->CallVoidMethod(env, user_data, callback, jstr, userdata);
    *((jlong *) ret) = rs;

    // TODO exception handling when we support non-native threads.
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CurlHeaderCallback_ffi_1callback(JNIEnv *env, jclass, jobject method) {
    callback = (*env)->FromReflectedMethod(env, method);
    return (jlong) &header_callback;
}
