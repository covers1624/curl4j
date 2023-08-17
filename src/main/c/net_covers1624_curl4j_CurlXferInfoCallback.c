#include "net_covers1624_curl4j_CurlXferInfoCallback.h"
#include "ffi.h"
#include "utils.h"

static jmethodID callback;

static void callback_handler(ffi_cif *, void *ret, void **args, void *user_data) {
    JNIEnv *env = getEnv();
    *((jint *) ret) = (*env)->CallIntMethod(env, user_data, callback, (jlong) args[0], (jlong) args[1], (jlong) args[2], (jlong) args[3], (jlong) args[4]);
    // TODO exception handling when we support non-native threads.
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CurlXferInfoCallback_ffi_1callback(JNIEnv *env, jclass, jobject method) {
    callback = (*env)->FromReflectedMethod(env, method);
    return (jlong) &callback_handler;
}
