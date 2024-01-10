#include "stdint.h"
#include "net_covers1624_curl4j_CURL_Functions.h"

JNIEXPORT jstring JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1version(JNIEnv *env, jclass clazz, jlong func) {
    return (*env)->NewStringUTF(env, ((const char *(*)(void)) (uintptr_t) func)());
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1version_1info(JNIEnv *env, jclass clazz, jlong func) {
    return (jlong) ((uintptr_t (*)(int)) (uintptr_t) func)(10); // TODO, give this argument to Java.
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1global_1init(JNIEnv *env, jclass clazz, jlong func, jlong flags) {
    return ((int (*)(long)) (uintptr_t) func)(flags);
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1global_1cleanup(JNIEnv *env, jclass clazz, jlong func) {
    ((void (*)(void)) (uintptr_t) func)();
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1init(JNIEnv *env, jclass clazz, jlong func) {
    return (jlong) ((uintptr_t(*)(void)) (uintptr_t) func)();
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1reset(JNIEnv *env, jclass clazz, jlong func, jlong curl) {
    ((void (*)(uintptr_t)) (uintptr_t) func)((uintptr_t) curl);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1perform(JNIEnv *env, jclass clazz, jlong func, jlong curl) {
    return ((int (*)(uintptr_t)) (uintptr_t) func)((uintptr_t) curl);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1pause(JNIEnv *env, jclass clazz, jlong func, jlong curl, jint bitmask) {
    return ((int (*)(uintptr_t, int)) (uintptr_t) func)((uintptr_t) curl, bitmask);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1setopt__JJIJ(JNIEnv *env, jclass clazz, jlong func, jlong curl, jint opt, jlong value) {
    return ((int (*)(uintptr_t, int, uintptr_t)) (uintptr_t) func)((uintptr_t) curl, opt, (uintptr_t) value);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1setopt__JJILjava_lang_String_2(JNIEnv *env, jclass clazz, jlong func, jlong curl, jint opt, jstring value) {
    const char *valStr = (*env)->GetStringUTFChars(env, value, NULL);
    int ret = ((int (*)(uintptr_t, int, const char *)) (uintptr_t) func)((uintptr_t) curl, opt, valStr);
    (*env)->ReleaseStringUTFChars(env, value, valStr);
    return ret;
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1getinfo(JNIEnv *env, jclass clazz, jlong func, jlong curl, jint info, jlong value) {
    return ((int (*)(uintptr_t, int, uintptr_t)) (uintptr_t) func)((uintptr_t) curl, info, (uintptr_t) value);
}

JNIEXPORT jstring JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1strerror(JNIEnv *env, jclass clazz, jlong func, jint code) {
    return (*env)->NewStringUTF(env, ((const char *(*)(int)) (uintptr_t) func)(code));
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1cleanup(JNIEnv *env, jclass clazz, jlong func, jlong curl) {
    ((void (*)(uintptr_t)) (uintptr_t) func)((uintptr_t) curl);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1impersonate(JNIEnv *env, jclass clazz, jlong func, jlong curl, jstring target, jboolean default_headers) {
    const char *targetStr = (*env)->GetStringUTFChars(env, target, NULL);
    int ret = ((int (*)(uintptr_t, const char *, int)) (uintptr_t) func)((uintptr_t) curl, targetStr, default_headers);
    (*env)->ReleaseStringUTFChars(env, target, targetStr);
    return ret;
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1slist_1append(JNIEnv *env, jclass clazz, jlong func, jlong list, jstring string) {
    const char *str = (*env)->GetStringUTFChars(env, string, NULL);
    uintptr_t ret = ((uintptr_t(*)(uintptr_t, const char *)) (uintptr_t) func)((uintptr_t) list, str);
    (*env)->ReleaseStringUTFChars(env, string, str);
    return (jlong) (uintptr_t) ret;
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1slist_1free_1all(JNIEnv *env, jclass clazz, jlong func, jlong list) {
    ((void (*)(uintptr_t)) (uintptr_t) func)((uintptr_t) list);
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1init(JNIEnv *env, jclass clazz, jlong func, jlong curl) {
    return (jlong) (uintptr_t) ((uintptr_t(*)(uintptr_t)) (uintptr_t) func)((uintptr_t) curl);
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1free(JNIEnv *env, jclass clazz, jlong func, jlong mime) {
    ((void (*)(uintptr_t)) ((uintptr_t) func))((uintptr_t) mime);
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1addpart(JNIEnv *env, jclass clazz, jlong func, jlong mime) {
    return (jlong) (uintptr_t) ((uintptr_t(*)(uintptr_t)) (uintptr_t) func)((uintptr_t) mime);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1name(JNIEnv *env, jclass clazz, jlong func, jlong part, jstring name) {
    const char *str = (*env)->GetStringUTFChars(env, name, NULL);
    int ret = ((int (*)(uintptr_t, const char *)) (uintptr_t) func)((uintptr_t) part, str);
    (*env)->ReleaseStringUTFChars(env, name, str);
    return ret;
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1filename(JNIEnv *env, jclass clazz, jlong func, jlong part, jstring name) {
    const char *str = (*env)->GetStringUTFChars(env, name, NULL);
    int ret = ((int (*)(uintptr_t, const char *)) (uintptr_t) func)((uintptr_t) part, str);
    (*env)->ReleaseStringUTFChars(env, name, str);
    return ret;
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1type(JNIEnv *env, jclass clazz, jlong func, jlong part, jstring name) {
    const char *str = (*env)->GetStringUTFChars(env, name, NULL);
    int ret = ((int (*)(uintptr_t, const char *)) (uintptr_t) func)((uintptr_t) part, str);
    (*env)->ReleaseStringUTFChars(env, name, str);
    return ret;
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1data(JNIEnv *env, jclass clazz, jlong func, jlong part, jbyteArray data) {
    int len = (*env)->GetArrayLength(env, data);
    jbyte *dataPtr = (*env)->GetByteArrayElements(env, data, NULL);
    int ret = ((int (*)(uintptr_t, const char *, size_t)) (uintptr_t) func)((uintptr_t) part, (const char *) dataPtr, len);
    (*env)->ReleaseByteArrayElements(env, data, dataPtr, JNI_ABORT);
    return ret;
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1data_1cb(JNIEnv * env, jclass clazz, jlong func, jlong part, jlong dataSize, jlong readFunc, jlong seekFunc, jlong freeFunc, jlong userData) {
    return ((int (*)(uintptr_t, long, uintptr_t, uintptr_t, uintptr_t, uintptr_t)) (uintptr_t) func)((uintptr_t) part, dataSize, (uintptr_t) readFunc, (uintptr_t) seekFunc, (uintptr_t) freeFunc, (uintptr_t) userData);
}
