#include "net_covers1624_curl4j_core_NativeTypes.h"

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_NativeTypes_pointerSize(JNIEnv *env, jclass clazz) {
    return sizeof(void *);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_NativeTypes_getIntSize(JNIEnv *env, jclass clazz) {
    return sizeof(int);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_NativeTypes_getLongSize(JNIEnv *env, jclass clazz) {
    return sizeof(long);
}

JNIEXPORT jint JNICALL Java_net_covers1624_curl4j_core_NativeTypes_getSizeTSize(JNIEnv *env, jclass clazz) {
    return sizeof(size_t);
}
