#include "net_covers1624_curl4j_CurlReadCallback.h"
#include "ffi.h"
#include "utils.h"

static jmethodID callback;

static void read_callback(ffi_cif *, void *ret, void **args, void *user_data) {
    JNIEnv *env = getEnv();
    *((jlong *) ret) = (*env)->CallLongMethod(
            env,
            user_data,
            callback,
            *(jlong *) args[0], *(jlong *) args[1], *(jlong *) args[2], *(jlong *) args[3]
    );
    // TODO exception handling when we support non-native threads.
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CurlReadCallback_ffi_1callback(JNIEnv *env, jclass, jobject method) {
    callback = (*env)->FromReflectedMethod(env, method);
    return (jlong) &read_callback;
}
