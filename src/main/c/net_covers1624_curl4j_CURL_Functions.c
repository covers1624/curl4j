#include "stdint.h"
#include "net_covers1624_curl4j_CURL_Functions.h"
#include "curl/curl.h"

JNIEXPORT jstring JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1version(JNIEnv *env, jclass, jlong func) {
    return (*env)->NewStringUTF(env, ((const char *(*)(void)) (uintptr_t) func)());
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1version_1info(JNIEnv *, jclass, jlong func) {
    return (jlong) ((uintptr_t (*)(CURLversion)) (uintptr_t) func)(CURLVERSION_NOW);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1global_1init(JNIEnv *, jclass, jlong func, jlong flags) {
    return ((CURLcode (*)(long)) (uintptr_t) func)(flags);
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1global_1cleanup(JNIEnv *, jclass, jlong func) {
    ((void (*)(void)) (uintptr_t) func)();
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1init(JNIEnv *, jclass, jlong func) {
    return (jlong) ((CURL *(*)(void)) (uintptr_t) func)();
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1reset(JNIEnv *, jclass, jlong func, jlong curl) {
    ((void (*)(CURL *)) (uintptr_t) func)((CURL *) curl);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1perform(JNIEnv *, jclass, jlong func, jlong curl) {
    return ((CURLcode (*)(CURL *)) (uintptr_t) func)((CURL *) curl);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1setopt__JJIJ(JNIEnv *, jclass, jlong func, jlong curl, jint opt, jlong value) {
    return ((CURLcode (*)(CURL *, int, uintptr_t)) (uintptr_t) func)((CURL *) curl, opt, (uintptr_t) value);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1setopt__JJILjava_lang_String_2(JNIEnv *env, jclass, jlong func, jlong curl, jint opt, jstring value) {
    const char *valStr = (*env)->GetStringUTFChars(env, value, NULL);
    CURLcode ret = ((CURLcode (*)(CURL *, int, const char *)) (uintptr_t) func)((CURL *) curl, opt, valStr);
    (*env)->ReleaseStringUTFChars(env, value, valStr);
    return ret;
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1getinfo(JNIEnv *, jclass, jlong func, jlong curl, jint info, jlong value) {
    return ((CURLcode (*)(CURL *, int, uintptr_t)) (uintptr_t) func)((CURL *) curl, info, (uintptr_t) value);
}

JNIEXPORT jstring JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1strerror(JNIEnv *env, jclass, jlong func, jint code) {
    return (*env)->NewStringUTF(env, ((const char *(*)(CURLcode)) (uintptr_t) func)(code));
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1cleanup(JNIEnv *, jclass, jlong func, jlong curl) {
    ((void (*)(CURL *)) (uintptr_t) func)((CURL *) curl);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1easy_1impersonate(JNIEnv *env, jclass, jlong func, jlong curl, jstring target, jboolean default_headers) {
    const char *targetStr = (*env)->GetStringUTFChars(env, target, NULL);
    CURLcode ret = ((CURLcode (*)(CURL *, const char *, int)) (uintptr_t) func)((CURL *) curl, targetStr, default_headers);
    (*env)->ReleaseStringUTFChars(env, target, targetStr);
    return ret;
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1slist_1append(JNIEnv *env, jclass, jlong func, jlong list, jstring string) {
    const char *str = (*env)->GetStringUTFChars(env, string, NULL);
    struct curl_slist *ret = ((struct curl_slist *(*)(struct curl_slist *, const char *)) (uintptr_t) func)((struct curl_slist *) list, str);
    (*env)->ReleaseStringUTFChars(env, string, str);
    return (jlong) (uintptr_t) ret;
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1slist_1free_1all(JNIEnv *, jclass, jlong func, jlong list) {
    ((void (*)(struct curl_slist *)) (uintptr_t) func)((struct curl_slist *) list);
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1init(JNIEnv *, jclass, jlong func, jlong curl) {
    return (jlong) (uintptr_t) ((curl_mime *(*)(CURL *)) (uintptr_t) func)((CURL *) curl);
}

JNIEXPORT void JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1free(JNIEnv *, jclass, jlong func, jlong mime) {
    ((void (*)(curl_mime *)) ((uintptr_t) func))((curl_mime *) mime);
}

JNIEXPORT jlong JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1addpart(JNIEnv *, jclass, jlong func, jlong mime) {
    return (jlong) (uintptr_t) ((curl_mimepart *(*)(curl_mime *)) (uintptr_t) func)((curl_mime *) mime);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1name(JNIEnv *env, jclass, jlong func, jlong part, jstring name) {
    const char *str = (*env)->GetStringUTFChars(env, name, NULL);
    int ret = ((int (*)(curl_mimepart *, const char *)) (uintptr_t) func)((curl_mimepart *) part, str);
    (*env)->ReleaseStringUTFChars(env, name, str);
    return ret;
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1filename(JNIEnv *env, jclass, jlong func, jlong part, jstring name) {
    const char *str = (*env)->GetStringUTFChars(env, name, NULL);
    int ret = ((int (*)(curl_mimepart *, const char *)) (uintptr_t) func)((curl_mimepart *) part, str);
    (*env)->ReleaseStringUTFChars(env, name, str);
    return ret;
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1type(JNIEnv *env, jclass, jlong func, jlong part, jstring name) {
    const char *str = (*env)->GetStringUTFChars(env, name, NULL);
    int ret = ((int (*)(curl_mimepart *, const char *)) (uintptr_t) func)((curl_mimepart *) part, str);
    (*env)->ReleaseStringUTFChars(env, name, str);
    return ret;
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1data(JNIEnv *env, jclass, jlong func, jlong part, jbyteArray data) {
    int len = (*env)->GetArrayLength(env, data);
    jbyte *dataPtr = (*env)->GetByteArrayElements(env, data, NULL);
    int ret = ((int (*)(curl_mimepart *, const char *, size_t)) (uintptr_t) func)((curl_mimepart *) part, (const char *) dataPtr, len);
    (*env)->ReleaseByteArrayElements(env, data, dataPtr, JNI_ABORT);
    return ret;
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_CURL_00024Functions_ncurl_1mime_1data_1cb(JNIEnv *, jclass, jlong func, jlong part, jlong dataSize, jlong readFunc, jlong seekFunc, jlong freeFunc, jlong userData) {
    return ((int (*)(curl_mimepart *, curl_off_t, uintptr_t, uintptr_t, uintptr_t, uintptr_t)) (uintptr_t) func)((curl_mimepart *) part, dataSize, (uintptr_t) readFunc, (uintptr_t) seekFunc, (uintptr_t) freeFunc, (uintptr_t) userData);
}

