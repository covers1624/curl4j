#include "net_covers1624_curl4j_core_Callback.h"
#include "stdlib.h"
#include "ffi.h"
#include "utils.h"

static jmethodID callbackMethod;

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Callback_ffi_1type_1pointer(JNIEnv *env, jclass clazz) {
    return (jlong) &ffi_type_pointer;
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Callback_ffi_1type_1int(JNIEnv *env, jclass clazz) {
    return (jlong) &ffi_type_uint32;
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Callback_ffi_1type_1long(JNIEnv *env, jclass clazz) {
    return (jlong) &ffi_type_uint64;
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Callback_ffi_1cif_1alloc(JNIEnv *env, jclass clazz) {
    return (jlong) malloc(sizeof(ffi_cif));
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_core_Callback_ffi_1cif_1free(JNIEnv *env, jclass clazz, jlong cif) {
    free((void *) cif);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_Callback_ffi_1prep_1cif(JNIEnv *env, jclass clazz, jlong cif, jlong rtype, jlongArray atypes) {
    jint nargs = (*env)->GetArrayLength(env, atypes);
    jlong *atypesPtr = (*env)->GetLongArrayElements(env, atypes, NULL);
    int ret = ffi_prep_cif((ffi_cif *) cif, FFI_DEFAULT_ABI, nargs, (ffi_type *) rtype, (ffi_type **) atypesPtr);
    return ret;
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Callback_ffi_1closure_1alloc(JNIEnv *env, jclass clazz, jlong code) {
    return (jlong) ffi_closure_alloc(sizeof(ffi_closure), (void *) code);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_Callback_ffi_1prep_1closure_1loc(JNIEnv *env, jclass clazz, jlong closure, jlong cif, jlong callback, jlong data, jlong code) {
    return ffi_prep_closure_loc((ffi_closure *) closure, (ffi_cif *) cif, (void *) callback, (void *) data, (void *) code);
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_core_Callback_ffi_1closure_1free(JNIEnv *env, jclass clazz, jlong closure) {
    ffi_closure_free((void *) closure);
}

static void ffi_callback(ffi_cif *cif, void *ret, void **args, void *user_data) {
    JNIEnv *env = getEnv();
    (*env)->CallVoidMethod(env, user_data, callbackMethod, ret, args);
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_core_Callback_ffi_1callback(JNIEnv *env, jclass clazz, jobject method) {
    callbackMethod = (*env)->FromReflectedMethod(env, method);
    return (jlong) &ffi_callback;
}
